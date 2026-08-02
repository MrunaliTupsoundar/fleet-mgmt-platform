package com.mrunali.fleet_mgmt_platform.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.mrunali.fleet_mgmt_platform.entity.enums.TripStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripResponseDto {

    private UUID id;
    private UUID assignmentId;
    private UUID driverId;
    private UUID vehicleId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double startOdometer;
    private Double endOdometer;
    private Double startBatteryPercentage;
    private Double endBatteryPercentage;
    private Double distance;
    private Double batteryUsed;
    private Double averageSpeed;
    private Double maxSpeed;
    private TripStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}