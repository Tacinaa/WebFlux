package com.example.exercices.exercice10;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class BookRouter {

    @Bean
    public RouterFunction<ServerResponse> bookRoutes(BookHandler handler) {
        return route(GET("/api/books"), handler::getAll)
                .andRoute(GET("/api/books/search"), handler::search)
                .andRoute(POST("/api/books"), handler::create)
                .andRoute(DELETE("/api/books/{id}"), handler::delete);
    }
}