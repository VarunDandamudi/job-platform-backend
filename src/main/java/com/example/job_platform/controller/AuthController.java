// src/main/java/com/example/job_platform/controller/AuthController.java
package com.example.job_platform.controller;

import com.example.job_platform.model.User;
import com.example.job_platform.service.UserService;
import com.example.job_platform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for user authentication operations: signup, login, and logout.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Endpoint for user signup.
     * Expects a JSON payload with 'username', 'password', and 'section'.
     *
     * @param requestBody A Map containing 'username', 'password', and 'section'.
     * @return ResponseEntity with success/failure message and appropriate HTTP status.
     */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        String password = requestBody.get("password");
        String section = requestBody.get("section"); // Get the new section field

        Map<String, String> response = new HashMap<>();

        if (username == null || password == null || section == null ||
                username.isEmpty() || password.isEmpty() || section.isEmpty()) {
            response.put("message", "Username, password, and section are required.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Simple validation for section values
        if (!"Post".equalsIgnoreCase(section) && !"Apply".equalsIgnoreCase(section)) {
            response.put("message", "Invalid section. Must be 'Post' or 'Apply'.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User newUser = userService.signup(username, password, section); // Pass section to service
        if (newUser != null) {
            response.put("message", "Signup successful for user: " + newUser.getUsername());
            response.put("userId", newUser.getId());
            response.put("username", newUser.getUsername());
            response.put("section", newUser.getSection()); // Return the section
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            response.put("message", "Username already exists.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
    }

    /**
     * Endpoint for user login.
     * Expects a JSON payload with 'username' and 'password'.
     * On successful login, a JWT token is generated and returned.
     *
     * @param requestBody A Map containing 'username' and 'password'.
     * @return ResponseEntity with success/failure message and appropriate HTTP status.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        String password = requestBody.get("password");

        Map<String, String> response = new HashMap<>();

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            response.put("message", "Username and password are required.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Optional<User> userOptional = userService.login(username, password);
        if (userOptional.isPresent()) {
            // Generate JWT token upon successful login
            String token = jwtUtil.generateToken(userOptional.get().getUsername());

            response.put("message", "Login successful for user: " + userOptional.get().getUsername());
            response.put("userId", userOptional.get().getId());
            response.put("username", userOptional.get().getUsername());
            response.put("section", userOptional.get().getSection()); // Return the user's section on login
            response.put("token", token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", "Invalid username or password.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Endpoint for user logout.
     * Since there's no server-side session management in this simple example with JWTs,
     * this endpoint primarily serves as a notification or for logging purposes.
     * It expects a username in the request body for logging.
     *
     * @param requestBody A Map containing 'username'.
     * @return ResponseEntity with a success message and HTTP status 200 OK.
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");

        Map<String, String> response = new HashMap<>();

        if (username == null || username.isEmpty()) {
            response.put("message", "Username is required for logout.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        userService.logout(username);
        response.put("message", "Logout successful for user: " + username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
