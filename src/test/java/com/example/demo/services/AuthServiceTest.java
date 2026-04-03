package com.example.demo.services;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(jwtService, userRepository, passwordEncoder);
    }

    @Test
    void register_shouldSaveUserAndReturnSuccessResponse() {
        RegisterRequest request = new RegisterRequest("john", "john@example.com", "password");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");

        User savedUser = new User();
        savedUser.setUsername("john");
        savedUser.setEmail("john@example.com");
        savedUser.setPassword("encoded-password");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        AuthResponse response = authService.register(request);

        assertTrue(response.success());
        assertEquals("Registration successful", response.message());
        assertEquals("john", response.username());
        assertEquals("john@example.com", response.email());
        assertNull(response.token());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("john", captor.getValue().getUsername());
        assertEquals("john@example.com", captor.getValue().getEmail());
        assertEquals("encoded-password", captor.getValue().getPassword());
    }

    @Test
    void register_shouldThrowWhenUsernameExists() {
        RegisterRequest request = new RegisterRequest("john", "john@example.com", "password");
        when(userRepository.existsByUsername("john")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.register(request));

        assertEquals("Username already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
        verify(userRepository, never()).existsByEmail(anyString());
    }

    @Test
    void register_shouldThrowWhenEmailExists() {
        RegisterRequest request = new RegisterRequest("john", "john@example.com", "password");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        assertEquals("Email already exists", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldReturnTokenWhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("john", "password");

        User user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("encoded-password");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded-password")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertTrue(response.success());
        assertEquals("Login successful", response.message());
        assertEquals("john", response.username());
        assertEquals("john@example.com", response.email());
        assertEquals("jwt-token", response.token());
    }

    @Test
    void login_shouldFallBackToEmailLookup() {
        LoginRequest request = new LoginRequest("john@example.com", "password");

        User user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("encoded-password");

        when(userRepository.findByUsername("john@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded-password")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertTrue(response.success());
        assertEquals("jwt-token", response.token());
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    void login_shouldThrowWhenUserNotFound() {
        LoginRequest request = new LoginRequest("john", "password");

        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.login(request));

        assertEquals("Invalid username/email or password", ex.getMessage());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_shouldThrowWhenPasswordIsInvalid() {
        LoginRequest request = new LoginRequest("john", "wrong-password");

        User user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("encoded-password");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.login(request));

        assertEquals("Invalid username/email or password", ex.getMessage());
        verify(jwtService, never()).generateToken(any());
    }
}
