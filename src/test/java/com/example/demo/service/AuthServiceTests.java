package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.AuthService;
import com.example.demo.services.JwtService;
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
class AuthServiceTests {

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
    void register_shouldCreateUserAndReturnSuccessResponse() {
        RegisterRequest request = new RegisterRequest("john", "john@example.com", "password123");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

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

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals("john", capturedUser.getUsername());
        assertEquals("john@example.com", capturedUser.getEmail());
        assertEquals("encoded-password", capturedUser.getPassword());

        verify(userRepository).existsByUsername("john");
        verify(userRepository).existsByEmail("john@example.com");
        verify(passwordEncoder).encode("password123");
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    void register_shouldThrowWhenUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest("john", "john@example.com", "password123");

        when(userRepository.existsByUsername("john")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository).existsByUsername("john");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    void register_shouldThrowWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("john", "john@example.com", "password123");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository).existsByUsername("john");
        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    void login_shouldReturnTokenWhenUsernameIsValid() {
        LoginRequest request = new LoginRequest("john", "password123");

        User user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("encoded-password");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertTrue(response.success());
        assertEquals("Login successful", response.message());
        assertEquals("john", response.username());
        assertEquals("john@example.com", response.email());
        assertEquals("jwt-token", response.token());

        verify(userRepository).findByUsername("john");
        verify(userRepository, never()).findByEmail(anyString());
        verify(passwordEncoder).matches("password123", "encoded-password");
        verify(jwtService).generateToken(user);
    }

    @Test
    void login_shouldReturnTokenWhenEmailIsValid() {
        LoginRequest request = new LoginRequest("john@example.com", "password123");

        User user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("encoded-password");

        when(userRepository.findByUsername("john@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertTrue(response.success());
        assertEquals("Login successful", response.message());
        assertEquals("john", response.username());
        assertEquals("john@example.com", response.email());
        assertEquals("jwt-token", response.token());

        verify(userRepository).findByUsername("john@example.com");
        verify(userRepository).findByEmail("john@example.com");
        verify(passwordEncoder).matches("password123", "encoded-password");
        verify(jwtService).generateToken(user);
    }

    @Test
    void login_shouldThrowWhenUserNotFound() {
        LoginRequest request = new LoginRequest("missing", "password123");

        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("missing")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(request)
        );

        assertEquals("Invalid username/email or password", exception.getMessage());
        verify(userRepository).findByUsername("missing");
        verify(userRepository).findByEmail("missing");
        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    void login_shouldThrowWhenPasswordDoesNotMatch() {
        LoginRequest request = new LoginRequest("john", "wrong-password");

        User user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("encoded-password");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(request)
        );

        assertEquals("Invalid username/email or password", exception.getMessage());
        verify(userRepository).findByUsername("john");
        verify(userRepository, never()).findByEmail(anyString());
        verify(passwordEncoder).matches("wrong-password", "encoded-password");
        verifyNoInteractions(jwtService);
    }
}
