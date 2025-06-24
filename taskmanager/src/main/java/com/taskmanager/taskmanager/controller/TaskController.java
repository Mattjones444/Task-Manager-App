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

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    /* ========================================================
       1. HTML PAGE – GET /tasks (uses same HTML file)
       ======================================================== */
    @GetMapping("/tasks")
    public String taskBoard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        AppUser currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Task> userTasks = taskRepository.findByUser(currentUser);

        // Status-based grouping
        Map<TaskStatus, List<Task>> grouped = userTasks.stream()
                .filter(t -> t.getStatus() != null)
                .collect(Collectors.groupingBy(Task::getStatus));

        model.addAttribute("todoTasks", grouped.getOrDefault(TaskStatus.TO_DO, List.of()));
        model.addAttribute("inProgressTasks", grouped.getOrDefault(TaskStatus.IN_PROGRESS, List.of()));
        model.addAttribute("doneTasks", grouped.getOrDefault(TaskStatus.DONE, List.of()));

        // Date-based sorting
        List<Task> sortedByDate = userTasks.stream()
                .sorted(Comparator.comparing(Task::getSetDate))
                .collect(Collectors.toList());

        // Debug output
        System.out.println("DEBUG: Tasks sorted by date for user '" + username + "':");
        for (Task task : sortedByDate) {
            System.out.println("- " + task.getTitle() + " | " + task.getSetDate() + " | Status: " + task.getStatus());
        }

        model.addAttribute("allTasksSortedByDate", sortedByDate);

        return "task";  // single Thymeleaf template
    }

    /* ========================================================
       2. REST ENDPOINTS – under /api/tasks
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

    // PATCH /api/tasks/{id}
    @PatchMapping("/api/tasks/{id}")
    @ResponseBody
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Task task = optionalTask.get();

        updates.forEach((key, value) -> {
            switch (key) {
                case "title":
                    task.setTitle((String) value);
                    break;
                case "description":
                    task.setDescription((String) value);
                    break;
                case "setDate":
                    try {
                        task.setSetDate(java.time.LocalDate.parse((String) value));
                    } catch (Exception e) {
                        // Ignore or handle error
                    }
                    break;
                case "status":
                    try {
                        task.setStatus(TaskStatus.valueOf((String) value));
                    } catch (Exception e) {
                        // Ignore invalid status
                    }
                    break;
            }
        });

        Task saved = taskRepository.save(task);
        return ResponseEntity.ok(saved);
    }

    // DELETE /api/tasks/{id}
    @DeleteMapping("/api/tasks/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}


