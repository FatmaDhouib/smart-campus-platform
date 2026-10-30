package com.smartcampus.auth.service;

import com.smartcampus.auth.dto.AuthResponse;
import com.smartcampus.auth.dto.LoginRequest;
import com.smartcampus.auth.dto.LoginResponse;
import com.smartcampus.auth.dto.RegisterRequest;
import com.smartcampus.auth.entity.Role;
import com.smartcampus.auth.entity.User;
import com.smartcampus.auth.exception.EmailAlreadyExistsException;
import com.smartcampus.auth.exception.InvalidCredentialsException;
import com.smartcampus.auth.repository.UserRepository;
import com.smartcampus.auth.security.JwtService;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthService authService;

    private static final long JWT_EXPIRATION_MS = 86400000L;
    private static final String TEST_EMAIL = "fatma@example.com";
    private static final String RAW_PASSWORD = "StrongPassword123";
    private static final String TEST_TOKEN = "test.jwt.token";

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        jwtService = mock(JwtService.class);

        authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtService,
                JWT_EXPIRATION_MS
        );
    }

    private User createTestUser() {
        User user = new User();
        user.setFirstName("Fatma");
        user.setLastName("Dhouib");
        user.setEmail(TEST_EMAIL);
        user.setPassword(passwordEncoder.encode(RAW_PASSWORD));
        user.setRole(Role.STUDENT);
        user.setEnabled(true);

        return user;
    }

    private void assignId(User user) {
        try {
            var idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, UUID.randomUUID());
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

    // Registration tests

    @Test
    void registerShouldCreateStudentWithHashedPassword() {
        RegisterRequest request = new RegisterRequest(
                "Fatma", "Dhouib", TEST_EMAIL, RAW_PASSWORD
        );

        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    assignId(user);
                    return user;
                });

        AuthResponse response = authService.register(request);

        assertNotNull(response.id());
        assertEquals("Fatma", response.firstName());
        assertEquals("Dhouib", response.lastName());
        assertEquals(TEST_EMAIL, response.email());
        assertEquals(Role.STUDENT, response.role());

        verify(userRepository).save(argThat((User user) ->
                user.getPassword() != null
                && !user.getPassword().equals(RAW_PASSWORD)
                && passwordEncoder.matches(
                        RAW_PASSWORD, user.getPassword()
                )
        ));
    }

    @Test
    void registerShouldNormalizeEmail() {
        RegisterRequest request = new RegisterRequest(
                "Fatma", "Dhouib",
                " FATMA@EXAMPLE.COM ", RAW_PASSWORD
        );

        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authService.register(request);

        assertEquals(TEST_EMAIL, response.email());
    }

    @Test
    void registerShouldRejectDuplicateEmail() {
        RegisterRequest request = new RegisterRequest(
                "Fatma", "Dhouib", TEST_EMAIL, RAW_PASSWORD
        );

        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.register(request)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerResponseShouldNotExposePassword() {
        RegisterRequest request = new RegisterRequest(
                "Fatma", "Dhouib", TEST_EMAIL, RAW_PASSWORD
        );

        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertFalse(response.toString().contains(RAW_PASSWORD));
    }

    // Login tests

    @Test
    void loginShouldReturnTokenForValidCredentials() {
        User user = createTestUser();
        assignId(user);

        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.of(user));
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(TEST_TOKEN);

        LoginResponse response = authService.login(
                new LoginRequest(TEST_EMAIL, RAW_PASSWORD)
        );

        assertEquals(user.getId(), response.id());
        assertEquals(TEST_EMAIL, response.email());
        assertEquals(Role.STUDENT, response.role());
        assertEquals(TEST_TOKEN, response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(86400L, response.expiresIn());

        verify(jwtService).generateToken(TEST_EMAIL);
    }

    @Test
    void loginShouldNormalizeEmail() {
        User user = createTestUser();

        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.of(user));
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(TEST_TOKEN);

        LoginResponse response = authService.login(
                new LoginRequest(" FATMA@EXAMPLE.COM ", RAW_PASSWORD)
        );

        assertEquals(TEST_EMAIL, response.email());
        verify(userRepository).findByEmail(TEST_EMAIL);
    }

    @Test
    void loginShouldRejectUnknownEmail() {
        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(
                        new LoginRequest(TEST_EMAIL, RAW_PASSWORD)
                )
        );

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void loginShouldRejectIncorrectPassword() {
        User user = createTestUser();

        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.of(user));

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(
                        new LoginRequest(TEST_EMAIL, "WrongPassword123")
                )
        );

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void loginShouldRejectDisabledAccount() {
        User user = createTestUser();
        user.setEnabled(false);

        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.of(user));

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(
                        new LoginRequest(TEST_EMAIL, RAW_PASSWORD)
                )
        );

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void loginResponseShouldNotExposePassword() {
        User user = createTestUser();

        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.of(user));
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(TEST_TOKEN);

        LoginResponse response = authService.login(
                new LoginRequest(TEST_EMAIL, RAW_PASSWORD)
        );

        assertFalse(response.toString().contains(RAW_PASSWORD));
    }
}