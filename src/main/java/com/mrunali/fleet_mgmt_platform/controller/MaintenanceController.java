package com.mrunali.fleet_mgmt_platform.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mrunali.fleet_mgmt_platform.dto.request.CompleteMaintenanceRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.CreateMaintenanceRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.UpdateMaintenanceRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.MaintenanceResponseDto;
import com.mrunali.fleet_mgmt_platform.service.MaintenanceService;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<MaintenanceResponseDto> createMaintenance(@RequestBody CreateMaintenanceRequestDto requestDto) {
        MaintenanceResponseDto responseDto = maintenanceService.createMaintenance(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<MaintenanceResponseDto>> getAllMaintenance() {
        List<MaintenanceResponseDto> maintenanceList = maintenanceService.getAllMaintenance();
        return new ResponseEntity<>(maintenanceList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<MaintenanceResponseDto> getMaintenanceById(@PathVariable UUID id) {
        MaintenanceResponseDto responseDto = maintenanceService.getMaintenanceById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<MaintenanceResponseDto> updateMaintenance(@PathVariable UUID id, @RequestBody UpdateMaintenanceRequestDto requestDto) {
        MaintenanceResponseDto responseDto = maintenanceService.updateMaintenance(id, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/{id}/in-progress")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<MaintenanceResponseDto> markAsInProgress(@PathVariable UUID id) {
        MaintenanceResponseDto responseDto = maintenanceService.markAsInProgress(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/{id}/completed")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<MaintenanceResponseDto> markAsCompleted(@PathVariable UUID id, @RequestBody CompleteMaintenanceRequestDto requestDto) {
        MaintenanceResponseDto responseDto = maintenanceService.markAsCompleted(id, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> deleteMaintenance(@PathVariable UUID id) {
        maintenanceService.deleteMaintenance(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}