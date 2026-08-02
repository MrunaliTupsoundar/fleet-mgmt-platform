package com.mrunali.fleet_mgmt_platform.service;

import java.util.List;
import java.util.UUID;

import com.mrunali.fleet_mgmt_platform.dto.request.TelemetryRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.TelemetryResponseDto;

public interface TelemetryService {

    TelemetryResponseDto ingestTelemetry(TelemetryRequestDto requestDto);
    List<TelemetryResponseDto> getTripTelemetry(UUID tripId);
    TelemetryResponseDto getLatestTelemetry(UUID tripId);
    
}