package com.mrunali.fleet_mgmt_platform.controller;

import com.mrunali.fleet_mgmt_platform.dto.request.VehicleRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.VehicleResponseDto;
import com.mrunali.fleet_mgmt_platform.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponseDto> createVehicle(@Valid @RequestBody VehicleRequestDto requestDto) {
        VehicleResponseDto responseDto = vehicleService.createVehicle(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<VehicleResponseDto>> getAllVehicles() {
        List<VehicleResponseDto> responseDtos = vehicleService.getAllVehicles();
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<VehicleResponseDto> getVehicleById(@PathVariable UUID id) {
        VehicleResponseDto responseDto = vehicleService.getVehicleById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/number/{vehicleNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<VehicleResponseDto> getVehicleByVehicleNumber(@PathVariable String vehicleNumber) {
        VehicleResponseDto responseDto = vehicleService.getVehicleByVehicleNumber(vehicleNumber);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/{vehicleNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponseDto> updateVehicle(
            @PathVariable String vehicleNumber,
            @Valid @RequestBody VehicleRequestDto requestDto) {
        VehicleResponseDto responseDto = vehicleService.updateVehicleByVehicleNumber(vehicleNumber, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{vehicleNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVehicle(@PathVariable String vehicleNumber) {
        vehicleService.deleteVehicleByVehicleNumber(vehicleNumber);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}