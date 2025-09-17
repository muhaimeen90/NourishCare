package com.nourishcare.userservice.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class GoogleSignInRequest {
    
    @NotBlank
    private String googleId;
    
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    private String name;
    
    private String image;
    
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    public GoogleSignInRequest() {}
    
    public String getGoogleId() {
        return googleId;
    }
    
    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}