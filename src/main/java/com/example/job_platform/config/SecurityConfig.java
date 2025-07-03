// src/main/java/com/example/job_platform/config/SecurityConfig.java
package com.example.job_platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration; // Import CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource; // Import CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // Import UrlBasedCorsConfigurationSource

import java.util.Arrays; // Import Arrays

/**
 * Security configuration to explicitly permit all requests.
 * This allows us to use BCryptPasswordEncoder without enabling full Spring Security
 * authentication and authorization filters.
 * Also configures CORS to allow frontend requests from localhost:3000.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain to permit all HTTP requests and enables CORS.
     *
     * @param http The HttpSecurity object to configure.
     * @return The configured SecurityFilterChain.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for simplicity (for APIs)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll() // Permit all requests without authentication
                );
        return http.build();
    }

    /**
     * Defines the PasswordEncoder bean.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures CORS to allow requests from your React frontend.
     * Adjust allowed origins as needed for deployment.
     *
     * @return CorsConfigurationSource for CORS policies.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow requests from your React development server
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Your React app's origin
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true); // Allow sending cookies/auth headers
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply to all paths
        return source;
    }
}
