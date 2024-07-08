package com.example.hng20.Models;


import jakarta.validation.constraints.NotBlank;

public class AddUserRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    // Getter and setter
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
