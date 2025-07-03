package com.example.job_platform.repository; // Updated package

import com.example.job_platform.model.User; // Updated import
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User documents.
 * Extends MongoRepository to provide standard CRUD operations
 * and custom query methods for finding users by username.
 */
@Repository // Marks this interface as a Spring Data repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Finds a user by their username.
     * Spring Data MongoDB automatically creates the query based on the method name.
     *
     * @param username The username to search for.
     * @return An Optional containing the found User, or empty if not found.
     */
    Optional<User> findByUsername(String username);
}