package com.mrunali.fleet_mgmt_platform.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryResponseDto {

    private UUID id;
    private UUID tripId;
    private LocalDateTime timestamp;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private Double batteryPercentage;
    private Double voltage;
    private Double current;
    private Double motorTemperature;
    private Double controllerTemperature;
    private Double stateOfHealth;
    private Double odometer;
    private LocalDateTime createdAt;
}