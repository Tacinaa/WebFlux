package com.example.exercice15.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class AuthService {

    private final JwtService jwtService;

    private final Map<String, String> users = Map.of(
            "user1", "password1",
            "user2", "password2"
    );

    public AuthService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public Mono<String> login(String username, String password) {
        String expectedPassword = users.get(username);

        if (expectedPassword != null && expectedPassword.equals(password)) {
            String token = jwtService.generateToken(username, Map.of("role", "USER"));
            return Mono.just(token);
        }

        return Mono.empty();
    }
}