// src/main/java/com/example/job_platform/service/ResumeService.java
package com.example.job_platform.service;

import com.example.job_platform.dto.JobRecommendation; // Import the new DTO
import com.example.job_platform.model.JobPosting; // Import JobPosting model
import com.example.job_platform.model.User;
import com.example.job_platform.repository.UserRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList; // For building the list of recommendations
import java.util.Arrays;
import java.util.Comparator; // For sorting recommendations
import java.util.HashSet; // For efficient skill comparison
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors; // For stream operations

/**
 * Service for managing resume uploads to MongoDB GridFS and updating user records.
 * Also stores metadata like extracted skills for simplified recommendations.
 * Includes logic for providing job recommendations based on applicant's skills.
 */
@Service
public class ResumeService {

    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations gridFsOperations;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JobPostingService jobPostingService; // Inject JobPostingService

    @Autowired
    public ResumeService(GridFsTemplate gridFsTemplate, GridFsOperations gridFsOperations,
                         UserRepository userRepository, UserService userService,
                         JobPostingService jobPostingService) { // Add JobPostingService to constructor
        this.gridFsTemplate = gridFsTemplate;
        this.gridFsOperations = gridFsOperations;
        this.userRepository = userRepository;
        this.userService = userService;
        this.jobPostingService = jobPostingService; // Initialize
    }

    /**
     * Uploads a resume PDF to GridFS and updates the user's record with the GridFS file ID.
     * Also accepts extracted skills and a summary, which will be added as metadata to the GridFS file.
     *
     * @param username The username of the "Apply" user uploading the resume.
     * @param file The resume file (MultipartFile).
     * @param extractedSkills A comma-separated string of skills extracted from the resume (simplified for now).
     * @param resumeSummary A brief summary/keywords from the resume (simplified for now).
     * @return The ID of the stored GridFS file if successful, or Optional.empty() if user not found,
     * not an "Apply" user, or upload fails.
     */
    public Optional<String> uploadResume(String username, MultipartFile file,
                                         String extractedSkills, String resumeSummary) {
        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isEmpty()) {
            System.err.println("Resume upload failed: User " + username + " not found.");
            return Optional.empty();
        }

        User user = userOptional.get();

        if (!"Apply".equalsIgnoreCase(user.getSection())) {
            System.err.println("Resume upload failed: User " + username + " is not authorized to upload resumes (Section: " + user.getSection() + ").");
            return Optional.empty();
        }

        if (file.isEmpty() || !("application/pdf".equalsIgnoreCase(file.getContentType()))) {
            System.err.println("Resume upload failed: Invalid file or file type. Only PDF files are allowed.");
            return Optional.empty();
        }

        if (user.getResumeGridFsId() != null && !user.getResumeGridFsId().isEmpty()) {
            try {
                gridFsTemplate.delete(new Query(Criteria.where("_id").is(user.getResumeGridFsId())));
                System.out.println("Deleted old resume for user: " + username);
            } catch (Exception e) {
                System.err.println("Error deleting old resume for user " + username + ": " + e.getMessage());
            }
        }

        try {
            Object fileId = gridFsTemplate.store(
                    file.getInputStream(),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    new BasicDBObject()
                            .append("username", username)
                            .append("extractedSkills", extractedSkills != null ?
                                    Arrays.stream(extractedSkills.split(","))
                                            .map(String::trim) // Trim whitespace from skills
                                            .map(String::toLowerCase) // Convert to lowercase for case-insensitive matching
                                            .collect(Collectors.toList()) :
                                    new ArrayList<>()) // Store an empty list if no skills
                            .append("resumeSummary", resumeSummary)
            );

            user.setResumeGridFsId(fileId.toString());
            userRepository.save(user);

            System.out.println("Resume uploaded successfully for user: " + username + " with GridFS ID: " + fileId);
            return Optional.of(fileId.toString());

        } catch (IOException e) {
            System.err.println("Error uploading resume for user " + username + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Retrieves a resume file from GridFS by its GridFS ID.
     *
     * @param gridFsId The ID of the resume file in GridFS.
     * @return An Optional containing the GridFSFile object if found.
     */
    public Optional<GridFSFile> getResumeFile(String gridFsId) {
        if (gridFsId == null || gridFsId.isEmpty()) {
            return Optional.empty();
        }
        Query query = new Query(Criteria.where("_id").is(new ObjectId(gridFsId)));
        return Optional.ofNullable(gridFsTemplate.findOne(query));
    }

    /**
     * Retrieves resume metadata (skills, summary) for a given user.
     *
     * @param username The username of the user.
     * @return A Map containing "extractedSkills" (List<String>) and "resumeSummary" (String)
     * if a resume is found and has metadata, otherwise empty.
     */
    public Optional<Map<String, Object>> getResumeMetadata(String username) {
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty() || userOptional.get().getResumeGridFsId() == null || userOptional.get().getResumeGridFsId().isEmpty()) {
            return Optional.empty();
        }

        String gridFsId = userOptional.get().getResumeGridFsId();
        Optional<GridFSFile> gridFSFileOptional = getResumeFile(gridFsId);

        if (gridFSFileOptional.isPresent()) {
            GridFSFile file = gridFSFileOptional.get();
            if (file.getMetadata() != null) {
                Map<String, Object> metadata = new java.util.HashMap<>();
                if (file.getMetadata().containsKey("extractedSkills")) {
                    // GridFS metadata stores Lists, so direct cast is fine
                    metadata.put("extractedSkills", file.getMetadata().get("extractedSkills"));
                }
                if (file.getMetadata().containsKey("resumeSummary")) {
                    metadata.put("resumeSummary", file.getMetadata().get("resumeSummary"));
                }
                return Optional.of(metadata);
            }
        }
        return Optional.empty();
    }

    /**
     * Provides a list of recommended job postings for a specific applicant based on their resume skills.
     *
     * @param applicantUsername The username of the applicant.
     * @return A list of JobRecommendation DTOs, sorted by match score in descending order.
     * Returns an empty list if the applicant is not found, not an "Apply" user,
     * or no resume data (skills) is available.
     */
    public List<JobRecommendation> getRecommendedJobsForApplicant(String applicantUsername) {
        List<JobRecommendation> recommendations = new ArrayList<>();

        // 1. Get applicant's extracted skills
        Optional<Map<String, Object>> resumeMetadataOptional = getResumeMetadata(applicantUsername);
        if (resumeMetadataOptional.isEmpty() || !resumeMetadataOptional.get().containsKey("extractedSkills")) {
            System.out.println("No skills found in resume for applicant: " + applicantUsername);
            return recommendations; // No skills, no recommendations
        }

        // Convert extracted skills to a Set for efficient lookup
        @SuppressWarnings("unchecked")
        List<String> applicantSkillsList = (List<String>) resumeMetadataOptional.get().get("extractedSkills");
        Set<String> applicantSkills = new HashSet<>(applicantSkillsList);

        if (applicantSkills.isEmpty()) {
            System.out.println("Applicant " + applicantUsername + " has no extracted skills for recommendation.");
            return recommendations;
        }

        // 2. Get all available job postings
        List<JobPosting> allJobs = jobPostingService.getAllJobPostings();
        if (allJobs.isEmpty()) {
            System.out.println("No job postings available for recommendation.");
            return recommendations; // No jobs, no recommendations
        }

        // 3. Compare applicant skills with job skills and calculate score
        for (JobPosting job : allJobs) {
            if (job.getSkills() != null && !job.getSkills().isEmpty()) {
                Set<String> jobSkills = job.getSkills().stream()
                        .map(String::trim)
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet());

                int commonSkills = 0;
                for (String skill : applicantSkills) {
                    if (jobSkills.contains(skill)) {
                        commonSkills++;
                    }
                }

                // Simple match score: number of common skills / total unique skills required by job
                // Or / total unique skills of applicant, or a combination.
                // For simplicity, let's use ratio of common skills to total skills required by job.
                // Avoid division by zero.
                double matchScore = (jobSkills.size() > 0) ? (double) commonSkills / jobSkills.size() : 0.0;
                matchScore = Math.round(matchScore * 100.0) / 100.0; // Round to 2 decimal places

                if (matchScore > 0) { // Only add jobs with some match
                    recommendations.add(new JobRecommendation(job, matchScore));
                }
            }
        }

        // 4. Sort recommendations by match score (descending)
        recommendations.sort(Comparator.comparingDouble(JobRecommendation::getMatchScore).reversed());

        System.out.println("Generated " + recommendations.size() + " recommendations for " + applicantUsername);
        return recommendations;
    }
}
