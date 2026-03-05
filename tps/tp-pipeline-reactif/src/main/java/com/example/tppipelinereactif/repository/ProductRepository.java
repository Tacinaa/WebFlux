package com.example.tppipelinereactif.repository;

import com.example.tppipelinereactif.model.Product;
//import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ProductRepository {

    private final Map<String, Product> products = new HashMap<>();

    private final Duration dbLatency = Duration.ofMillis(100);

    // bonus : taux d'erreur
    private final double errorRate;

    private final Map<String, Mono<Product>> productCache = new HashMap<>();

    public ProductRepository() {
        this(0.10);
    }

    public ProductRepository(double errorRate) {
        this.errorRate = errorRate;
        seedProducts();
    }

    private void seedProducts() {
            products.put("PROD001",
                    new Product("PROD001",
                            "Casque Logitech Pro X Lightspeed",
                            new BigDecimal("229.99"),
                            5,
                            "Électronique"));

            products.put("PROD002",
                    new Product("PROD002",
                            "Montre Connectée Sport",
                            new BigDecimal("179.90"),
                            0,
                            "Électronique"));

            products.put("PROD003",
                    new Product("PROD003",
                            "Skyr Nature 500g",
                            new BigDecimal("2.95"),
                            25,
                            "Alimentaire"));

            products.put("PROD004",
                    new Product("PROD004",
                            "Chaise Ergonomique Bureau",
                            new BigDecimal("249.00"),
                            3,
                            "Mobilier"));

            products.put("PROD005",
                    new Product("PROD005",
                            "Câble USB-C Haute Vitesse",
                            new BigDecimal("12.99"),
                            100,
                            "Électronique"));
    }

//    public Mono<Product> findById(String id) {
//        return Mono.defer(() -> maybeFail()
//                        .then(Mono.justOrEmpty(products.get(id))))
//                .delayElement(dbLatency);
//    }

    public Mono<Product> findById(String id) {
        if (id == null) return Mono.empty();

        // Bonus B
        return productCache.computeIfAbsent(id, key ->
                Mono.defer(() -> maybeFail()
                                .then(Mono.justOrEmpty(products.get(key))))
                        .delayElement(dbLatency)
                        .cache()
        );
    }

    public Mono<Integer> getStock(String productId) {
        return Mono.defer(() -> maybeFail()
                        .then(Mono.justOrEmpty(products.get(productId)))
                        .map(Product::getStock)
                        .defaultIfEmpty(0))
                .delayElement(dbLatency);
    }

    private Mono<Void> maybeFail() {
        return Mono.defer(() -> {
            double r = ThreadLocalRandom.current().nextDouble();
            if (r < errorRate) {
                return Mono.error(new RuntimeException("Erreur simulée"));
            }
            return Mono.empty();
        });
    }

//    public void putProduct(Product product) {
//        products.put(product.getId(), product);
//    }
}