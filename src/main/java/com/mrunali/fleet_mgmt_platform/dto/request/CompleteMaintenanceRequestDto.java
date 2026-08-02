package com.mrunali.fleet_mgmt_platform.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompleteMaintenanceRequestDto {

    @NotNull(message = "Remarks cannot be null")
    private String remarks;
    
}
