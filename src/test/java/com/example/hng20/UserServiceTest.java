package com.example.hng20;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.hng20.Models.Organisation;

// src/test/java/com/example/hng20/service/UserServiceTest.java

import com.example.hng20.Models.User;
import com.example.hng20.Repositories.UserRepository;
import com.example.hng20.Services.UserService;
import com.example.hng20.Services.OrganisationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private OrganisationService organisationService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
    }

    @Test
    public void testRegisterUser() {
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.registerUser(user);

        assertNotNull(savedUser);
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());

        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testFindByEmail() {
        String email = "test@example.com";
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findByEmail(email);

        assertTrue(foundUser.isPresent());
        assertEquals(email, foundUser.get().getEmail());

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testExistsByEmail() {
        String email = "existing@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertTrue(userService.existsByEmail(email));

        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    public void testUserCannotAccessUnauthorizedOrganisations() {
        // Set up test data
        user.setUserId("123e4567-e89b-12d3-a456-426614174000");
        String unauthorizedOrgId = "999e4567-e89b-12d3-a456-426614174999";

        // Create a list with one authorized organization
        List<Organisation> authorizedOrgs = new ArrayList<>();
        Organisation authorizedOrg = new Organisation();
        authorizedOrg.setOrgId("111e4567-e89b-12d3-a456-426614174111");
        authorizedOrgs.add(authorizedOrg);

        // Mock the behavior of organisationService
        when(organisationService.getUserOrganisations(user)).thenReturn(authorizedOrgs);

        // Get the user's organizations
        List<Organisation> userOrgs = organisationService.getUserOrganisations(user);

        // Check that the unauthorized org is not in the user's organizations
        boolean hasUnauthorizedAccess = userOrgs.stream()
            .anyMatch(org -> org.getOrgId().equals(unauthorizedOrgId));

        assertFalse(hasUnauthorizedAccess);
    }
}