package com.nourishcare.inventoryservice.security;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
    
    @Value("${nourishcare.app.jwtSecret:bm91cmlzaGNhcmVzZWNyZXRrZXlmb3Jqd3R0b2tlbnNlY3VyaXR5}")
    private String jwtSecret;
    
    // Simple JWT parsing without validation for now
    // In production, you would use proper JWT libraries
    public String getUserIdFromJwtToken(String token) {
        try {
            // Simple JWT parsing - split by dots and decode payload
            String[] chunks = token.split("\\.");
            if (chunks.length >= 2) {
                String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));
                // Extract sub field - this is very basic parsing
                if (payload.contains("\"sub\":\"")) {
                    int start = payload.indexOf("\"sub\":\"") + 7;
                    int end = payload.indexOf("\"", start);
                    if (end > start) {
                        return payload.substring(start, end);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing JWT: " + e.getMessage());
        }
        return null;
    }
    
    public boolean validateJwtToken(String authToken) {
        // For now, just check if token is present and has 3 parts
        return authToken != null && authToken.split("\\.").length == 3;
    }
}