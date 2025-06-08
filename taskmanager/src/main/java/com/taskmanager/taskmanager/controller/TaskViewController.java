package com.taskmanager.taskmanager.controller;

import com.taskmanager.model.AppUser;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class TaskViewController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskViewController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/tasks")
    public String showUserTasks(Model model) {
        // Get current username from security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        // Find user in the database
        AppUser currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found: " + currentUsername));

        // Get only their tasks
        model.addAttribute("tasks", taskRepository.findByUser(currentUser));

        return "tasks";
    }
}
