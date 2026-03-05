package com.example.exercices.exercice10;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class BookHandler {

    private final BookService bookService;

    public BookHandler(BookService bookService) {
        this.bookService = bookService;
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookService.getAllBooks(), Book.class);
    }

    public Mono<ServerResponse> search(ServerRequest request) {
        String title = request.queryParam("title").orElse("");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookService.searchByTitle(title), Book.class);
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(Book.class)
                .flatMap(bookService::createBook)
                .flatMap(created -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(created));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");

        return bookService.getBookById(id)
                .switchIfEmpty(ServerResponse.notFound().build().then(Mono.empty()))
                .flatMap(b -> bookService.deleteBook(id).then(ServerResponse.noContent().build()));
    }
}