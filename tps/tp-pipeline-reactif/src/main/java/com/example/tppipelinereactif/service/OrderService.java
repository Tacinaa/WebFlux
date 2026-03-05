package com.example.tppipelinereactif.service;

import com.example.tppipelinereactif.exception.InvalidOrderException;
import com.example.tppipelinereactif.model.Order;
import com.example.tppipelinereactif.model.OrderRequest;
import com.example.tppipelinereactif.model.OrderStatus;
import com.example.tppipelinereactif.model.ProductWithPrice;
import com.example.tppipelinereactif.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final ProductRepository productRepository;

    public OrderService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Mono<Order> processOrder(OrderRequest request) {

        return Mono.justOrEmpty(request)
                .switchIfEmpty(Mono.error(new InvalidOrderException("La requête est nulle")))

                // 1) Valider
                .flatMap(req -> {
                    if (req.getCustomerId() == null || req.getCustomerId().isBlank()) {
                        return Mono.error(new InvalidOrderException("customerId est obligatoire"));
                    }
                    if (req.getProductIds() == null || req.getProductIds().isEmpty()) {
                        return Mono.error(new InvalidOrderException("productIds ne doit pas être vide"));
                    }
                    return Mono.just(req);
                })

                // 7) Logging
                .doOnNext(req -> log.info("[VALIDATED] customerId={}, nbIds={}",
                        req.getCustomerId(), req.getProductIds().size()))

                // 2) Convertir
                .flatMap(req -> {
                    List<String> cleanedIds = req.getProductIds().stream()
                            .filter(Objects::nonNull)
                            .map(String::trim)
                            .filter(id -> !id.isEmpty())
                            .limit(100)
                            .collect(Collectors.toList());

                    log.info("[IDS_READY] {} ids après nettoyage/limite", cleanedIds.size());

                    return Mono.just(new Object[]{req, cleanedIds});
                })

                // 3) Traitement produits
                .flatMap(tuple -> {
                    OrderRequest req = (OrderRequest) tuple[0];
                    @SuppressWarnings("unchecked")
                    List<String> cleanedIds = (List<String>) tuple[1];

                    return Flux.fromIterable(cleanedIds)
                            .doOnNext(id -> log.info("[ID_OK] {}", id))

                            .flatMap(id ->
                                    productRepository.findById(id)
                                            .doOnNext(p -> log.info("[FOUND] {} - {}", p.getId(), p.getName()))
                                            .switchIfEmpty(Mono.fromRunnable(() ->
                                                    log.warn("[NOT_FOUND] id={} -> ignoré", id)
                                            ).then(Mono.empty()))
                                            .onErrorResume(e -> {
                                                log.error("[REPO_ERROR] id={} -> ignoré ({})", id, e.toString());
                                                return Mono.empty();
                                            })
                            )

                            .flatMap(product ->
                                    productRepository.getStock(product.getId())
                                            .doOnNext(stock -> log.info("[STOCK] {} = {}", product.getId(), stock))
                                            .filter(stock -> stock > 0)
                                            .map(stock -> product)
                            )

                            // 4) Réductions
                            .map(product -> {
                                int discount = "Électronique".equals(product.getCategory()) ? 10 : 5;

                                BigDecimal original = product.getPrice();

                                BigDecimal finalPrice = original
                                        .multiply(BigDecimal.valueOf(100 - discount))
                                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                                log.info("[DISCOUNT] {} cat={} {}% : {} -> {}",
                                        product.getId(),
                                        product.getCategory(),
                                        discount,
                                        original,
                                        finalPrice
                                );

                                return new ProductWithPrice(product, original, discount, finalPrice);
                            })

                            .collectList()
                            .doOnNext(list -> log.info("[PRODUCTS_READY] {} produits retenus", list.size()))

                            // 5) Combiner résultats
                            .map(productWithPrices -> {
                                BigDecimal total = productWithPrices.stream()
                                        .map(ProductWithPrice::getFinalPrice)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                                boolean discountApplied = productWithPrices.stream()
                                        .anyMatch(pwp -> pwp.getDiscountPercentage() != null && pwp.getDiscountPercentage() > 0);

                                return new Order(
                                        UUID.randomUUID().toString(),
                                        cleanedIds,
                                        productWithPrices,
                                        total,
                                        discountApplied,
                                        LocalDateTime.now(),
                                        OrderStatus.COMPLETED
                                );
                            });
                })

                .doOnNext(order -> log.info("[ORDER_DONE] orderId={} total={} status={}",
                        order.getOrderId(), order.getTotalPrice(), order.getStatus()))

                // 6) Gestion d'erreurs
                .timeout(java.time.Duration.ofSeconds(5))

                .doOnError(e -> log.error("[ORDER_ERROR] {}", e.toString(), e))

                .onErrorResume(e -> {
                    log.warn("[ORDER_FAILED_FALLBACK] Création d'une commande FAILED suite à une erreur");
                    return Mono.just(buildFailedOrder(request));
                })

                .doFinally(signal -> log.info("[FINALLY] signal={}", signal));
    }

    protected Order buildFailedOrder(OrderRequest request) {
        return new Order(
                UUID.randomUUID().toString(),
                request != null ? request.getProductIds() : List.of(),
                List.of(),
                BigDecimal.ZERO,
                false,
                LocalDateTime.now(),
                OrderStatus.FAILED
        );
    }
}