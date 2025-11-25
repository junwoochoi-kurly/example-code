package com.example.demo;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class BrokenCircuitStackOverFlowDemo {

    static class BrokenCircuit {
        private final AtomicReference<String> state = new AtomicReference<>("OPEN");
        private final AtomicBoolean isOpenFlag = new AtomicBoolean(true);

        private final ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        @Getter
        private ScheduledFuture<?> transitionFuture;

        public static void main(String[] args) throws Exception {
            BrokenCircuit brokenCircuit = new BrokenCircuit();

            // 1) 자동 전환 Future 실행
            brokenCircuit.scheduleAutoTransition();

            // 2) toHalfOpenState 안에서 sleep 중일 타이밍까지 잠깐 대기
            Thread.sleep(200);

            // 3) Future를 cancel(true) 해서 그 작업 스레드에 interrupt 날리기
            log.info("[M] Calling cancel(true) from main thread");
            brokenCircuit.getTransitionFuture().cancel(true);

            // 4) 작업이 정리될 시간 조금 줌
            Thread.sleep(500);

            // 5) 깨진 상태 확인
            brokenCircuit.printStatus();

            // 6) 이 상태에서 tryAcquire() 호출 → 무한 재귀 → StackOverflowError
            log.info("[M] Calling tryAcquire() to trigger StackOverflowError...");
            brokenCircuit.tryAcquire();
        }

        // 자동 OPEN -> HALF_OPEN 전환 스케줄
        public void scheduleAutoTransition() {
            transitionFuture = scheduler.schedule(() -> {
                log.info("[T] Future started in {}", Thread.currentThread().getName());
                try {
                    toHalfOpenState();
                } finally {
                    log.info("[T] Future finished in {}", Thread.currentThread().getName());
                }
            }, 0, TimeUnit.MILLISECONDS);  // 바로 실행되게 0으로 설정
        }

        // Resilience4j OpenState.toHalfOpenState 느낌의 구조
        private synchronized void toHalfOpenState() {
            log.info("  toHalfOpenState() enter, isOpenFlag={}, state={}, thread={}",
                    isOpenFlag.get(), state.get(), Thread.currentThread().getName());

            if (isOpenFlag.compareAndSet(true, false)) {
                log.info("  isOpenFlag set to false");

                try {
                    // preTransition 작업 가정
                    log.info("  doing preTransition work (sleep)");
                    Thread.sleep(1000); // <-- 여기서 interrupt 맞게 만들 것

                    state.set("HALF_OPEN");
                    log.info("  state changed to HALF_OPEN");
                } catch (InterruptedException e) {
                    log.warn("  >>> Interrupted in the middle of transition!", e);
                    log.warn("  >>> current state={}, isOpenFlag={}", state.get(), isOpenFlag.get());
                    Thread.currentThread().interrupt();
                }
            } else {
                log.info("  isOpenFlag was already false, skipping transition");
            }
        }

        /**
         * Resilience4j의 OpenState.tryAcquirePermission 느낌을 단순화한 버전
         * - state == OPEN + isOpenFlag == true  → toHalfOpenState() 시도
         * - state == OPEN + isOpenFlag == false → 재귀 호출 (무한 루프) → StackOverflowError
         */
        public void tryAcquire() {
            log.info("tryAcquire() called: state={}, isOpenFlag={}", state.get(), isOpenFlag.get());

            // OPEN이 아니면 그냥 반환 (정상적인 케이스)
            if (!"OPEN".equals(state.get())) {
                log.info("  -> state is not OPEN ({}), returning.", state.get());
                return;
            }

            // OPEN + isOpenFlag == true → 아직 한 번도 전환 시도 안 한 상태라고 가정
            if (isOpenFlag.get()) {
                log.info("  -> OPEN + isOpenFlag=true: calling toHalfOpenState()");
                toHalfOpenState();
                return;
            }

            // 여기로 오면: state == OPEN 이면서 isOpenFlag == false
            // = “반쯤만 전환된(Open인데 open 아니라고 찍힌) 깨진 상태”
            log.warn("  -> OPEN + isOpenFlag=false: inconsistent state detected, recursing...");
            // 무한 재귀 → 결국 StackOverflowError 발생
            tryAcquire();
        }

        public void printStatus() {
            log.info("=== FINAL STATUS ===");
            log.info("state      = {}", state.get());
            log.info("isOpenFlag = {}", isOpenFlag.get());
        }

        public void shutdown() {
            scheduler.shutdownNow();
        }
    }
}
