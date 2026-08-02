package com.mrunali.fleet_mgmt_platform.service;

import java.util.List;
import java.util.UUID;

import com.mrunali.fleet_mgmt_platform.dto.response.LiveVehicleStatusResponseDto;

public interface LiveVehicleStatusService {

    LiveVehicleStatusResponseDto getLiveVehicleStatus(UUID vehicleId);
    List<LiveVehicleStatusResponseDto> getAllLiveVehicleStatuses();
    List<LiveVehicleStatusResponseDto> getConnectedVehicles();
    List<LiveVehicleStatusResponseDto> getDisconnectedVehicles();
    
}