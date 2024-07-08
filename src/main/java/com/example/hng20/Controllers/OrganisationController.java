package com.example.hng20.Controllers;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.hng20.ExceptionHandlers.ResourceNotFoundException;
import com.example.hng20.Models.AddUserRequest;
import com.example.hng20.Models.ApiResponse;
import com.example.hng20.Models.Organisation;
import com.example.hng20.Models.OrganisationDTO;
import com.example.hng20.Models.User;
import com.example.hng20.Models.UserPrincipal;
import com.example.hng20.Services.OrganisationService;
import com.example.hng20.Services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/organisations")
public class OrganisationController {

    private static final Logger logger = LoggerFactory.getLogger(OrganisationController.class);
    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getUserOrganisations(@AuthenticationPrincipal UserPrincipal currentUserPrincipal) {
        if (currentUserPrincipal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("error", "User not authenticated", 401));
        }
        User currentUser = currentUserPrincipal.getUser();
        logger.info("Fetching organisations for user: {}", currentUser.getEmail());
        List<Organisation> organisations = organisationService.getUserOrganisations(currentUser);
        logger.info("Organisations retrieved: {}", organisations);
         List<OrganisationDTO> orgDTOs;
        orgDTOs = organisations.stream()
                .map(OrganisationDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse("success", "Organisations retrieved successfully", orgDTOs));
    }

    @GetMapping("/{orgId}")
    public ResponseEntity<?> getOrganisation(@PathVariable String orgId, @AuthenticationPrincipal User currentUser) {
        return organisationService.findById(orgId)
        .map(org -> {
            OrganisationDTO orgDTO = new OrganisationDTO(org);
            return ResponseEntity.ok(new ApiResponse("success", "Organisation found", orgDTO));
        })
        .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createOrganisation(@Valid @RequestBody Organisation organisation, @AuthenticationPrincipal User currentUser) {
        organisation.getUsers().add(currentUser);
        Organisation createdOrg = organisationService.createOrganisation(organisation);
        OrganisationDTO orgDTO = new OrganisationDTO(createdOrg);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("success", "Organisation created successfully", orgDTO));
    }

    @PostMapping("/{orgId}/users")
    public ResponseEntity<?> addUserToOrganisation(@PathVariable String orgId, @RequestBody AddUserRequest request, @AuthenticationPrincipal User currentUser) {
        return organisationService.findById(orgId)
                .map(org -> {
                    User userToAdd = userService.findById(request.getUserId())
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    @SuppressWarnings("unused")
                    Organisation updatedOrg = organisationService.addUserToOrganisation(org, userToAdd);
                    return ResponseEntity.ok(new ApiResponse("success", "User added to organisation successfully", null));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
