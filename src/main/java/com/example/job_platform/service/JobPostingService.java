// src/main/java/com/example/job_platform/service/JobPostingService.java
package com.example.job_platform.service;

import com.example.job_platform.model.JobPosting;
import com.example.job_platform.model.User;
import com.example.job_platform.repository.JobPostingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing job postings.
 * Handles creation and retrieval of job postings, including authorization checks
 * for user sections.
 */
@Service
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final UserService userService; // To fetch user details for authorization

    @Autowired
    public JobPostingService(JobPostingRepository jobPostingRepository, UserService userService) {
        this.jobPostingRepository = jobPostingRepository;
        this.userService = userService;
    }

    /**
     * Creates a new job posting.
     * Requires the user identified by `posterUsername` to have the "Post" section.
     *
     * @param title The title of the job.
     * @param description The detailed description of the job.
     * @param skills A list of required skills.
     * @param experience The required experience level.
     * @param location The job location.
     * @param posterUsername The username of the user attempting to post the job.
     * @return The created JobPosting object if successful, or Optional.empty() if
     * the user is not authorized or not found.
     */
    public Optional<JobPosting> createJobPosting(String title, String description, List<String> skills,
                                                 String experience, String location, String posterUsername) {
        // Find the user to verify their section
        Optional<User> posterOptional = userService.findByUsername(posterUsername);

        if (posterOptional.isEmpty()) {
            System.err.println("Job posting failed: User " + posterUsername + " not found.");
            return Optional.empty(); // User not found
        }

        User poster = posterOptional.get();

        // Check if the user has the "Post" section
        if (!"Post".equalsIgnoreCase(poster.getSection())) {
            System.err.println("Job posting failed: User " + posterUsername + " is not authorized to post jobs (Section: " + poster.getSection() + ").");
            return Optional.empty(); // User not authorized
        }

        JobPosting newJob = new JobPosting(
                title, description, skills, experience, location,
                poster.getId(), poster.getUsername()
        );

        return Optional.of(jobPostingRepository.save(newJob));
    }

    /**
     * Retrieves all job postings.
     * @return A list of all job postings.
     */
    public List<JobPosting> getAllJobPostings() {
        return jobPostingRepository.findAll();
    }

    // You can add more methods here, e.g., getJobById, updateJobPosting, deleteJobPosting
}
