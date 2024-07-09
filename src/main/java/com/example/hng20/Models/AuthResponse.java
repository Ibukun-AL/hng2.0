package com.example.hng20.Models;

import lombok.Data;


@Data
public class AuthResponse {
    private String accessToken;
    private UserDTO user;
    private OrganisationDTO organisation;

    public AuthResponse(String accessToken, UserDTO user, OrganisationDTO organisation) {
        this.accessToken = accessToken;
        this.user = user;
        this.organisation = organisation;
    }

     // Constructor for login response
     public AuthResponse(String accessToken, UserDTO user) {
        this.accessToken = accessToken;
        this.user = user;
    }

    // Getters and setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

  
}
