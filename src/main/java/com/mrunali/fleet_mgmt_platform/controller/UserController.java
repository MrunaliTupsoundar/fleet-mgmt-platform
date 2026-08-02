package com.mrunali.fleet_mgmt_platform.controller;

import com.mrunali.fleet_mgmt_platform.dto.request.CreateUserRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.InviteUserRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.SetPasswordRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.InviteUserResponseDto;
import com.mrunali.fleet_mgmt_platform.dto.response.UserResponseDto;
import com.mrunali.fleet_mgmt_platform.service.InvitationService;
import com.mrunali.fleet_mgmt_platform.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final InvitationService invitationService;

    public UserController(UserService userService, InvitationService invitationService) {
        this.userService = userService;
        this.invitationService = invitationService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody CreateUserRequestDto createUserRequestDto) {
        UserResponseDto userResponseDto = userService.createUser(createUserRequestDto);
        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable UUID id, @Valid @RequestBody CreateUserRequestDto createUserRequestDto) {
        UserResponseDto userResponseDto = userService.updateUser(id, createUserRequestDto);
        return ResponseEntity.ok(userResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/invite")
    public ResponseEntity<InviteUserResponseDto> inviteUser(@Valid @RequestBody InviteUserRequestDto inviteUserRequestDto) {
        InviteUserResponseDto inviteUserResponseDto = invitationService.inviteUser(inviteUserRequestDto);
        return ResponseEntity.ok(inviteUserResponseDto);
    }

    @PostMapping("/set-password")
    public ResponseEntity<UserResponseDto> setUserPassword(@Valid @RequestBody SetPasswordRequestDto setPasswordRequestDto) {
        UserResponseDto userResponseDto = invitationService.setUserPassword(setPasswordRequestDto);
        return ResponseEntity.ok(userResponseDto);
    }
}