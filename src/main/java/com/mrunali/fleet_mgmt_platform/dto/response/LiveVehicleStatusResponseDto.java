package com.mrunali.fleet_mgmt_platform.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.mrunali.fleet_mgmt_platform.entity.enums.ConnectionStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiveVehicleStatusResponseDto {

    private UUID id;
    private UUID vehicleId;
    private Double latitude;
    private Double longitude;
    private Double batteryPercentage;
    private Double stateOfHealth;
    private Double odometer;
    private LocalDateTime lastSeen;
    private ConnectionStatus connectionStatus;
}