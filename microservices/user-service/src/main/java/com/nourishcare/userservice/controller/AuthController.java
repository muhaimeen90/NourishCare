package com.nourishcare.userservice.controller;

import com.nourishcare.userservice.dto.*;
import com.nourishcare.userservice.model.User;
import com.nourishcare.userservice.repository.UserRepository;
import com.nourishcare.userservice.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    AuthenticationManager authenticationManager;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    PasswordEncoder encoder;
    
    @Autowired
    JwtUtils jwtUtils;
    
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        
        // Find user by username or email
        User user = userRepository.findByUsername(loginRequest.getUsernameOrEmail())
                .orElseGet(() -> userRepository.findByEmail(loginRequest.getUsernameOrEmail())
                        .orElse(null));
        
        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: User not found!"));
        }
        
        // Check if password matches
        if (!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Invalid credentials!"));
        }
        
        // Create authentication token
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        user.getId(), // Use userId for authentication
                        loginRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        List<String> roles = user.getRoles().stream()
                .map(role -> role.toString())
                .collect(Collectors.toList());
        
        // Update last login time
        user.setLastLoginAt(java.time.LocalDateTime.now());
        userRepository.save(user);
        
        return ResponseEntity.ok(new JwtResponse(jwt,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles));
    }
    
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }
        
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }
        
        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getFirstName(),
                signUpRequest.getLastName());
        
        if (signUpRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(signUpRequest.getPhoneNumber());
        }
        
        // Set default role
        Set<User.UserRole> roles = new HashSet<>();
        roles.add(User.UserRole.USER);
        user.setRoles(roles);
        
        userRepository.save(user);
        
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    
    @PostMapping("/google-signin")
    public ResponseEntity<?> googleSignIn(@Valid @RequestBody GoogleSignInRequest googleSignInRequest) {
        try {
            // Check if user exists by email
            Optional<User> existingUser = userRepository.findByEmail(googleSignInRequest.getEmail());
            
            User user;
            if (existingUser.isPresent()) {
                // Update existing user with Google info
                user = existingUser.get();
                user.setFirstName(googleSignInRequest.getFirstName());
                user.setLastName(googleSignInRequest.getLastName());
                user.setLastLoginAt(java.time.LocalDateTime.now());
            } else {
                // Create new user from Google profile
                user = new User();
                user.setUsername(googleSignInRequest.getEmail().split("@")[0] + "_google");
                user.setEmail(googleSignInRequest.getEmail());
                user.setFirstName(googleSignInRequest.getFirstName());
                user.setLastName(googleSignInRequest.getLastName());
                user.setProfileImageUrl(googleSignInRequest.getImage());
                user.setEmailVerified(true); // Google emails are verified
                
                // Set a random password (user won't use it for Google auth)
                user.setPassword(encoder.encode(java.util.UUID.randomUUID().toString()));
                
                // Set default role
                Set<User.UserRole> roles = new HashSet<>();
                roles.add(User.UserRole.USER);
                user.setRoles(roles);
            }
            
            userRepository.save(user);
            
            // Generate JWT token
            String jwt = jwtUtils.generateTokenFromUsername(user.getId());
            
            List<String> roles = user.getRoles().stream()
                    .map(role -> role.toString())
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(new JwtResponse(jwt,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    roles));
                    
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Google sign-in failed - " + e.getMessage()));
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("You've been signed out!"));
    }
    
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            if (jwtUtils.validateJwtToken(token)) {
                String userId = jwtUtils.getUserIdFromJwtToken(token);
                Optional<User> user = userRepository.findById(userId);
                
                if (user.isPresent()) {
                    User foundUser = user.get();
                    List<String> roles = foundUser.getRoles().stream()
                            .map(role -> role.toString())
                            .collect(Collectors.toList());
                    
                    return ResponseEntity.ok(new JwtResponse(token,
                            foundUser.getId(),
                            foundUser.getUsername(),
                            foundUser.getEmail(),
                            foundUser.getFirstName(),
                            foundUser.getLastName(),
                            roles));
                }
            }
            
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Invalid token"));
                    
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Token validation failed"));
        }
    }
}