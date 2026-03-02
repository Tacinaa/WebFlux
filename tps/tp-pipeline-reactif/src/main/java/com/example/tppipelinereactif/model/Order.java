package com.example.tppipelinereactif.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {

    private String orderId;
    private List<String> productIds;
    private List<ProductWithPrice> products;
    private BigDecimal totalPrice;
    private Boolean discountApplied;
    private LocalDateTime createdAt;
    private OrderStatus status;

    public Order(String orderId, List<String> productIds, List<ProductWithPrice> products, BigDecimal totalPrice, Boolean discountApplied, LocalDateTime createdAt, OrderStatus status) {
        this.orderId = orderId;
        this.productIds = productIds;
        this.products = products;
        this.totalPrice = totalPrice;
        this.discountApplied = discountApplied;
        this.createdAt = createdAt;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<String> getProductIds() {
        return productIds;
    }

    public List<ProductWithPrice> getProducts() {
        return products;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public Boolean getDiscountApplied() {
        return discountApplied;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }
}