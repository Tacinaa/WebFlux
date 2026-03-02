package com.foodexpress.webflux.exercices;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

public class Exercice2 {
    public static void main(String[] args) {
        AtomicInteger count = new AtomicInteger(0);

        Mono<String> unreliableApi = Mono.fromCallable(() -> {
            if(count.incrementAndGet() < 3){
                System.out.println("Tentative "+ count.get()+": Erreur réseau");
                throw new RuntimeException("Erreur réseau");
            }
            System.out.println("Tentative "+ count.get()+": Succès!");
            return "Succès!";
        });

        unreliableApi
                .retry(2)
                .subscribe(
                        System.out::println,
                        error -> System.err.println("Echec definitif "+error.getMessage())
                );
    }
}
