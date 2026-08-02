package com.mrunali.fleet_mgmt_platform.dto.request;

import java.util.UUID;

import com.mrunali.fleet_mgmt_platform.entity.enums.AlertSeverity;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAlertRequestDto {

    @NotNull(message = "Vehicle ID is required")
    private UUID vehicleId;
    
    @NotNull(message = "Telemetry ID is required")
    private UUID telemetryId;

    @NotNull(message = "Alert type is required")
    private AlertType type;

    @NotNull(message = "Alert severity is required")
    private AlertSeverity severity;

    @NotNull(message = "Alert message is required")
    private String message;
}