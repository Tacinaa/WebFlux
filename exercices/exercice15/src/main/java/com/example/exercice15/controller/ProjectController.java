package com.example.exercice15.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @GetMapping
    public Mono<Map<String, Object>> getProjects(Authentication authentication) {
        String username = authentication.getName();

        List<String> projects;

        if ("user1".equals(username)) {
            projects = List.of("Projet A", "Projet B");
        } else if ("user2".equals(username)) {
            projects = List.of("Projet C", "Projet D");
        } else {
            projects = List.of();
        }

        return Mono.just(Map.of(
                "username", username,
                "projects", projects
        ));
    }
}