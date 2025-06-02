package com.taskmanager.taskmanager; // Must match your main class package or be scanned

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String home() {
        return "home"; // This looks for templates/home.html in src/main/resources/templates/
    }
}


