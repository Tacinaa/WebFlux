package com.example.tppipelinereactif;

import com.example.tppipelinereactif.model.OrderRequest;
import com.example.tppipelinereactif.repository.ProductRepository;
import com.example.tppipelinereactif.service.OrderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

//@Component
public class DemoRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        ProductRepository repo = new ProductRepository(0.0); // 0% erreurs pour commencer
        OrderService service = new OrderService(repo);

        OrderRequest request = new OrderRequest(
                Arrays.asList("PROD001", "PROD002", "   ", null, "PROD999", "PROD003"),
                "CUST001"
        );

        service.processOrder(request)
                .doOnNext(order -> {
                    System.out.println("\n===== RESULT ORDER =====");
                    System.out.println("orderId=" + order.getOrderId());
                    System.out.println("status=" + order.getStatus());
                    System.out.println("total=" + order.getTotalPrice());
                    System.out.println("nbProducts=" + order.getProducts().size());
                    System.out.println("products:");
                    order.getProducts().forEach(pwp -> {
                        System.out.println(" - " + pwp.getProduct().getId()
                                + " | " + pwp.getProduct().getName()
                                + " | cat=" + pwp.getProduct().getCategory()
                                + " | original=" + pwp.getOriginalPrice()
                                + " | discount=" + pwp.getDiscountPercentage() + "%"
                                + " | final=" + pwp.getFinalPrice()
                        );
                    });
                    System.out.println("========================\n");
                })
                .block();
    }
}