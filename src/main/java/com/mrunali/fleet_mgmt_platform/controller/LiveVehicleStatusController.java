package com.mrunali.fleet_mgmt_platform.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.mrunali.fleet_mgmt_platform.dto.response.LiveVehicleStatusResponseDto;
import com.mrunali.fleet_mgmt_platform.service.LiveVehicleStatusService;

@RestController
@RequestMapping("/api/live-vehicle-status")
public class LiveVehicleStatusController {

    private final LiveVehicleStatusService liveVehicleStatusService;

    public LiveVehicleStatusController(LiveVehicleStatusService liveVehicleStatusService) {
        this.liveVehicleStatusService = liveVehicleStatusService;
    }

    @GetMapping("/vehicle/{vehicleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<LiveVehicleStatusResponseDto> getLiveVehicleStatus(@PathVariable UUID vehicleId) {
        LiveVehicleStatusResponseDto responseDto = liveVehicleStatusService.getLiveVehicleStatus(vehicleId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<LiveVehicleStatusResponseDto>> getAllLiveVehicleStatuses() {
        List<LiveVehicleStatusResponseDto> responseDtos = liveVehicleStatusService.getAllLiveVehicleStatuses();
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @GetMapping("/connected")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<LiveVehicleStatusResponseDto>> getConnectedVehicles() {
        List<LiveVehicleStatusResponseDto> responseDtos = liveVehicleStatusService.getConnectedVehicles();
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @GetMapping("/disconnected")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<LiveVehicleStatusResponseDto>> getDisconnectedVehicles() {
        List<LiveVehicleStatusResponseDto> responseDtos = liveVehicleStatusService.getDisconnectedVehicles();
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }
}