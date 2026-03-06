package com.example.exercice14.controller;

import com.example.exercice14.model.Room;
import com.example.exercice14.service.RoomService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public Flux<Room> getRooms() {
        return roomService.getAllRooms();
    }

    @PostMapping
    public Mono<Room> addRoom(@RequestBody Room room) {
        return roomService.addRoom(room);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteRoom(@PathVariable Long id) {
        return roomService.deleteRoom(id);
    }
}