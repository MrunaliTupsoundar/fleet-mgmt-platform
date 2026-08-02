package com.mrunali.fleet_mgmt_platform.dto.response;

import com.mrunali.fleet_mgmt_platform.entity.enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponseDto {

    private UUID id;
    private String vehicleNumber;
    private String type;
    private String manufacturer;
    private String model;
    private Integer manufactureYear;
    private Double batteryCapacity;
    private VehicleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}