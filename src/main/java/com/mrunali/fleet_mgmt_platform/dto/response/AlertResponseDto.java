package com.mrunali.fleet_mgmt_platform.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.mrunali.fleet_mgmt_platform.entity.enums.AlertSeverity;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertStatus;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertResponseDto {

    private UUID id;
    private UUID vehicleId;
    private UUID telemetryId;
    private AlertType type;
    private AlertSeverity severity;
    private AlertStatus status;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime acknowledgedAt;
    private LocalDateTime resolvedAt;
    private UUID acknowledgedById;
    private UUID resolvedById;
}