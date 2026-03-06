package com.example.exercice14.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomAccessDeniedHandler implements ServerAccessDeniedHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange,
                             AccessDeniedException denied) {

        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = """
                {
                  "error": "FORBIDDEN",
                  "message": "Vous n'avez pas les droits nécessaires pour accéder à cette ressource"
                }
                """;

        var buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(body.getBytes());

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}