package com.example.exercices.exercice10;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BookService {

    private final Map<String, Book> books = new ConcurrentHashMap<>();

    public BookService() {
        books.put("1", new Book("1", "Un palais d'épines et de roses", "Sarah J. Maas"));
        books.put("2", new Book("2", "Shatter me 1", "Tahereh Mafi"));
        books.put("3", new Book("3", "Tant que fleuriront les citrionniers", "Zoulfa Katouh"));
    }

    public Flux<Book> getAllBooks() {
        return Flux.fromIterable(books.values());
    }

    public Flux<Book> searchByTitle(String titleQuery) {
        if (titleQuery == null || titleQuery.isBlank()) {
            return Flux.empty();
        }
        String q = titleQuery.toLowerCase();

        return Flux.fromIterable(books.values())
                .filter(b -> b.getTitle() != null && b.getTitle().toLowerCase().contains(q));
    }

    public Mono<Book> createBook(Book book) {
        String id = UUID.randomUUID().toString();
        book.setId(id);
        books.put(id, book);
        return Mono.just(book);
    }

    public Mono<Void> deleteBook(String id) {
        books.remove(id);
        return Mono.empty();
    }

    public Mono<Book> getBookById(String id) {
        return Mono.justOrEmpty(books.get(id));
    }
}