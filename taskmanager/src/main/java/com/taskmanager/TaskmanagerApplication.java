package com.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.taskmanager.model.AppUser;
import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;

@SpringBootApplication
public class TaskmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskmanagerApplication.class, args);
	}





}
