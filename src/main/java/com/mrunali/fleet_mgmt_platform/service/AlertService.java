package com.mrunali.fleet_mgmt_platform.service;

import java.util.List;
import java.util.UUID;

import com.mrunali.fleet_mgmt_platform.dto.request.AcknowledgeAlertRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.CreateAlertRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.ResolveAlertRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.AlertResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertSeverity;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertStatus;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertType;

public interface AlertService {

    List<AlertResponseDto> getAlerts();
    List<AlertResponseDto> getAlertsByVehicleId(UUID vehicleId);
    AlertResponseDto createAlert(CreateAlertRequestDto requestDto);
    List<AlertResponseDto> getAlertsByTelemetryId(UUID telemetryId);
    List<AlertResponseDto> getAlertsByStatus(AlertStatus status);
    List<AlertResponseDto> getAlertsByType(AlertType type);
    List<AlertResponseDto> getAlertsBySeverity(AlertSeverity severity);
    List<AlertResponseDto> getAlertsByStatusAndType(AlertStatus status, AlertType type);
    List<AlertResponseDto> getAlertsByStatusAndSeverity(AlertStatus status, AlertSeverity severity);
    List<AlertResponseDto> getAlertsByTypeAndSeverity(AlertType type, AlertSeverity severity);
    List<AlertResponseDto> getAlertsByStatusAndTypeAndSeverity(AlertStatus status, AlertType type, AlertSeverity severity);
    AlertResponseDto acknowledgeAlert(AcknowledgeAlertRequestDto requestDto);
    AlertResponseDto resolveAlert(ResolveAlertRequestDto requestDto);
    List<AlertResponseDto> getAlertsByAcknowledgedById(UUID userId);
    List<AlertResponseDto> getAlertsByResolvedById(UUID userId);

}