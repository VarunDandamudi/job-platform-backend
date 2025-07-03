// src/main/java/com/example/job_platform/config/CorsConfig.java
package com.example.job_platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for Cross-Origin Resource Sharing (CORS) to allow frontend
 * applications to communicate with this backend API.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply CORS to all endpoints in your API
                .allowedOrigins("http://localhost:3000") // IMPORTANT: Replace with your actual frontend URL (e.g., React dev server)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow common HTTP methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true); // Allow sending of cookies or authorization headers (if you use them)
    }
}
