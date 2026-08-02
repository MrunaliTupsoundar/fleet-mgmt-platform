package com.mrunali.fleet_mgmt_platform.service.impl;

import com.mrunali.fleet_mgmt_platform.dto.request.LoginRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.LoginResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.User;
import com.mrunali.fleet_mgmt_platform.exception.InvalidCredentialsException;
import com.mrunali.fleet_mgmt_platform.exception.TokenGenerationFailedException;
import com.mrunali.fleet_mgmt_platform.exception.UserNotFoundException;
import com.mrunali.fleet_mgmt_platform.repository.UserRepository;
import com.mrunali.fleet_mgmt_platform.security.JwtService;
import com.mrunali.fleet_mgmt_platform.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        User loginUser = user.get();
        if (!passwordEncoder.matches(request.getPassword(), loginUser.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String token;
        try {
            token = jwtService.generateToken(loginUser);
        } catch (Exception e) {
            throw new TokenGenerationFailedException("Token generation failed");
        }

        System.out.println("Generated Token: " + token); // Log the token
        return new LoginResponseDto(token, loginUser.getRole());
    }
}