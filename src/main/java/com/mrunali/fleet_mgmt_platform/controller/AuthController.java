package com.mrunali.fleet_mgmt_platform.controller;

import com.mrunali.fleet_mgmt_platform.dto.request.LoginRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.LoginResponseDto;
import com.mrunali.fleet_mgmt_platform.service.AuthService;
import com.mrunali.fleet_mgmt_platform.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = authService.login(loginRequestDto);
        return ResponseEntity.ok(loginResponseDto);
    }
}