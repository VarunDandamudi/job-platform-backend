// src/main/java/com/example/job_platform/repository/JobPostingRepository.java
package com.example.job_platform.repository;

import com.example.job_platform.model.JobPosting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for JobPosting documents.
 * Extends MongoRepository to provide standard CRUD operations.
 */
@Repository // Marks this interface as a Spring Data repository
public interface JobPostingRepository extends MongoRepository<JobPosting, String> {
    // You can add custom query methods here if needed, e.g.,
    // List<JobPosting> findByPostedByUserId(String userId);
    // List<JobPosting> findBySkillsContaining(String skill);
}
