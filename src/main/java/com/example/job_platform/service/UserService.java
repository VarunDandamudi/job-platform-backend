// src/main/java/com/example/job_platform/service/UserService.java
package com.example.job_platform.service;

import com.example.job_platform.model.User;
import com.example.job_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class to handle business logic for user operations.
 * This includes signup, login, and potentially logout logic.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user with a specified section (Post/Apply).
     * Passwords will be encrypted using BCrypt before saving.
     *
     * @param username The username for the new user.
     * @param password The raw password for the new user.
     * @param section  The user's section ("Post" or "Apply").
     * @return The newly created User object if successful, or null if the username already exists.
     */
    public User signup(String username, String password, String section) {
        // Basic validation for section input
        if (!"Post".equalsIgnoreCase(section) && !"Apply".equalsIgnoreCase(section)) {
            System.err.println("Invalid section provided: " + section + ". Must be 'Post' or 'Apply'.");
            return null; // Or throw an IllegalArgumentException
        }

        // Check if a user with the given username already exists
        if (userRepository.findByUsername(username).isPresent()) {
            return null; // Username already exists
        }
        // Encrypt the password before saving!
        String encodedPassword = passwordEncoder.encode(password);
        User newUser = new User(username, encodedPassword, section); // Save encoded password and section
        return userRepository.save(newUser);
    }

    /**
     * Authenticates a user based on username and password.
     * Compares the provided raw password with the stored hashed password.
     *
     * @param username The username to authenticate.
     * @param rawPassword The raw password provided by the user.
     * @return The authenticated User object if credentials are valid, or empty Optional otherwise.
     */
    public Optional<User> login(String username, String rawPassword) {
        // Find the user by username
        Optional<User> userOptional = userRepository.findByUsername(username);

        // Check if user exists and if the provided raw password matches the stored encoded password
        if (userOptional.isPresent() && passwordEncoder.matches(rawPassword, userOptional.get().getPassword())) {
            return userOptional; // Authentication successful
        }
        return Optional.empty(); // Authentication failed
    }

    /**
     * Finds a user by their username.
     * @param username The username to search for.
     * @return An Optional containing the found User, or empty if not found.
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Placeholder for logout logic.
     * In a stateless scenario without sessions (like a simple API), logout might just be a client-side action.
     * If sessions were used, this would invalidate the session.
     *
     * @param username The username of the user logging out (for logging purposes, not strict requirement here).
     * @return true always, as there's no server-side session to invalidate in this simple setup.
     */
    public boolean logout(String username) {
        System.out.println("User " + username + " has logged out.");
        return true;
    }
}
