package com.mrunali.fleet_mgmt_platform.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryRequestDto {

    @NotNull(message = "Trip ID is required")
    private UUID tripId;

    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be greater than or equal to -90.0")
    @DecimalMax(value = "90.0", message = "Latitude must be less than or equal to 90.0")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be greater than or equal to -180.0")
    @DecimalMax(value = "180.0", message = "Longitude must be less than or equal to 180.0")
    private Double longitude;

    @NotNull(message = "Speed is required")
    @DecimalMin(value = "0.0", message = "Speed must be greater than or equal to 0.0")
    private Double speed;

    @NotNull(message = "Battery Percentage is required")
    @DecimalMin(value = "0.0", message = "Battery Percentage must be greater than or equal to 0.0")
    @DecimalMax(value = "100.0", message = "Battery Percentage must be less than or equal to 100.0")
    private Double batteryPercentage;

    @NotNull(message = "Voltage is required")
    @DecimalMin(value = "0.0", message = "Voltage must be greater than or equal to 0.0")
    private Double voltage;

    @NotNull(message = "Current is required")
    @DecimalMin(value = "0.0", message = "Current must be greater than or equal to 0.0")
    private Double current;

    @NotNull(message = "Motor Temperature is required")
    @DecimalMin(value = "-40.0", message = "Motor Temperature must be greater than or equal to -40.0")
    @DecimalMax(value = "125.0", message = "Motor Temperature must be less than or equal to 125.0")
    private Double motorTemperature;

    @NotNull(message = "Controller Temperature is required")
    @DecimalMin(value = "-40.0", message = "Controller Temperature must be greater than or equal to -40.0")
    @DecimalMax(value = "125.0", message = "Controller Temperature must be less than or equal to 125.0")
    private Double controllerTemperature;

    @NotNull(message = "State of Health is required")
    @DecimalMin(value = "0.0", message = "State of Health must be greater than or equal to 0.0")
    @DecimalMax(value = "100.0", message = "State of Health must be less than or equal to 100.0")
    private Double stateOfHealth;

    @NotNull(message = "Odometer is required")
    @DecimalMin(value = "0.0", message = "Odometer must be greater than or equal to 0.0")
    private Double odometer;
}