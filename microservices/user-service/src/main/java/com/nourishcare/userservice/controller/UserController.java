package com.nourishcare.userservice.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nourishcare.userservice.model.User;
import com.nourishcare.userservice.security.UserPrincipal;
import com.nourishcare.userservice.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Optional<User> user = userService.getUserById(userPrincipal.getId());
        if (user.isPresent()) {
            User foundUser = user.get();
            // Remove password from response
            foundUser.setPassword(null);
            return ResponseEntity.ok(foundUser);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                             @Valid @RequestBody User userUpdate) {
        Optional<User> userOptional = userService.getUserById(userPrincipal.getId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Update allowed fields
            if (userUpdate.getFirstName() != null) user.setFirstName(userUpdate.getFirstName());
            if (userUpdate.getLastName() != null) user.setLastName(userUpdate.getLastName());
            if (userUpdate.getPhoneNumber() != null) user.setPhoneNumber(userUpdate.getPhoneNumber());
            if (userUpdate.getDateOfBirth() != null) user.setDateOfBirth(userUpdate.getDateOfBirth());
            if (userUpdate.getGender() != null) user.setGender(userUpdate.getGender());
            if (userUpdate.getHeight() != null) user.setHeight(userUpdate.getHeight());
            if (userUpdate.getWeight() != null) user.setWeight(userUpdate.getWeight());
            if (userUpdate.getActivityLevel() != null) user.setActivityLevel(userUpdate.getActivityLevel());
            if (userUpdate.getDietaryRestrictions() != null) user.setDietaryRestrictions(userUpdate.getDietaryRestrictions());
            if (userUpdate.getAllergies() != null) user.setAllergies(userUpdate.getAllergies());
            if (userUpdate.getPreferredCuisines() != null) user.setPreferredCuisines(userUpdate.getPreferredCuisines());
            if (userUpdate.getNutritionalGoals() != null) user.setNutritionalGoals(userUpdate.getNutritionalGoals());
            if (userUpdate.getHealthProfile() != null) user.setHealthProfile(userUpdate.getHealthProfile());
            if (userUpdate.getPreferences() != null) user.setPreferences(userUpdate.getPreferences());
            if (userUpdate.getAddress() != null) user.setAddress(userUpdate.getAddress());
            if (userUpdate.getTimezone() != null) user.setTimezone(userUpdate.getTimezone());
            if (userUpdate.getLanguage() != null) user.setLanguage(userUpdate.getLanguage());
            
            User updatedUser = userService.updateUser(user);
            updatedUser.setPassword(null); // Remove password from response
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            User foundUser = user.get();
            foundUser.setPassword(null); // Remove password from response
            return ResponseEntity.ok(foundUser);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        // Remove passwords from all users
        users.forEach(user -> user.setPassword(null));
        return ResponseEntity.ok(users);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/{id}/verify-email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> verifyEmail(@PathVariable String id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            userService.verifyEmail(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}