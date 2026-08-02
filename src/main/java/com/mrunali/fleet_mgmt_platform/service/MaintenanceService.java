package com.mrunali.fleet_mgmt_platform.service;

import java.util.List;
import java.util.UUID;

import com.mrunali.fleet_mgmt_platform.dto.request.CompleteMaintenanceRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.CreateMaintenanceRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.UpdateMaintenanceRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.MaintenanceResponseDto;

public interface MaintenanceService {
    MaintenanceResponseDto createMaintenance(CreateMaintenanceRequestDto requestDto);
    List<MaintenanceResponseDto> getAllMaintenance();
    MaintenanceResponseDto getMaintenanceById(UUID id);
    MaintenanceResponseDto updateMaintenance(UUID id, UpdateMaintenanceRequestDto requestDto);
    MaintenanceResponseDto markAsInProgress(UUID id);
    MaintenanceResponseDto markAsCompleted(UUID id, CompleteMaintenanceRequestDto requestDto);
    void deleteMaintenance(UUID id);
}