package com.example.hng20.Models;

import lombok.Data;


@Data
public class AuthResponse {
    private String token;
    private UserDTO user;
    private OrganisationDTO organisation;

    public AuthResponse(String token, UserDTO user, OrganisationDTO organisation) {
        this.token = token;
        this.user = user;
        this.organisation = organisation;
    }

     // Constructor for login response
     public AuthResponse(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }

    // Getters and setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

  
}
