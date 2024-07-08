package com.example.hng20.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.hng20.Models.ApiResponse;
import com.example.hng20.Models.User;
import com.example.hng20.Services.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id, @AuthenticationPrincipal User currentUser) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(new ApiResponse("success", "User found", user)))
                .orElse(ResponseEntity.notFound().build());
    }
}
