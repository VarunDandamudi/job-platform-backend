package com.example.job_platform; // Updated package

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories // Enable Spring Data MongoDB repositories
public class JobPlatformAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobPlatformAuthApplication.class, args);
	}

}