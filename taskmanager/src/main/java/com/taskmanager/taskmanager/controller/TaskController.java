package com.taskmanager.taskmanager.controller; // You might create a new 'controller' package

import com.taskmanager.model.AppUser;
import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@RestController // Marks this class as a REST Controller
@RequestMapping("/api/tasks") // Base URL for task-related endpoints
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository; // To find or create users

    // Spring will inject these repositories for you
    public TaskController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // This method will handle HTTP POST requests to /api/tasks
   @PostMapping
public ResponseEntity<Task> createTask(@RequestBody Task newTask) {
    // Get the currently logged-in user's username
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = auth.getName();

    // Fetch the user from the database
    AppUser currentUser = userRepository.findByUsername(currentUsername)
        .orElseThrow(() -> new RuntimeException("User not found: " + currentUsername));

    // Link the task to the current user
    newTask.setUser(currentUser);

    Task savedTask = taskRepository.save(newTask);
    return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
}

    // You might also want an endpoint to fetch all tasks to display them
    @GetMapping
    public Iterable<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Example of how you might get a list of users for your dropdown (e.g., in the modal)
    @GetMapping("/users")
    public Iterable<AppUser> getAllUsers() {
        return userRepository.findAll();
    }
}