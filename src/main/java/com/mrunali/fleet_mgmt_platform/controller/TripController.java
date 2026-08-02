package com.mrunali.fleet_mgmt_platform.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.mrunali.fleet_mgmt_platform.dto.request.StartTripRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.EndTripRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.TripResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.enums.TripStatus;
import com.mrunali.fleet_mgmt_platform.service.TripService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping("/start")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<TripResponseDto> startTrip(@Valid @RequestBody StartTripRequestDto requestDto) {
        TripResponseDto responseDto = tripService.startTrip(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("/end")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<TripResponseDto> endTrip(@Valid @RequestBody EndTripRequestDto requestDto) {
        TripResponseDto responseDto = tripService.endTrip(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TripResponseDto>> getAllTrips() {
        List<TripResponseDto> responseDtos = tripService.getAllTrips();
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TripResponseDto> getTripById(@PathVariable UUID id) {
        TripResponseDto responseDto = tripService.getTripById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/driver/{driverId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TripResponseDto>> getTripsByDriverId(@PathVariable UUID driverId) {
        List<TripResponseDto> responseDtos = tripService.getTripsByDriverId(driverId);
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @GetMapping("/vehicle/{vehicleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TripResponseDto>> getTripsByVehicleId(@PathVariable UUID vehicleId) {
        List<TripResponseDto> responseDtos = tripService.getTripsByVehicleId(vehicleId);
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TripResponseDto>> getTripsByStatus(@PathVariable TripStatus status) {
        List<TripResponseDto> responseDtos = tripService.getTripsByStatus(status);
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @GetMapping("/driver/{driverId}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TripResponseDto>> getTripsByDriverIdAndStatus(@PathVariable UUID driverId, @PathVariable TripStatus status) {
        List<TripResponseDto> responseDtos = tripService.getTripsByDriverIdAndStatus(driverId, status);
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @GetMapping("/vehicle/{vehicleId}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TripResponseDto>> getTripsByVehicleIdAndStatus(@PathVariable UUID vehicleId, @PathVariable TripStatus status) {
        List<TripResponseDto> responseDtos = tripService.getTripsByVehicleIdAndStatus(vehicleId, status);
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }
}