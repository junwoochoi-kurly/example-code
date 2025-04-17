package com.example.demo.completablefuture;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

/**
 * 2025-04-16T22:57:31.464+09:00  INFO 8367 --- [nio-8080-exec-3] c.e.d.c.ProductControllerV1              : getProductSync
 * 2025-04-16T22:57:31.466+09:00  INFO 8367 --- [onPool-worker-1] c.e.d.c.ProductControllerV1              : fetchMember
 * 2025-04-16T22:57:31.466+09:00  INFO 8367 --- [onPool-worker-2] c.e.d.c.ProductControllerV1              : fetchProduct
 * 2025-04-16T22:57:31.466+09:00  INFO 8367 --- [onPool-worker-3] c.e.d.c.ProductControllerV1              : fetchBrand
 * 2025-04-16T22:57:36.469+09:00  INFO 8367 --- [nio-8080-exec-3] c.e.d.c.ProductControllerV1              : end getProductSync
 * */
@RestController
public class ProductControllerV1 {

    private static final Logger log = LoggerFactory.getLogger(ProductControllerV1.class);

    @GetMapping("/sync")
    public ProductResponse getProductSync() {
        log.info("getProductSync");
        CompletableFuture<String> memberFuture = fetchMember();
        CompletableFuture<String> productFuture = fetchProduct();
        CompletableFuture<String> brandFuture = fetchBrand();

        try {
            // 모든 future 가 끝날 때까지 블로킹 (서블릿 스레드 점유)
            CompletableFuture.allOf(memberFuture, productFuture, brandFuture).join();
        } catch (Exception e) {
            System.err.println("비동기 데이터 처리 중 오류 발생: " + e.getMessage());
        }


        ProductResponse productResponse = new ProductResponse(
                memberFuture.join(),   // 이제는 이미 완료되었으므로 바로 꺼냄
                productFuture.join(),
                brandFuture.join()
        );
        log.info("end getProductSync");
        return productResponse;
    }

    private CompletableFuture<String> fetchMember() {
        return CompletableFuture.supplyAsync(() -> {
            log.info("fetchMember");
            sleep(5000);
            return "회원정보";
        });
    }

    private CompletableFuture<String> fetchProduct() {
        return CompletableFuture.supplyAsync(() -> {
            log.info("fetchProduct");
            sleep(5000);
            return "상품정보";
        });
    }

    private CompletableFuture<String> fetchBrand() {
        return CompletableFuture.supplyAsync(() -> {
            log.info("fetchBrand");
            sleep(5000);
            return "브랜드정보";
        });
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
    }

    public record ProductResponse(String member, String product, String brand) {}

}
