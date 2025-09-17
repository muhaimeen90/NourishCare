package com.nourishcare.inventoryservice.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nourishcare.inventoryservice.model.FoodDonation;
import com.nourishcare.inventoryservice.model.FoodDonationRequest;
import com.nourishcare.inventoryservice.model.FoodItem;
import com.nourishcare.inventoryservice.service.InventoryService;

/**
 * REST Controller for community food sharing features
 * Handles donation creation, browsing, and management
 */
@RestController
@RequestMapping("/api/community")
public class CommunityController {

    @Autowired
    private InventoryService inventoryService;

    /**
     * Create a new food donation
     * POST /api/community/donations
     */
    @PostMapping("/donations")
    public ResponseEntity<?> createDonation(@Valid @RequestBody FoodDonationRequest request, 
                                          HttpServletRequest httpRequest) {
        try {
            // Get user ID from JWT token (set by interceptor)
            String authenticatedUserId = (String) httpRequest.getAttribute("userId");
            
            if (request.getFoodItemIds().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "At least one food item is required"));
            }
            
            String donorId;
            if (authenticatedUserId != null) {
                // Use authenticated user ID
                donorId = authenticatedUserId;
            } else {
                // Fallback: Get user ID from the first food item (for backward compatibility)
                Optional<FoodItem> firstItem = inventoryService.getFoodItemById(request.getFoodItemIds().get(0));
                if (!firstItem.isPresent()) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Food item not found"));
                }
                donorId = firstItem.get().getUserId();
            }
            
            FoodDonation donation = inventoryService.createDonation(donorId, request);
            return ResponseEntity.ok(donation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create donation: " + e.getMessage()));
        }
    }

    /**
     * Get all available donations for community browsing
     * GET /api/community/donations/available
     */
    @GetMapping("/donations/available")
    public ResponseEntity<List<FoodDonation>> getAvailableDonations() {
        List<FoodDonation> donations = inventoryService.getAllAvailableDonations();
        return ResponseEntity.ok(donations);
    }

    /**
     * Get available donations by city
     * GET /api/community/donations/available?city=CityName
     */
    @GetMapping("/donations/available/city")
    public ResponseEntity<List<FoodDonation>> getAvailableDonationsByCity(
            @RequestParam String city) {
        List<FoodDonation> donations = inventoryService.getAvailableDonationsByCity(city);
        return ResponseEntity.ok(donations);
    }

    /**
     * Get donations by specific donor
     * GET /api/community/donations/donor/{donorId}
     */
    @GetMapping("/donations/donor/{donorId}")
    public ResponseEntity<List<FoodDonation>> getDonationsByDonor(
            @PathVariable String donorId) {
        List<FoodDonation> donations = inventoryService.getDonationsByDonor(donorId);
        return ResponseEntity.ok(donations);
    }

    /**
     * Get specific donation by ID
     * GET /api/community/donations/{id}
     */
    @GetMapping("/donations/{id}")
    public ResponseEntity<?> getDonationById(@PathVariable String id) {
        Optional<FoodDonation> donation = inventoryService.getDonationById(id);
        if (donation.isPresent()) {
            return ResponseEntity.ok(donation.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update donation status (only by donor)
     * PUT /api/community/donations/{id}/status
     */
    @PutMapping("/donations/{id}/status")
    public ResponseEntity<?> updateDonationStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> statusUpdate,
            HttpServletRequest httpRequest) {
        try {
            // Get authenticated user ID
            String authenticatedUserId = (String) httpRequest.getAttribute("userId");
            
            String statusStr = statusUpdate.get("status");
            if (statusStr == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Status is required"));
            }

            FoodDonation.DonationStatus newStatus;
            try {
                newStatus = FoodDonation.DonationStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid status: " + statusStr));
            }

            // Use authenticated user ID if available, otherwise require donorId parameter
            String donorId = authenticatedUserId;
            if (donorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authentication required"));
            }

            Optional<FoodDonation> updatedDonation = inventoryService.updateDonationStatus(id, donorId, newStatus);
            if (updatedDonation.isPresent()) {
                return ResponseEntity.ok(updatedDonation.get());
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Not authorized to update this donation or donation not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update donation status: " + e.getMessage()));
        }
    }

    /**
     * Cancel a donation (only by donor)
     * DELETE /api/community/donations/{id}
     */
    @DeleteMapping("/donations/{id}")
    public ResponseEntity<?> cancelDonation(
            @PathVariable String id,
            @RequestParam String donorId) {
        boolean cancelled = inventoryService.cancelDonation(id, donorId);
        if (cancelled) {
            return ResponseEntity.ok(Map.of("message", "Donation cancelled successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Not authorized to cancel this donation or donation not found"));
        }
    }

    /**
     * Get user's items suitable for donation (expiring soon)
     * GET /api/community/suggestions/{userId}
     */
    @GetMapping("/suggestions/{userId}")
    public ResponseEntity<List<FoodItem>> getDonationSuggestions(
            @PathVariable String userId,
            @RequestParam(defaultValue = "3") int days) {
        List<FoodItem> suggestions = inventoryService.getExpiringSoonForDonation(userId, days);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Get donation statistics for the community
     * GET /api/community/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDonationStats() {
        Map<String, Object> stats = inventoryService.getDonationStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get donation statistics for the community (alternative endpoint)
     * GET /api/community/donations/stats
     */
    @GetMapping("/donations/stats")
    public ResponseEntity<Map<String, Object>> getDonationStatsAlternative() {
        Map<String, Object> stats = inventoryService.getDonationStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Admin endpoint to clean up expired donations
     * POST /api/community/cleanup
     */
    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupExpiredDonations() {
        inventoryService.cleanupExpiredDonations();
        return ResponseEntity.ok(Map.of("message", "Expired donations cleaned up successfully"));
    }

    /**
     * Health check for community features
     * GET /api/community/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "healthy",
                "service", "community-sharing",
                "timestamp", System.currentTimeMillis()
        ));
    }
}