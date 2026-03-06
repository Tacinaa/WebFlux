package com.example.exercice14.service;

import com.example.exercice14.model.Room;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RoomService {

    private final Map<Long, Room> rooms = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

    public Flux<Room> getAllRooms() {
        return Flux.fromIterable(rooms.values());
    }

    public Mono<Room> addRoom(Room room) {
        Long id = idGenerator.incrementAndGet();
        room.setId(id);
        rooms.put(id, room);
        return Mono.just(room);
    }

    public Mono<Void> deleteRoom(Long id) {
        rooms.remove(id);
        return Mono.empty();
    }
}