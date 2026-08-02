package com.mrunali.fleet_mgmt_platform.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleAssignmentRequestDto {

    @NotNull(message = "Driver ID is required")
    private UUID driverId;

    @NotNull(message = "Vehicle ID is required")
    private UUID vehicleId;

    @NotNull(message = "Assigned From is required")
    private LocalDateTime assignedFrom;

    @NotNull(message = "Assigned To is required")
    private LocalDateTime assignedTo;
}