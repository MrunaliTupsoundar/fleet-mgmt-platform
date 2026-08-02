package com.mrunali.fleet_mgmt_platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class InviteUserResponseDto {
    private UUID id;
    private String email;
    private String token;
}