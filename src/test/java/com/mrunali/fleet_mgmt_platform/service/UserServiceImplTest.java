package com.mrunali.fleet_mgmt_platform.service;

import com.mrunali.fleet_mgmt_platform.dto.request.CreateUserRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.UserResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.User;
import com.mrunali.fleet_mgmt_platform.entity.enums.Role;
import com.mrunali.fleet_mgmt_platform.entity.enums.UserStatus;
import com.mrunali.fleet_mgmt_platform.exception.UserAlreadyExistsException;
import com.mrunali.fleet_mgmt_platform.exception.UserNotFoundException;
import com.mrunali.fleet_mgmt_platform.repository.UserRepository;
import com.mrunali.fleet_mgmt_platform.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private CreateUserRequestDto createUserRequestDto;
    private UUID userId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPhone("1234567890");
        user.setRole(Role.DRIVER);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());

        createUserRequestDto = new CreateUserRequestDto();
        createUserRequestDto.setName("Test User");
        createUserRequestDto.setEmail("test@example.com");
        createUserRequestDto.setPassword("password");
        createUserRequestDto.setPhone("1234567890");
        createUserRequestDto.setRole(Role.DRIVER);
    }

    @Test
    public void testCreateUser_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto responseDto = userService.createUser(createUserRequestDto);

        assertNotNull(responseDto);
        assertEquals(user.getEmail(), responseDto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testCreateUser_UserAlreadyExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(createUserRequestDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<UserResponseDto> responseDtos = userService.getAllUsers();

        assertNotNull(responseDtos);
        assertEquals(1, responseDtos.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testGetUserById_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponseDto responseDto = userService.getUserById(userId);

        assertNotNull(responseDto);
        assertEquals(userId, responseDto.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetUserById_NotFound() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(UUID.randomUUID()));
    }

    @Test
    public void testUpdateUser_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto responseDto = userService.updateUser(userId, createUserRequestDto);

        assertNotNull(responseDto);
        assertEquals(createUserRequestDto.getEmail(), responseDto.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateUser_NotFound() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(UUID.randomUUID(), createUserRequestDto));
    }

    @Test
    public void testDeleteUser_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).save(argThat(savedUser -> savedUser.getStatus() == UserStatus.INACTIVE));
    }

    @Test
    public void testDeleteUser_NotFound() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(UUID.randomUUID()));
        verify(userRepository, never()).save(any(User.class));
    }
}