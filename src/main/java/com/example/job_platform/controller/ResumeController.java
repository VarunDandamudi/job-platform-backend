// src/main/java/com/example/job_platform/controller/ResumeController.java
package com.example.job_platform.controller;

import com.example.job_platform.dto.JobRecommendation; // Import the new DTO
import com.example.job_platform.model.User;
import com.example.job_platform.service.ResumeService;
import com.example.job_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for resume-related operations, primarily upload and recommendations.
 */
@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;
    private final UserService userService;

    @Autowired
    public ResumeController(ResumeService resumeService, UserService userService) {
        this.resumeService = resumeService;
        this.userService = userService;
    }

    /**
     * Endpoint for "Apply" users to upload their resume (PDF).
     * Accepts a MultipartFile for the PDF and additional metadata (skills, summary).
     *
     * @param username The username of the applicant.
     * @param file The PDF resume file.
     * @param extractedSkills Comma-separated skills (e.g., "Java,Spring,MongoDB").
     * @param resumeSummary A brief summary or keywords from the resume.
     * @return ResponseEntity with success/failure message.
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadResume(
            @RequestParam("username") String username,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "extractedSkills", required = false) String extractedSkills,
            @RequestParam(value = "resumeSummary", required = false) String resumeSummary) {

        Map<String, String> response;

        if (username == null || username.isEmpty()) {
            response = Map.of("message", "Username is required for resume upload.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            response = Map.of("message", "User not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();
        if (!"Apply".equalsIgnoreCase(user.getSection())) {
            response = Map.of("message", "Only users with 'Apply' section can upload resumes.");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        if (file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            response = Map.of("message", "No file uploaded or file name is empty.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            response = Map.of("message", "Invalid file type. Only PDF files are allowed.");
            return new ResponseEntity<>(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        Optional<String> gridFsFileId = resumeService.uploadResume(username, file, extractedSkills, resumeSummary);

        if (gridFsFileId.isPresent()) {
            response = Map.of(
                    "message", "Resume uploaded successfully.",
                    "gridFsId", gridFsFileId.get()
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response = Map.of("message", "Failed to upload resume. Check server logs for details.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to get job recommendations for a specific applicant based on their resume skills.
     *
     * @param applicantUsername The username of the applicant.
     * @return A list of recommended jobs with their match scores, or an empty list.
     */
    @GetMapping("/recommendations/{applicantUsername}")
    public ResponseEntity<List<JobRecommendation>> getJobRecommendations(@PathVariable String applicantUsername) {
        Optional<User> userOptional = userService.findByUsername(applicantUsername);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of()); // User not found
        }
        User user = userOptional.get();
        if (!"Apply".equalsIgnoreCase(user.getSection())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(List.of()); // Only "Apply" users get recommendations
        }

        List<JobRecommendation> recommendations = resumeService.getRecommendedJobsForApplicant(applicantUsername);
        return ResponseEntity.ok(recommendations);
    }
}
