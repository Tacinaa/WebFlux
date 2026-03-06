package com.example.exercice12.service;

import com.example.exercice12.model.OrderEntity;
import com.example.exercice12.repository.OrderRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Flux<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }

    public Mono<OrderEntity> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Mono<OrderEntity> createOrder(OrderEntity order) {
        order.setId(null);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public Mono<OrderEntity> updateOrderStatus(Long id, String status) {
        return orderRepository.findById(id)
                .flatMap(existingOrder -> {
                    existingOrder.setStatus(status);
                    return orderRepository.save(existingOrder);
                });
    }

    public Mono<Void> deleteOrder(Long id) {
        return orderRepository.deleteById(id);
    }

    public Flux<OrderEntity> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    public Flux<OrderEntity> getOrdersByCustomerName(String customerName) {
        return orderRepository.findByCustomerName(customerName);
    }

    public Flux<OrderEntity> getPagedOrders(int page, int size) {
        long offset = (long) page * size;
        return orderRepository.findPaged(size, offset);
    }
}