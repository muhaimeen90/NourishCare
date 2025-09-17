package com.nourishcare.inventoryservice.repository;

import com.nourishcare.inventoryservice.model.FoodDonation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FoodDonationRepository extends MongoRepository<FoodDonation, String> {
    
    /**
     * Find available donations ordered by creation date (newest first)
     */
    List<FoodDonation> findByStatusOrderByCreatedAtDesc(FoodDonation.DonationStatus status);
    
    /**
     * Find donations by donor ID ordered by creation date (newest first)
     */
    List<FoodDonation> findByDonorIdOrderByCreatedAtDesc(String donorId);
    
    /**
     * Find available donations by city (case insensitive)
     */
    List<FoodDonation> findByCityIgnoreCaseAndStatusOrderByCreatedAtDesc(String city, FoodDonation.DonationStatus status);
    
    /**
     * Find donations in a specific city
     */
    List<FoodDonation> findByCityIgnoreCaseOrderByCreatedAtDesc(String city);
    
    /**
     * Find donations created after a specific date
     */
    List<FoodDonation> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime date);
    
    /**
     * Find donations by status and city
     */
    @Query("{'status': ?0, 'city': {$regex: ?1, $options: 'i'}}")
    List<FoodDonation> findByStatusAndCity(FoodDonation.DonationStatus status, String city);
    
    /**
     * Count available donations
     */
    long countByStatus(FoodDonation.DonationStatus status);
    
    /**
     * Count donations by donor
     */
    long countByDonorId(String donorId);
    
    /**
     * Find old donations for cleanup (created before specified date)
     */
    @Query("{'createdAt': {$lt: ?0}}")
    List<FoodDonation> findOldDonations(LocalDateTime beforeDate);
    
    /**
     * Find donations with expiring items (custom query for complex logic)
     */
    @Query("{'status': 'AVAILABLE', 'foodItems.expirationDate': {$lte: ?0}}")
    List<FoodDonation> findDonationsWithExpiringSoon(LocalDateTime expirationThreshold);
}