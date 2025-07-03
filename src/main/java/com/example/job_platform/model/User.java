// src/main/java/com/example/job_platform/model/User.java
package com.example.job_platform.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User model representing a document in the 'users' collection in MongoDB.
 * It includes basic user details like ID, username, password, a 'section'
 * to differentiate between job posters and job seekers, and a reference
 * to their uploaded resume in GridFS.
 */
@Document(collection = "users") // Maps this class to the 'users' collection
public class User {

    @Id // Marks this field as the primary identifier for the document
    private String id;

    @Indexed(unique = true) // Ensures username is unique in the collection
    private String username;
    private String password; // Hashed password
    private String section;  // "Post" for job posters, "Apply" for job seekers
    private String resumeGridFsId; // New field: Stores the GridFS ID of the user's resume

    // Default constructor for Spring Data MongoDB
    public User() {
    }

    // Constructor updated to include section (used for initial signup)
    public User(String username, String password, String section) {
        this.username = username;
        this.password = password;
        this.section = section;
    }

    // Getters and Setters for all fields
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getResumeGridFsId() { // Getter for resumeGridFsId
        return resumeGridFsId;
    }

    public void setResumeGridFsId(String resumeGridFsId) { // Setter for resumeGridFsId
        this.resumeGridFsId = resumeGridFsId;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", section='" + section + '\'' +
                ", resumeGridFsId='" + (resumeGridFsId != null ? "present" : "absent") + '\'' + // Avoid logging actual ID directly
                '}';
    }
}
