package com.example.hng20;

import java.util.List;

import com.example.hng20.Models.Organisation;
import com.example.hng20.Models.User;
import com.example.hng20.Repositories.UserRepository;
import com.example.hng20.Services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.hng20.Repositories.OrganisationRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "DATABASE_URL=jdbc:h2:mem:testdb",
    "DATABASE_USERNAME=sa",
    "DATABASE_PASSWORD=",
    "JWT_SECRET=9a4f2c8d3b7a1e6f45c8a0b3f267d8b1d4e6f3c8a9d2b5f8e3a9c6b5d0f1e2a3",
    "JWT_EXPIRATION_MS=86400000"
})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
    // Clear any associations or references that prevent deletion of users
    List<Organisation> allOrgs = organisationRepository.findAll();
    for (Organisation org : allOrgs) {
        org.getUsers().clear(); // Assuming users are mapped in the Organisation entity
        organisationRepository.save(org);
    }

    // Delete all users
    userRepository.deleteAll();
}

    @Test
    public void testRegisterUserSuccessfully() throws Exception {
    String jsonRequest = "{\"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"john.doe@example.com\", \"password\": \"password123\"}";

    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data.accessToken", notNullValue()))
            .andExpect(jsonPath("$.data.user.userId", notNullValue()))
            .andExpect(jsonPath("$.data.user.firstName").value("John"))
            .andExpect(jsonPath("$.data.user.lastName").value("Doe"))
            .andExpect(jsonPath("$.data.user.email").value("john.doe@example.com")); // Check if userId exists in the response
}

    @Test
    public void testRegisterUserMissingFields() throws Exception {
    String jsonRequest = "{\"firstName\": \"\", \"lastName\": \"\", \"email\": \"\", \"password\": \"\"}";

    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.status").value("error"))  // Adjusted to expect "error"
            .andExpect(jsonPath("$.message").value("Validation Error"));  // Ensure this matches the actual error message returned
}

    @Test
    public void testRegisterUserDuplicateEmail() throws Exception {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        userService.saveUser(user);

        String jsonRequest = "{\"firstName\": \"Jane\", \"lastName\": \"Doe\", \"email\": \"john.doe@example.com\", \"password\": \"password123\"}";

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }
}
