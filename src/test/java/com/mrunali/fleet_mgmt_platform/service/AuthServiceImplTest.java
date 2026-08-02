package com.mrunali.fleet_mgmt_platform.service;

import com.mrunali.fleet_mgmt_platform.dto.request.LoginRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.LoginResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.User;
import com.mrunali.fleet_mgmt_platform.entity.enums.Role;
import com.mrunali.fleet_mgmt_platform.exception.InvalidCredentialsException;
import com.mrunali.fleet_mgmt_platform.exception.TokenGenerationFailedException;
import com.mrunali.fleet_mgmt_platform.exception.UserNotFoundException;
import com.mrunali.fleet_mgmt_platform.repository.UserRepository;
import com.mrunali.fleet_mgmt_platform.security.JwtService;
import com.mrunali.fleet_mgmt_platform.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private LoginRequestDto loginRequestDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.ADMIN);

        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("test@example.com");
        loginRequestDto.setPassword("password");
    }

    @Test
    public void testLogin_Success() {
        when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("test-token");

        LoginResponseDto response = authService.login(loginRequestDto);

        assertNotNull(response);
        assertEquals("test-token", response.getToken());
        assertEquals(Role.ADMIN, response.getRole());

        verify(userRepository, times(1)).findByEmail(loginRequestDto.getEmail());
        verify(passwordEncoder, times(1)).matches(loginRequestDto.getPassword(), user.getPassword());
        verify(jwtService, times(1)).generateToken(user);
    }

    @Test
    public void testLogin_UserNotFound() {
        when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.login(loginRequestDto));

        verify(userRepository, times(1)).findByEmail(loginRequestDto.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    public void testLogin_InvalidCredentials() {
        when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginRequestDto));

        verify(userRepository, times(1)).findByEmail(loginRequestDto.getEmail());
        verify(passwordEncoder, times(1)).matches(loginRequestDto.getPassword(), user.getPassword());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    public void testLogin_TokenGenerationFailed() {
        when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user)).thenThrow(new RuntimeException("JWT error"));

        assertThrows(TokenGenerationFailedException.class, () -> authService.login(loginRequestDto));

        verify(userRepository, times(1)).findByEmail(loginRequestDto.getEmail());
        verify(passwordEncoder, times(1)).matches(loginRequestDto.getPassword(), user.getPassword());
        verify(jwtService, times(1)).generateToken(user);
    }
}