package com.mrunali.fleet_mgmt_platform.service;

import com.mrunali.fleet_mgmt_platform.dto.request.VehicleRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.VehicleResponseDto;
import java.util.List;
import java.util.UUID;

public interface VehicleService {
    VehicleResponseDto createVehicle(VehicleRequestDto requestDto);
    List<VehicleResponseDto> getAllVehicles();
    VehicleResponseDto getVehicleById(UUID id);
    VehicleResponseDto getVehicleByVehicleNumber(String vehicleNumber);
    VehicleResponseDto updateVehicleByVehicleNumber(String vehicleNumber, VehicleRequestDto requestDto);
    void deleteVehicleByVehicleNumber(String vehicleNumber);
}