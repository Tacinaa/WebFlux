package com.example.tppipelinereactif.model;

import java.math.BigDecimal;

public class ProductWithPrice {

    private Product product;
    private BigDecimal originalPrice;
    private Integer discountPercentage;
    private BigDecimal finalPrice;

    public ProductWithPrice(Product product, BigDecimal originalPrice, Integer discountPercentage, BigDecimal finalPrice) {
        this.product = product;
        this.originalPrice = originalPrice;
        this.discountPercentage = discountPercentage;
        this.finalPrice = finalPrice;
    }

    public Product getProduct() {
        return product;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public Integer getDiscountPercentage() {
        return discountPercentage;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }
}