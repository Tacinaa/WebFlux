package com.example.exercices.exercice9;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskService {

    private final Map<String, Task> tasks = new ConcurrentHashMap<>();

    public TaskService() {
        tasks.put("1", new Task("1", "Manger", false));
        tasks.put("2", new Task("2", "Encore manger", true));
    }

    public Flux<Task> getAllTasks() {
        return Flux.fromIterable(tasks.values());
    }

    public Mono<Task> getTaskById(String id) {
        return Mono.justOrEmpty(tasks.get(id));
    }

    public Mono<Task> createTask(Task task) {
        String id = UUID.randomUUID().toString();
        task.setId(id);
        tasks.put(id, task);
        return Mono.just(task);
    }

    public Mono<Task> updateTask(String id, Task updated) {
        return getTaskById(id)
                .flatMap(existing -> {
                    existing.setDescription(updated.getDescription());
                    existing.setCompleted(updated.isCompleted());
                    tasks.put(id, existing);
                    return Mono.just(existing);
                });
    }

    public Mono<Void> deleteTask(String id) {
        tasks.remove(id);
        return Mono.empty();
    }
}