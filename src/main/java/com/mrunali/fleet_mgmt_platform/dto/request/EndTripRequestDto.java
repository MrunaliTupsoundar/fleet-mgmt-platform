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
public class EndTripRequestDto {

    @NotNull(message = "Trip ID is required")
    private UUID tripId;

    @NotNull(message = "End Time is required")
    private LocalDateTime endTime;

    @NotNull(message = "End Odometer is required")
    private Double endOdometer;

    @NotNull(message = "End Battery Percentage is required")
    private Double endBatteryPercentage;
}