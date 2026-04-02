package com.example.demo.services;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest request) {
        log.info("Register request received for username={} email={}", request.username(), request.email());

        if (userRepository.existsByUsername(request.username())) {
            log.warn("Registration failed: username already exists: {}", request.username());
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Registration failed: email already exists: {}", request.email());
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        User saved = userRepository.save(user);
        log.info("User registered successfully: username={} email={} id={}", saved.getUsername(), saved.getEmail(), saved.getId());
        return new AuthResponse(true, "Registration successful", saved.getUsername(), saved.getEmail(), null);
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login request received for identifier={}", request.usernameOrEmail());

        User user = userRepository.findByUsername(request.usernameOrEmail())
                .or(() -> userRepository.findByEmail(request.usernameOrEmail()))
                .orElseThrow(() -> {
                    log.warn("Login failed: user not found for identifier={}", request.usernameOrEmail());
                    return new IllegalArgumentException("Invalid username/email or password");
                });

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Login failed: password mismatch for username={} email={}", user.getUsername(), user.getEmail());
            throw new IllegalArgumentException("Invalid username/email or password");
        }

        String token = jwtService.generateToken(user);
        log.info("Login successful for username={} email={}", user.getUsername(), user.getEmail());
        return new AuthResponse(true, "Login successful", user.getUsername(), user.getEmail(), token);
    }
}
