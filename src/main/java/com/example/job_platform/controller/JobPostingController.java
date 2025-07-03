package com.example.job_platform.controller;

import com.example.job_platform.model.JobPosting;
import com.example.job_platform.service.JobPostingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for managing job postings.
 * Provides endpoints for creating and retrieving job postings.
 */
@RestController
@RequestMapping("/api/jobs") // Base path for job-related endpoints
public class JobPostingController {

    private final JobPostingService jobPostingService;

    @Autowired
    public JobPostingController(JobPostingService jobPostingService) {
        this.jobPostingService = jobPostingService;
    }

    /**
     * Endpoint to create a new job posting.
     * Requires the user to be of "Post" section.
     * Expects a JSON payload with job details and the poster's username.
     *
     * @param requestBody A Map containing 'title', 'description', 'skills' (List),
     * 'experience', 'location', and 'posterUsername'.
     * @return ResponseEntity with the created job posting or an error message.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createJobPosting(@RequestBody Map<String, Object> requestBody) {
        String title = (String) requestBody.get("title");
        String description = (String) requestBody.get("description");
        // Skills will come as a List<String> from JSON
        @SuppressWarnings("unchecked")
        List<String> skills = (List<String>) requestBody.get("skills");
        String experience = (String) requestBody.get("experience");
        String location = (String) requestBody.get("location");
        String posterUsername = (String) requestBody.get("posterUsername"); // The username of the logged-in poster

        // Basic validation
        if (title == null || description == null || skills == null || experience == null ||
                location == null || posterUsername == null ||
                title.isEmpty() || description.isEmpty() || experience.isEmpty() ||
                location.isEmpty() || posterUsername.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "All fields (title, description, skills, experience, location, posterUsername) are required."));
        }

        Optional<JobPosting> jobPosting = jobPostingService.createJobPosting(
                title, description, skills, experience, location, posterUsername
        );

        if (jobPosting.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Job posting created successfully.",
                    "job", jobPosting.get()
            ));
        } else {
            // This could be due to user not found or not authorized (section mismatch)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "message", "Failed to create job posting. Ensure the user exists and has 'Post' section."
            ));
        }
    }

    /**
     * Endpoint to retrieve all job postings.
     *
     * @return ResponseEntity with a list of all job postings.
     */
    @GetMapping
    public ResponseEntity<List<JobPosting>> getAllJobPostings() {
        List<JobPosting> jobPostings = jobPostingService.getAllJobPostings();
        return ResponseEntity.ok(jobPostings);
    }
}
