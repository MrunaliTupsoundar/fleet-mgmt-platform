package com.mrunali.fleet_mgmt_platform.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mrunali.fleet_mgmt_platform.entity.enums.MaintenanceStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaintenanceResponseDto {
    private UUID id;
    private UUID vehicleId;
    private UUID reportedById;
    private String description;
    private String remarks;
    private MaintenanceStatus status;
    private LocalDate scheduledDate;
    private LocalDateTime completedDate;
    private LocalDateTime createdAt;
}