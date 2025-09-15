package com.nourishcare.visionservice.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class GoogleCloudConfig {

    private static final Logger logger = LoggerFactory.getLogger(GoogleCloudConfig.class);

    @Value("${google.application.credentials}")
    private String credentialsPath;

    @Value("${google.cloud.project-id}")
    private String projectId;

    @Bean
    public GoogleCredentials googleCredentials() throws IOException {
        logger.info("Loading Google Cloud credentials from: {}", credentialsPath);
        
        try {
            // Remove the "classpath:" prefix if present
            String resourcePath = credentialsPath.startsWith("classpath:") 
                ? credentialsPath.substring("classpath:".length())
                : credentialsPath;
                
            ClassPathResource resource = new ClassPathResource(resourcePath);
            
            if (!resource.exists()) {
                throw new IOException("Google Cloud credentials file not found: " + resourcePath);
            }
            
            try (InputStream credentialsStream = resource.getInputStream()) {
                GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
                logger.info("Google Cloud credentials loaded successfully for project: {}", projectId);
                return credentials;
            }
        } catch (Exception e) {
            logger.error("Failed to load Google Cloud credentials", e);
            throw new IOException("Failed to load Google Cloud credentials: " + e.getMessage(), e);
        }
    }

    @Bean
    public ImageAnnotatorSettings imageAnnotatorSettings(GoogleCredentials googleCredentials) throws IOException {
        logger.info("Creating ImageAnnotator settings...");
        
        try {
            ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                    .setCredentialsProvider(() -> googleCredentials)
                    .build();
                    
            logger.info("ImageAnnotator settings created successfully");
            return settings;
        } catch (Exception e) {
            logger.error("Failed to create ImageAnnotator settings", e);
            throw new IOException("Failed to create ImageAnnotator settings: " + e.getMessage(), e);
        }
    }
}