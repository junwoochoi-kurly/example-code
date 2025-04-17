package com.example.demo.completablefuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 2025-04-16T22:58:47.035+09:00  INFO 9034 --- [nio-8080-exec-1] c.e.d.c.ProductControllerV2              : getProductAsync
 * 2025-04-16T22:58:47.036+09:00  INFO 9034 --- [onPool-worker-1] c.e.d.c.ProductControllerV2              : fetchMember
 * 2025-04-16T22:58:47.036+09:00  INFO 9034 --- [onPool-worker-2] c.e.d.c.ProductControllerV2              : fetchProduct
 * 2025-04-16T22:58:47.036+09:00  INFO 9034 --- [onPool-worker-3] c.e.d.c.ProductControllerV2              : fetchBrand
 * 2025-04-16T22:58:47.036+09:00  INFO 9034 --- [nio-8080-exec-1] c.e.d.c.ProductControllerV2              : end getProductAsync
 * 2025-04-16T22:58:52.040+09:00  INFO 9034 --- [onPool-worker-2] c.e.d.c.ProductControllerV2              : thenApply
 * */
@RestController
public class ProductControllerV2 {

    private static final Logger log = LoggerFactory.getLogger(ProductControllerV2.class);

    @GetMapping("/async")
    public CompletableFuture<ProductResponse> getProductAsync() {
        log.info("getProductAsync");
        CompletableFuture<String> memberFuture = fetchMember();
        CompletableFuture<String> productFuture = fetchProduct();
        CompletableFuture<String> brandFuture = fetchBrand();

        CompletableFuture<ProductResponse> productResponseCompletableFuture = CompletableFuture.allOf(memberFuture, productFuture, brandFuture)
                .thenApply(v -> {
                    log.info("thenApply");

                    return new ProductResponse(
                            memberFuture.join(),
                            productFuture.join(),
                            brandFuture.join()
                    );
                });

        log.info("end getProductAsync");
        return productResponseCompletableFuture;
    }

    @GetMapping("/async2")
    public ProductResponse getProductAsync2() throws ExecutionException, InterruptedException {
        log.info("getProductAsync");
        CompletableFuture<String> memberFuture = fetchMember();
        CompletableFuture<String> productFuture = fetchProduct();
        CompletableFuture<String> brandFuture = fetchBrand();

        CompletableFuture<ProductResponse> productResponseCompletableFuture = CompletableFuture.allOf(memberFuture, productFuture, brandFuture)
                .thenApply(v -> {
                    log.info("thenApply");

                    return new ProductResponse(
                            memberFuture.join(),  // join은 여기서 해도 서블릿 스레드는 이미 반환됨
                            productFuture.join(),
                            brandFuture.join()
                    );
                });

        ProductResponse productResponse = productResponseCompletableFuture.get();
        log.info("end getProductAsync");
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
