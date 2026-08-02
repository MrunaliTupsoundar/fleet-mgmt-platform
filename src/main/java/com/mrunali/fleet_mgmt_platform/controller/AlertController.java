package com.mrunali.fleet_mgmt_platform.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mrunali.fleet_mgmt_platform.dto.request.AcknowledgeAlertRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.CreateAlertRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.ResolveAlertRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.AlertResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertSeverity;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertStatus;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertType;
import com.mrunali.fleet_mgmt_platform.service.AlertService;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<AlertResponseDto> createAlert(@RequestBody CreateAlertRequestDto requestDto) {
        AlertResponseDto responseDto = alertService.createAlert(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<AlertResponseDto>> getAlerts() {
        List<AlertResponseDto> alerts = alertService.getAlerts();
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    @GetMapping("/vehicle/{vehicleId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<AlertResponseDto>> getAlertsByVehicleId(@PathVariable UUID vehicleId) {
        List<AlertResponseDto> alerts = alertService.getAlertsByVehicleId(vehicleId);
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    @GetMapping("/telemetry/{telemetryId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') ")
    public ResponseEntity<List<AlertResponseDto>> getAlertsByTelemetryId(@PathVariable UUID telemetryId) {
        List<AlertResponseDto> alerts = alertService.getAlertsByTelemetryId(telemetryId);
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<AlertResponseDto>> getAlertsByStatus(@PathVariable AlertStatus status) {
        List<AlertResponseDto> alerts = alertService.getAlertsByStatus(status);
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<AlertResponseDto>> getAlertsByType(@PathVariable AlertType type) {
        List<AlertResponseDto> alerts = alertService.getAlertsByType(type);
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    @GetMapping("/severity/{severity}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<AlertResponseDto>> getAlertsBySeverity(@PathVariable AlertSeverity severity) {
        List<AlertResponseDto> alerts = alertService.getAlertsBySeverity(severity);
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    @GetMapping("/status/{status}/type/{type}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<AlertResponseDto>> getAlertsByStatusAndType(@PathVariable AlertStatus status, @PathVariable AlertType type) {
        List<AlertResponseDto> alerts = alertService.getAlertsByStatusAndType(status, type);
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    @GetMapping("/status/{status}/severity/{severity}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<AlertResponseDto>> getAlertsByStatusAndSeverity(@PathVariable AlertStatus status, @PathVariable AlertSeverity severity) {
        List<AlertResponseDto> alerts = alertService.getAlertsByStatusAndSeverity(status, severity);
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    @GetMapping("/type/{type}/severity/{severity}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<AlertResponseDto>> getAlertsByTypeAndSeverity(@PathVariable AlertType type, @PathVariable AlertSeverity severity) {
        List<AlertResponseDto> alerts = alertService.getAlertsByTypeAndSeverity(type, severity);
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    @GetMapping("/status/{status}/type/{type}/severity/{severity}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<AlertResponseDto>> getAlertsByStatusAndTypeAndSeverity(@PathVariable AlertStatus status, @PathVariable AlertType type, @PathVariable AlertSeverity severity) {
        List<AlertResponseDto> alerts = alertService.getAlertsByStatusAndTypeAndSeverity(status, type, severity);
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    @PostMapping("/acknowledge")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<AlertResponseDto> acknowledgeAlert(@RequestBody AcknowledgeAlertRequestDto requestDto) {
        AlertResponseDto responseDto = alertService.acknowledgeAlert(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/resolve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<AlertResponseDto> resolveAlert(@RequestBody ResolveAlertRequestDto requestDto) {
        AlertResponseDto responseDto = alertService.resolveAlert(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/acknowledged-by/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<AlertResponseDto>> getAlertsByAcknowledgedById(@PathVariable UUID userId) {
        List<AlertResponseDto> alerts = alertService.getAlertsByAcknowledgedById(userId);
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    @GetMapping("/resolved-by/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<AlertResponseDto>> getAlertsByResolvedById(@PathVariable UUID userId) {
        List<AlertResponseDto> alerts = alertService.getAlertsByResolvedById(userId);
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }
}