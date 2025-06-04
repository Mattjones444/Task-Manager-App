package com.taskmanager.taskmanager.controller;

import com.taskmanager.model.AppUser;
import org.springframework.ui.Model;

import com.taskmanager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

  @GetMapping("/register")
public String showRegistrationForm(Model model) {
    model.addAttribute("user", new AppUser());  // <-- Add empty user object here
    return "register";
}


    @PostMapping("/register")
    public String registerUser(@ModelAttribute AppUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // return login.html view
    }
}

