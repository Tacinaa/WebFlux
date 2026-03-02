package com.example.tppipelinereactif.model;

import java.math.BigDecimal;

public class Product {

    private String id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String category;

    public Product(String id, String name, BigDecimal price, Integer stock, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }

    public String getCategory() {
        return category;
    }
}