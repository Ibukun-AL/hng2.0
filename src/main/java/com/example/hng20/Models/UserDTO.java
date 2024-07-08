package com.example.hng20.Models;

import lombok.Data;

@Data
public class UserDTO {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    // Getters and setters

    public UserDTO(User user) {
        this.userId = user.getUserId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        // Map other fields
    }
}
