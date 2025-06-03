package com.taskmanager; // You might create a new 'controller' package

import com.taskmanager.model.AppUser;
import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        // Find or create the user. This is crucial for your task creation.
        // For simplicity, let's assume the client sends the userId directly in the Task object
        // Or you might send the username and find it here.
        // For this example, let's replicate the logic from your CommandLineRunner:
        AppUser existingUser = userRepository.findByUsername("Test User").orElseGet(() -> {
            AppUser newUser = new AppUser();
            newUser.setUsername("Task Owner");
            userRepository.save(newUser);
            return newUser;
        });

        newTask.setUser(existingUser); // Associate the task with the found/created user

        Task savedTask = taskRepository.save(newTask);
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED); // Return the saved task with 201 Created status
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