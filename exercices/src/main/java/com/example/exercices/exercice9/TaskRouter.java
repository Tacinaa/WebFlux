package com.example.exercices.exercice9;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class TaskRouter {

    @Bean
    public RouterFunction<ServerResponse> taskRoutes(TaskHandler handler) {
        return route(GET("/api/tasks"), handler::getAll)
                .andRoute(GET("/api/tasks/{id}"), handler::getById)
                .andRoute(POST("/api/tasks"), handler::create)
                .andRoute(PUT("/api/tasks/{id}"), handler::update)
                .andRoute(DELETE("/api/tasks/{id}"), handler::delete);
    }
}