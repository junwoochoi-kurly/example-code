package com.example.demo.circuitissue;

import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class BrokenCircuitDemo {

    private static final Logger log = Logger.getLogger(BrokenCircuitDemo.class.getName());

    static class BrokenCircuit {
        private final AtomicReference<String> state = new AtomicReference<>("OPEN");
        private final AtomicBoolean isOpenFlag = new AtomicBoolean(true);

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

            // 5) 최종 상태 출력
            brokenCircuit.printStatus();
            brokenCircuit.shutdown();
        }

        private final ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        private ScheduledFuture<?> transitionFuture;
        
        public ScheduledFuture<?> getTransitionFuture() {
            return transitionFuture;
        }
        // 자동 OPEN -> HALF_OPEN 전환 스케줄

        public void scheduleAutoTransition() {
            transitionFuture = scheduler.schedule(() -> {
                log.info("[T] Future started in " + Thread.currentThread().getName());
                try {
                    toHalfOpenState();
                } finally {
                    log.info("[T] Future finished in " + Thread.currentThread().getName());
                }
            }, 0, TimeUnit.MILLISECONDS);  // 바로 실행되게 0으로 설정
        }

        // Resilience4j OpenState.toHalfOpenState 느낌의 구조
        private synchronized void toHalfOpenState() {
            log.info("  toHalfOpenState() enter, isOpenFlag=" + isOpenFlag.get() + ", state=" + state.get() + ", thread=" + Thread.currentThread().getName());

            if (isOpenFlag.compareAndSet(true, false)) {
                log.info("  isOpenFlag set to false");

                try {
                    // preTransition 작업 가정
                    log.info("  doing preTransition work (sleep)");
                    Thread.sleep(1000); // <-- 여기서 interrupt 맞게 만들 것

                    state.set("HALF_OPEN");
                    log.info("  state changed to HALF_OPEN");
                } catch (InterruptedException e) {
                    log.warning("  >>> Interrupted in the middle of transition!: " + e.getMessage());
                    log.warning("  >>> current state=" + state.get() + ", isOpenFlag=" + isOpenFlag.get());
                    Thread.currentThread().interrupt();
                }
            } else {
                log.info("  isOpenFlag was already false, skipping transition");
            }
        }

        public void printStatus() {
            log.info("=== FINAL STATUS ===");
            log.info("state      = " + state.get());
            log.info("isOpenFlag = " + isOpenFlag.get());
        }

        public void shutdown() {
            scheduler.shutdownNow();
        }

    }
}
