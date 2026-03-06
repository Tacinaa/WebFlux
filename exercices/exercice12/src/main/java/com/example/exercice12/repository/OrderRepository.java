package com.example.exercice12.repository;

import com.example.exercice12.model.OrderEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface OrderRepository extends ReactiveCrudRepository<OrderEntity, Long> {

    Flux<OrderEntity> findByStatus(String status);

    Flux<OrderEntity> findByCustomerName(String customerName);

    @Query("SELECT * FROM orders LIMIT :size OFFSET :offset")
    Flux<OrderEntity> findPaged(int size, long offset);
}