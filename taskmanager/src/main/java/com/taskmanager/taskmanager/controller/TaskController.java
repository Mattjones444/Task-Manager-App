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

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository taskRepository,
                          UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    /* ========================================================
       1. HTML PAGE – GET /tasks
       ======================================================== */
    @GetMapping("/tasks")
    public String taskBoard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        AppUser currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Task> userTasks = taskRepository.findByUser(currentUser);
        Map<TaskStatus, List<Task>> grouped = userTasks.stream()
                .filter(t -> t.getStatus() != null)
                .collect(Collectors.groupingBy(Task::getStatus));

        model.addAttribute("todoTasks", grouped.getOrDefault(TaskStatus.TO_DO, List.of()));
        model.addAttribute("inProgressTasks", grouped.getOrDefault(TaskStatus.IN_PROGRESS, List.of()));
        model.addAttribute("doneTasks", grouped.getOrDefault(TaskStatus.DONE, List.of()));

        return "task";
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

    // PATCH /api/tasks/{id}
    @PatchMapping("/api/tasks/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Map<String, String> updates) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        if (updates.containsKey("status")) {
            String statusStr = updates.get("status").toUpperCase().replaceAll("\\s", "_");
            try {
                task.setStatus(TaskStatus.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status value: " + statusStr);
            }
        }

        if (updates.containsKey("title")) {
            task.setTitle(updates.get("title"));
        }

        if (updates.containsKey("description")) {
            task.setDescription(updates.get("description"));
        }

        if (updates.containsKey("setDate")) {
            String dateStr = updates.get("setDate");
            try {
                task.setSetDate(LocalDate.parse(dateStr));
            } catch (DateTimeParseException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format: " + dateStr);
            }
        }

        Task updated = taskRepository.save(task);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/tasks/{id}
    @DeleteMapping("/api/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build();  // 404 Not Found
        }
    }
}

