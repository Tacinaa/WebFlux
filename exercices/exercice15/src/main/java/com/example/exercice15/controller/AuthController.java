package com.example.exercice15.controller;

import com.example.exercice15.dto.LoginRequest;
import com.example.exercice15.dto.LoginResponse;
import com.example.exercice15.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Mono<LoginResponse> login(@RequestBody LoginRequest request) {
        return authService.login(request.getUsername(), request.getPassword())
                .map(LoginResponse::new)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides")
                ));
    }
}