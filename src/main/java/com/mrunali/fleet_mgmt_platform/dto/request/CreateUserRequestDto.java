package com.mrunali.fleet_mgmt_platform.dto.request;

import com.mrunali.fleet_mgmt_platform.entity.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestDto {

    @NotBlank
    private String name;
    
    @NotBlank
    @Email
    private String email;
    
    private String password;
    
    @NotBlank
    private String phone;
    
    @NotNull
    private Role role;
}
