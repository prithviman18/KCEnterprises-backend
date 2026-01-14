package com.KC.Enterprises.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.KC.Enterprises.dto.LoginRequest;
import com.KC.Enterprises.dto.RegisterRequest;
import com.KC.Enterprises.entity.User;
import com.KC.Enterprises.repository.UserRepository;
import com.KC.Enterprises.service.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public String register(RegisterRequest request) {
        User user = User.builder()
            .email(request.getEmail())
            .username(request.getUsername())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .phone(request.getPhone())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .build();

        userRepository.save(user);

        return jwtService.generateToken(user);
    }

    public String login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),  // This should match UserDetailsService
                    request.getPassword()
                )
            );
            
            // Load user by email to be consistent
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found after authentication"));
    
            return jwtService.generateToken(user);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid email or password");
        }
    }
}
