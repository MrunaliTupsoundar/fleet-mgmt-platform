package com.mrunali.fleet_mgmt_platform.service;

import java.util.List;
import java.util.UUID;
import com.mrunali.fleet_mgmt_platform.dto.request.CreateUserRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.UserResponseDto;

public interface UserService {
    UserResponseDto createUser(CreateUserRequestDto requestDto);
    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(UUID id);
    UserResponseDto updateUser(UUID id, CreateUserRequestDto requestDto);
    void deleteUser(UUID id);
}