package com.mrunali.fleet_mgmt_platform.service;

import com.mrunali.fleet_mgmt_platform.dto.request.LoginRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.LoginResponseDto;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto request);
}