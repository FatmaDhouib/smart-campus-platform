package com.smartcampus.auth.integration;

import com.smartcampus.auth.entity.Role;
import com.smartcampus.auth.entity.User;
import com.smartcampus.auth.repository.UserRepository;
import com.smartcampus.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;


    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    private User createUser(String email, Role role) {

        User user = new User();

        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("StrongPassword123"));
        user.setRole(role);
        user.setEnabled(true);

        return userRepository.save(user);
    }

   @Test
    void registrationEndpointShouldBePublic() throws Exception {

       String request = """
            {
                "firstName": "Test",
                "lastName": "Student",
                "email": "new.student@example.com",
                "password": "StrongPassword123"
            }
            """;

     mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.role").value("STUDENT"));
    }

   @Test
    void loginEndpointShouldBePublic() throws Exception {

    createUser("login@example.com", Role.STUDENT);

        String request = """
            {
                "email": "login@example.com",
                "password": "StrongPassword123"
            }
            """;

    mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void currentUserEndpointShouldRejectMissingToken() throws Exception {

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void currentUserEndpointShouldRejectInvalidToken() throws Exception {

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer invalid.token.value"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void currentUserEndpointShouldReturnAuthenticatedStudent()
            throws Exception {

        User student = createUser("student@example.com", Role.STUDENT);
        String token = jwtService.generateToken(student.getEmail());

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("student@example.com"))
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void currentUserEndpointShouldReturnAuthenticatedStaff()
            throws Exception {

        User staff = createUser("staff@example.com", Role.STAFF);
        String token = jwtService.generateToken(staff.getEmail());

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("staff@example.com"))
                .andExpect(jsonPath("$.role").value("STAFF"));
    }
}
