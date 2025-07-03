package com.example.job_platform.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Model representing a job posting document in the 'job_postings' collection.
 * Includes details like title, description, skills, experience, location,
 * and information about the user who posted it.
 */
@Document(collection = "job_postings") // Maps this class to the 'job_postings' collection
public class JobPosting {

    @Id // Primary identifier for the document
    private String id;
    private String title;
    private String description;
    private List<String> skills; // List of required skills
    private String experience; // e.g., "0-2 years", "2-5 years", "5+ years"
    private String location;
    private String postedByUserId; // ID of the user who posted the job
    private String postedByUsername; // Username of the user who posted the job
    private LocalDateTime postedDate; // Timestamp of when the job was posted

    // Default constructor for Spring Data MongoDB
    public JobPosting() {
    }

    // Constructor for creating a new job posting
    public JobPosting(String title, String description, List<String> skills,
                      String experience, String location, String postedByUserId,
                      String postedByUsername) {
        this.title = title;
        this.description = description;
        this.skills = skills;
        this.experience = experience;
        this.location = location;
        this.postedByUserId = postedByUserId;
        this.postedByUsername = postedByUsername;
        this.postedDate = LocalDateTime.now(); // Set current time when created
    }

    // Getters and Setters for all fields
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPostedByUserId() {
        return postedByUserId;
    }

    public void setPostedByUserId(String postedByUserId) {
        this.postedByUserId = postedByUserId;
    }

    public String getPostedByUsername() {
        return postedByUsername;
    }

    public void setPostedByUsername(String postedByUsername) {
        this.postedByUsername = postedByUsername;
    }

    public LocalDateTime getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(LocalDateTime postedDate) {
        this.postedDate = postedDate;
    }

    @Override
    public String toString() {
        return "JobPosting{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", postedByUsername='" + postedByUsername + '\'' +
                ", postedDate=" + postedDate +
                '}';
    }
}
