package com.mrunali.fleet_mgmt_platform.service;

import com.mrunali.fleet_mgmt_platform.dto.request.InviteUserRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.SetPasswordRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.InviteUserResponseDto;
import com.mrunali.fleet_mgmt_platform.dto.response.UserResponseDto;

public interface InvitationService {
    InviteUserResponseDto inviteUser(InviteUserRequestDto requestDto);
    UserResponseDto setUserPassword(SetPasswordRequestDto requestDto);
}