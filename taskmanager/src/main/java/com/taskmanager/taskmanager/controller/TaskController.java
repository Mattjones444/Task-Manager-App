package com.taskmanager.taskmanager.controller;

import com.taskmanager.model.AppUser;
import com.taskmanager.model.Task;
import com.taskmanager.model.TaskStatus;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller          // <-- now a regular MVC controller
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository taskRepository,
                          UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    /* ========================================================
       1. HTML PAGE  –  GET /tasks
       ======================================================== */
    @GetMapping("/tasks")
    public String taskBoard(Model model) {

        // Logged‑in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        AppUser currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // User's tasks grouped by status
        List<Task> userTasks = taskRepository.findByUser(currentUser);
        Map<TaskStatus, List<Task>> grouped = userTasks.stream()
                .filter(t -> t.getStatus() != null)
                .collect(Collectors.groupingBy(Task::getStatus));

        model.addAttribute("todoTasks",       grouped.getOrDefault(TaskStatus.TO_DO,        List.of()));
        model.addAttribute("inProgressTasks", grouped.getOrDefault(TaskStatus.IN_PROGRESS,  List.of()));
        model.addAttribute("doneTasks",       grouped.getOrDefault(TaskStatus.DONE,        List.of()));

        return "task";           // -> templates/task.html
    }

    /* ========================================================
       2. REST ENDPOINTS  –  still under /api/tasks
       ======================================================== */

    // POST /api/tasks
@PostMapping("/api/tasks")
@ResponseBody
public ResponseEntity<Task> createTask(@RequestBody Task newTask) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = auth.getName();

    AppUser currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new RuntimeException("User not found: " + currentUsername));

    newTask.setUser(currentUser);
    newTask.setStatus(TaskStatus.TO_DO);  
    Task saved = taskRepository.save(newTask);

    return new ResponseEntity<>(saved, HttpStatus.CREATED);
}

    // GET /api/tasks
    @GetMapping("/api/tasks")
    @ResponseBody
    public Iterable<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // GET /api/tasks/users
    @GetMapping("/api/tasks/users")
    @ResponseBody
    public Iterable<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

@PatchMapping("/api/tasks/{id}")
public ResponseEntity<Task> updateTaskStatus(@PathVariable Long id, @RequestBody Map<String, String> updates) {
    Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

    if (updates.containsKey("status")) {
        String statusStr = updates.get("status").toUpperCase().replaceAll("\\s", "_"); // normalize string

        TaskStatus newStatus;
        try {
            newStatus = TaskStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status value: " + statusStr);
        }

        task.setStatus(newStatus);
    }

    Task updated = taskRepository.save(task);
    return ResponseEntity.ok(updated);
}
}
