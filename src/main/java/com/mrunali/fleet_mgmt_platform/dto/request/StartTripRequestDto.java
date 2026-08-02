package com.mrunali.fleet_mgmt_platform.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartTripRequestDto {

    @NotNull(message = "Vehicle ID is required")
    private UUID vehicleId;

    @NotNull(message = "Start Time is required")
    private LocalDateTime startTime;

    @NotNull(message = "Start Odometer is required")
    private Double startOdometer;

    @NotNull(message = "Start Battery Percentage is required")
    private Double startBatteryPercentage;
}