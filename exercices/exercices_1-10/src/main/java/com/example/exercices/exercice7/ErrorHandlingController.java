package com.example.exercices.exercice7;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ErrorHandlingController {

    @GetMapping("/api/error-resume")
    public Flux<String> errorResume() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Erreur après le C")))
                .onErrorResume(err -> Flux.just("Default1", "Default2"));
    }

    @GetMapping("/api/error-continue")
    public Flux<Integer> errorContinue() {
        return Flux.range(1, 5)
                .map(n -> {
                    if (n ==2){
                        throw new RuntimeException("Erreur à 2");
                    }
                    return n;
                })
                .onErrorContinue((error, value) -> {});
    }
}