package com.example.exercice12.controller;

import com.example.exercice12.dto.OrderStatusUpdateRequest;
import com.example.exercice12.model.OrderEntity;
import com.example.exercice12.service.OrderService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Flux<OrderEntity> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Mono<OrderEntity> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping
    public Mono<OrderEntity> createOrder(@RequestBody OrderEntity order) {
        return orderService.createOrder(order);
    }

    @PutMapping("/{id}")
    public Mono<OrderEntity> updateOrderStatus(@PathVariable Long id,
                                               @RequestBody OrderStatusUpdateRequest request) {
        return orderService.updateOrderStatus(id, request.getStatus());
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteOrder(@PathVariable Long id) {
        return orderService.deleteOrder(id);
    }

    @GetMapping("/search")
    public Flux<OrderEntity> getOrdersByStatus(@RequestParam String status) {
        return orderService.getOrdersByStatus(status);
    }

    @GetMapping("/customer/{customerName}")
    public Flux<OrderEntity> getOrdersByCustomerName(@PathVariable String customerName) {
        return orderService.getOrdersByCustomerName(customerName);
    }

    @GetMapping("/paged")
    public Flux<OrderEntity> getPagedOrders(@RequestParam int page, @RequestParam int size) {
        return orderService.getPagedOrders(page, size);
    }
}