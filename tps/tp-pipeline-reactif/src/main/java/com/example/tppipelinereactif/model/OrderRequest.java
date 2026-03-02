package com.example.tppipelinereactif.model;

import java.util.List;

public class OrderRequest {

    private List<String> productIds;
    private String customerId;

    public OrderRequest(List<String> productIds, String customerId) {
        this.productIds = productIds;
        this.customerId = customerId;
    }

    public List<String> getProductIds() {
        return productIds;
    }

    public String getCustomerId() {
        return customerId;
    }
}