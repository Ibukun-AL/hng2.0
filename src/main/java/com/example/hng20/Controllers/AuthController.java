package com.example.hng20.Controllers;


import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hng20.Models.ApiResponse;
import com.example.hng20.Models.AuthResponse;
import com.example.hng20.Models.LoginRequest;
import com.example.hng20.Models.Organisation;
import com.example.hng20.Models.OrganisationDTO;
import com.example.hng20.Models.User;
import com.example.hng20.Models.UserDTO;
import com.example.hng20.Models.UserPrincipal;
import com.example.hng20.Security.JwtTokenProvider;
import com.example.hng20.Services.OrganisationService;
import com.example.hng20.Services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        logger.info("Received registration request for email: " + user.getEmail());

        if (userService.existsByEmail(user.getEmail())) {
            logger.warn("Email already exists: " + user.getEmail());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ApiResponse("Bad Request", "Registration unsuccessful", 400));
        }

        User registeredUser = userService.registerUser(user);
        logger.info("User registered successfully: " + registeredUser.getEmail());

        

       
    try {
        Organisation defaultOrg = new Organisation();
        defaultOrg.setName(user.getFirstName() + "'s Organisation");
        defaultOrg.getUsers().add(registeredUser);

        organisationService.createOrganisation(defaultOrg);
        logger.info("Default organization created for user: {}", registeredUser.getEmail());

        registeredUser.getOrganisations().add(defaultOrg);
        userService.saveUser(registeredUser); // Save the updated user with the organisation
        logger.info("Updated user with organisation: {}", registeredUser);

        String accessToken = jwtTokenProvider.generateToken(UserPrincipal.create(registeredUser));
        logger.info("Generated token: {}", accessToken);

        UserDTO userDTO = new UserDTO(registeredUser);
        AuthResponse authResponse = new AuthResponse(accessToken, userDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("success", "Registration successful", authResponse));
    } catch (Exception e) {
        logger.error("Error creating default organization for user: {}", registeredUser.getEmail(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "User registered but failed to create organization", 500));
    }
    // String token = jwtTokenProvider.generateToken(UserPrincipal.create(registeredUser));
    //     logger.info("Generated token: " + token);

    //     return ResponseEntity.status(HttpStatus.CREATED)
    //             .body(new ApiResponse("success", "Registration successful", new AuthResponse(token, registeredUser)));
        
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for user: " + loginRequest.getEmail());
    if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
        logger.warn("Empty password provided for user: " + loginRequest.getEmail());
        return ResponseEntity.badRequest().body(new ApiResponse("error", "Password cannot be empty", 400));
    }
    try {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Object principal = authentication.getPrincipal();
        logger.info("Principal class: {}", principal.getClass().getName());

        if (!(principal instanceof UserPrincipal)) {
            logger.error("Principal is not an instance of UserPrincipal");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Unexpected authentication principal", 500));
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser(); // Assuming UserPrincipal has a getUser() method

        if (user == null) {
            logger.error("User object is null in UserPrincipal");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "User not found", 500));
        }

        String accessToken = jwtTokenProvider.generateToken(userPrincipal);

        logger.info("Generated token for user: {}", user.getEmail());
        UserDTO userDTO = new UserDTO(user);

        // Create a response with the access token and user data only
        AuthResponse authResponse = new AuthResponse(accessToken, userDTO);
        return ResponseEntity.ok(new ApiResponse("success", "Login successful", authResponse));
    } catch (AuthenticationException e) {
        logger.error("Authentication failed for user: {}", loginRequest.getEmail(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse("Bad request", "Authentication failed", 401));
    }
    }
}
