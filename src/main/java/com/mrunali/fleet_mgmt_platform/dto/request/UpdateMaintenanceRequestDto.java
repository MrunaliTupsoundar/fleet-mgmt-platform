package com.mrunali.fleet_mgmt_platform.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMaintenanceRequestDto {

    @NotNull(message = "Description cannot be null")
    private String description;

    @NotNull(message = "Scheduled Date cannot be null")
    private LocalDate scheduledDate;

}
