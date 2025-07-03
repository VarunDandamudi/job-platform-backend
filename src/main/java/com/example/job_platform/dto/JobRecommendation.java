// src/main/java/com/example/job_platform/dto/JobRecommendation.java
package com.example.job_platform.dto;

import com.example.job_platform.model.JobPosting;

/**
 * Data Transfer Object (DTO) to represent a job posting along with a calculated match score
 * for a specific applicant.
 */
public class JobRecommendation {
    private JobPosting jobPosting;
    private double matchScore; // Score indicating how well the applicant's skills match the job

    public JobRecommendation(JobPosting jobPosting, double matchScore) {
        this.jobPosting = jobPosting;
        this.matchScore = matchScore;
    }

    // Getters
    public JobPosting getJobPosting() {
        return jobPosting;
    }

    public double getMatchScore() {
        return matchScore;
    }

    // Setters (optional, typically DTOs are immutable)
    public void setJobPosting(JobPosting jobPosting) {
        this.jobPosting = jobPosting;
    }

    public void setMatchScore(double matchScore) {
        this.matchScore = matchScore;
    }
}
