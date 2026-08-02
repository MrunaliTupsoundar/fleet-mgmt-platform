package com.mrunali.fleet_mgmt_platform.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mrunali.fleet_mgmt_platform.dto.request.AcknowledgeAlertRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.CreateAlertRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.ResolveAlertRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.AlertResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.Alert;
import com.mrunali.fleet_mgmt_platform.entity.Telemetry;
import com.mrunali.fleet_mgmt_platform.entity.User;
import com.mrunali.fleet_mgmt_platform.entity.Vehicle;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertSeverity;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertStatus;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertType;
import com.mrunali.fleet_mgmt_platform.exception.TelemetryNotFoundException;
import com.mrunali.fleet_mgmt_platform.repository.AlertRepository;
import com.mrunali.fleet_mgmt_platform.repository.TelemetryRepository;
import com.mrunali.fleet_mgmt_platform.repository.UserRepository;
import com.mrunali.fleet_mgmt_platform.service.AlertService;
import com.mrunali.fleet_mgmt_platform.exception.AlertNotAcknowledgedException;
import com.mrunali.fleet_mgmt_platform.exception.AlertNotActiveException;
import com.mrunali.fleet_mgmt_platform.exception.AlertNotFoundException;
import com.mrunali.fleet_mgmt_platform.exception.UserNotFoundException;

@Service
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final TelemetryRepository telemetryRepository;
    private final UserRepository userRepository;

    public AlertServiceImpl(AlertRepository alertRepository, TelemetryRepository telemetryRepository, UserRepository userRepository) {
        this.alertRepository = alertRepository;
        this.telemetryRepository = telemetryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<AlertResponseDto> getAlerts() {
        List<Alert> alerts = alertRepository.findAll();
        return alerts.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AlertResponseDto createAlert(CreateAlertRequestDto requestDto) {
        Telemetry telemetry = telemetryRepository.findById(requestDto.getTelemetryId())
                .orElseThrow(() -> new TelemetryNotFoundException("Telemetry not found"));

        Vehicle vehicle = telemetry.getTrip().getVehicle();

        Alert alert = new Alert();
        alert.setVehicle(vehicle);
        alert.setTelemetry(telemetry);
        alert.setType(requestDto.getType());
        alert.setSeverity(requestDto.getSeverity());
        alert.setStatus(AlertStatus.ACTIVE);
        alert.setMessage(requestDto.getMessage());

        Alert savedAlert = alertRepository.save(alert);

        return mapToResponseDto(savedAlert);
    }

    @Override
    public List<AlertResponseDto> getAlertsByVehicleId(UUID vehicleId) {
        List<Alert> alerts = alertRepository.findByVehicleId(vehicleId);
        return alerts.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlertResponseDto> getAlertsByTelemetryId(UUID telemetryId) {
        List<Alert> alerts = alertRepository.findByTelemetryId(telemetryId);
        return alerts.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlertResponseDto> getAlertsByStatus(AlertStatus status) {
        List<Alert> alerts = alertRepository.findByStatus(status);
        return alerts.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlertResponseDto> getAlertsByType(AlertType type) {
        List<Alert> alerts = alertRepository.findByType(type);
        return alerts.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlertResponseDto> getAlertsBySeverity(AlertSeverity severity) {
        List<Alert> alerts = alertRepository.findBySeverity(severity);
        return alerts.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlertResponseDto> getAlertsByStatusAndType(AlertStatus status, AlertType type) {
        List<Alert> alerts = alertRepository.findByStatusAndType(status, type);
        return alerts.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlertResponseDto> getAlertsByStatusAndSeverity(AlertStatus status, AlertSeverity severity) {
        List<Alert> alerts = alertRepository.findByStatusAndSeverity(status, severity);
        return alerts.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlertResponseDto> getAlertsByTypeAndSeverity(AlertType type, AlertSeverity severity) {
        List<Alert> alerts = alertRepository.findByTypeAndSeverity(type, severity);
        return alerts.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlertResponseDto> getAlertsByStatusAndTypeAndSeverity(AlertStatus status, AlertType type, AlertSeverity severity) {
        List<Alert> alerts = alertRepository.findByStatusAndTypeAndSeverity(status, type, severity);
        return alerts.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AlertResponseDto acknowledgeAlert(AcknowledgeAlertRequestDto requestDto) {
        Alert alert = alertRepository.findById(requestDto.getAlertId())
                .orElseThrow(() -> new AlertNotFoundException("Alert not found"));

        if (alert.getStatus() != AlertStatus.ACTIVE) {
            throw new AlertNotActiveException("Alert is not active");
        }

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        alert.setStatus(AlertStatus.ACKNOWLEDGED);
        alert.setAcknowledgedBy(user);
        alert.setAcknowledgedAt(LocalDateTime.now());

        Alert savedAlert = alertRepository.save(alert);

        return mapToResponseDto(savedAlert);
    }

    @Override
    @Transactional
    public AlertResponseDto resolveAlert(ResolveAlertRequestDto requestDto) {
        Alert alert = alertRepository.findById(requestDto.getAlertId())
                .orElseThrow(() -> new AlertNotFoundException("Alert not found"));

        if (alert.getStatus() != AlertStatus.ACKNOWLEDGED) {
            throw new AlertNotAcknowledgedException("Alert is not acknowledged");
        }

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        alert.setStatus(AlertStatus.RESOLVED);
        alert.setResolvedBy(user);
        alert.setResolvedAt(LocalDateTime.now());

        Alert savedAlert = alertRepository.save(alert);

        return mapToResponseDto(savedAlert);
    }

    @Override
    public List<AlertResponseDto> getAlertsByAcknowledgedById(UUID userId) {
        List<Alert> alerts = alertRepository.findByAcknowledgedById(userId);
        return alerts.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlertResponseDto> getAlertsByResolvedById(UUID userId) {
        List<Alert> alerts = alertRepository.findByResolvedById(userId);
        return alerts.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private AlertResponseDto mapToResponseDto(Alert alert) {
        AlertResponseDto responseDto = new AlertResponseDto();
        responseDto.setId(alert.getId());
        responseDto.setVehicleId(alert.getVehicle().getId());
        responseDto.setTelemetryId(alert.getTelemetry().getId());
        responseDto.setType(alert.getType());
        responseDto.setSeverity(alert.getSeverity());
        responseDto.setStatus(alert.getStatus());
        responseDto.setMessage(alert.getMessage());
        responseDto.setCreatedAt(alert.getCreatedAt());
        responseDto.setAcknowledgedAt(alert.getAcknowledgedAt());
        responseDto.setResolvedAt(alert.getResolvedAt());

        if (alert.getAcknowledgedBy() != null) {
            responseDto.setAcknowledgedById(alert.getAcknowledgedBy().getId());
        }

        if (alert.getResolvedBy() != null) {
            responseDto.setResolvedById(alert.getResolvedBy().getId());
        }

        return responseDto;
    }
}