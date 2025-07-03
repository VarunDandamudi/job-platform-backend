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

    // In src/main/java/com/example/job_platform/config/CorsConfig.java
// ...
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("job-platform-sage.vercel.app") // <-- REPLACE THIS with your Vercel URL
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
// ...
}
