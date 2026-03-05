package com.example.tppipelinereactif.service;

import com.example.tppipelinereactif.model.OrderRequest;
import com.example.tppipelinereactif.model.OrderStatus;
import com.example.tppipelinereactif.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

class OrderServiceTest {

    private OrderService orderService;

    @BeforeEach
    void setup() {
        ProductRepository productRepository = new ProductRepository(0.0);
        orderService = new OrderService(productRepository);
    }

    @Test
    @DisplayName("Doit créer une commande COMPLETED avec 2 produits valides et le total correct")
    void test_processOrderSuccess() {

        OrderRequest request = new OrderRequest(
                Arrays.asList("PROD001", "PROD003"),
                "CUST001"
        );

        StepVerifier.create(orderService.processOrder(request))
                .assertNext(order -> {
                    assertThat(order.getOrderId()).isNotNull();
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
                    assertThat(order.getProducts()).hasSize(2);
                    assertThat(order.getTotalPrice()).isEqualByComparingTo(new BigDecimal("209.79"));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Doit ignorer les IDs invalides, vides ou inexistants")
    void test_processOrderWithInvalidIds() {

        OrderRequest request = new OrderRequest(
                Arrays.asList("PROD001", "PROD999", "   ", null, "PROD002", "PROD003"),
                "CUST001"
        );

        StepVerifier.create(orderService.processOrder(request))
                .assertNext(order -> {
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);

                    assertThat(order.getProducts()).hasSize(2);

                    assertThat(order.getProducts())
                            .extracting(pwp -> pwp.getProduct().getId())
                            .containsExactlyInAnyOrder("PROD001", "PROD003");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Doit ignorer les produits en rupture de stock")
    void test_processOrderWithoutStock() {

        OrderRequest request = new OrderRequest(
                Arrays.asList("PROD002"),
                "CUST001"
        );

        StepVerifier.create(orderService.processOrder(request))
                .assertNext(order -> {
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
                    assertThat(order.getProducts()).isEmpty();
                    assertThat(order.getTotalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Doit appliquer les remises correctes selon la catégorie produit")
    void test_processOrderWithDiscounts() {

        OrderRequest request = new OrderRequest(
                Arrays.asList("PROD001", "PROD003"),
                "CUST001"
        );

        StepVerifier.create(orderService.processOrder(request))
                .assertNext(order -> {
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
                    assertThat(order.getProducts()).hasSize(2);

                    var p1 = order.getProducts().stream()
                            .filter(p -> p.getProduct().getId().equals("PROD001"))
                            .findFirst()
                            .orElseThrow();

                    var p3 = order.getProducts().stream()
                            .filter(p -> p.getProduct().getId().equals("PROD003"))
                            .findFirst()
                            .orElseThrow();

                    assertThat(p1.getDiscountPercentage()).isEqualTo(10);
                    assertThat(p3.getDiscountPercentage()).isEqualTo(5);

                    BigDecimal expectedTotal = p1.getFinalPrice().add(p3.getFinalPrice());
                    assertThat(order.getTotalPrice()).isEqualByComparingTo(expectedTotal);
                })
                .verifyComplete();
    }

    static class SlowProductRepository extends ProductRepository {
        public SlowProductRepository() {
            super(0.0);
        }

        @Override
        public reactor.core.publisher.Mono<com.example.tppipelinereactif.model.Product> findById(String id) {
            return super.findById(id).delayElement(java.time.Duration.ofSeconds(6));
        }
    }

    @Test
    @DisplayName("Doit retourner une commande FAILED si le traitement dépasse le timeout")
    void test_processOrderTimeout() {

        OrderService slowService = new OrderService(new SlowProductRepository());

        OrderRequest request = new OrderRequest(
                Arrays.asList("PROD001"),
                "CUST001"
        );

        StepVerifier.create(slowService.processOrder(request))
                .assertNext(order -> {
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.FAILED);
                    assertThat(order.getProducts()).isEmpty();
                    assertThat(order.getTotalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Doit continuer à produire des commandes même si le repository génère des erreurs")
    void test_processOrderWithErrors() {

        ProductRepository flakyRepo = new ProductRepository(0.5);
        OrderService flakyService = new OrderService(flakyRepo);

        OrderRequest request = new OrderRequest(
                Arrays.asList("PROD001", "PROD003"),
                "CUST001"
        );

        StepVerifier.create(
                        reactor.core.publisher.Flux.range(1, 10)
                                .flatMap(i -> flakyService.processOrder(request))
                )
                .expectNextCount(10)
                .verifyComplete();
    }
}