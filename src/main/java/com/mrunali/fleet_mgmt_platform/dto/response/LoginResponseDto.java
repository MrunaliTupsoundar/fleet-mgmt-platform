package com.mrunali.fleet_mgmt_platform.dto.response;

import com.mrunali.fleet_mgmt_platform.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private Role role;
}