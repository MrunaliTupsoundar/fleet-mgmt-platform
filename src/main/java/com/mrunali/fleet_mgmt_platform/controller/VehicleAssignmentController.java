package com.mrunali.fleet_mgmt_platform.controller;

import com.mrunali.fleet_mgmt_platform.dto.request.VehicleAssignmentRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.VehicleAssignmentResponseDto;
import com.mrunali.fleet_mgmt_platform.service.VehicleAssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assignments")
public class VehicleAssignmentController {

    private final VehicleAssignmentService vehicleAssignmentService;

    public VehicleAssignmentController(VehicleAssignmentService vehicleAssignmentService) {
        this.vehicleAssignmentService = vehicleAssignmentService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<VehicleAssignmentResponseDto> createAssignment(@Valid @RequestBody VehicleAssignmentRequestDto requestDto) {
        VehicleAssignmentResponseDto responseDto = vehicleAssignmentService.createAssignment(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<VehicleAssignmentResponseDto>> getAllAssignments() {
        List<VehicleAssignmentResponseDto> responseDtos = vehicleAssignmentService.getAllAssignments();
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<VehicleAssignmentResponseDto> getAssignmentById(@PathVariable UUID id) {
        VehicleAssignmentResponseDto responseDto = vehicleAssignmentService.getAssignmentById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/driver/{driverId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<VehicleAssignmentResponseDto>> getAssignmentsByDriverId(@PathVariable UUID driverId) {
        List<VehicleAssignmentResponseDto> responseDtos = vehicleAssignmentService.getAssignmentsByDriverId(driverId);
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @GetMapping("/vehicle/{vehicleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<VehicleAssignmentResponseDto>> getAssignmentsByVehicleId(@PathVariable UUID vehicleId) {
        List<VehicleAssignmentResponseDto> responseDtos = vehicleAssignmentService.getAssignmentsByVehicleId(vehicleId);
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @GetMapping("/assigned-by/{assignedById}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<VehicleAssignmentResponseDto>> getAssignmentsByAssignedById(@PathVariable UUID assignedById) {
        List<VehicleAssignmentResponseDto> responseDtos = vehicleAssignmentService.getAssignmentsByAssignedById(assignedById);
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<VehicleAssignmentResponseDto> updateAssignment(
            @PathVariable UUID id,
            @Valid @RequestBody VehicleAssignmentRequestDto requestDto) {
        VehicleAssignmentResponseDto responseDto = vehicleAssignmentService.updateAssignment(id, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteAssignment(@PathVariable UUID id) {
        vehicleAssignmentService.deleteAssignment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}