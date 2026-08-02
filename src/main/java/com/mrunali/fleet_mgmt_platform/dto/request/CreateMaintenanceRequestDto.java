package com.mrunali.fleet_mgmt_platform.dto.request;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMaintenanceRequestDto {

    @NotNull(message = "Vehicle ID cannot be null")
    private UUID vehicleId;

    @NotNull(message = "Reported By ID cannot be null")
    private UUID reportedById;

    @NotNull(message = "Description cannot be null")
    private String description;

    @NotNull(message = "Scheduled Date cannot be null")
    private LocalDate scheduledDate;
}