package com.mrunali.fleet_mgmt_platform.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.mrunali.fleet_mgmt_platform.dto.request.TelemetryRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.TelemetryResponseDto;
import com.mrunali.fleet_mgmt_platform.service.TelemetryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/telemetry")
public class TelemetryController {

    private final TelemetryService telemetryService;

    public TelemetryController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @PostMapping
    public ResponseEntity<TelemetryResponseDto> ingestTelemetry(@Valid @RequestBody TelemetryRequestDto requestDto) {
        TelemetryResponseDto responseDto = telemetryService.ingestTelemetry(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/trip/{tripId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TelemetryResponseDto>> getTripTelemetry(@PathVariable UUID tripId) {
        List<TelemetryResponseDto> responseDtos = telemetryService.getTripTelemetry(tripId);
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @GetMapping("/latest/{tripId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TelemetryResponseDto> getLatestTelemetry(@PathVariable UUID tripId) {
        TelemetryResponseDto responseDto = telemetryService.getLatestTelemetry(tripId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}