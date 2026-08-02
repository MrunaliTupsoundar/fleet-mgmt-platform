package com.mrunali.fleet_mgmt_platform.service;

import com.mrunali.fleet_mgmt_platform.dto.request.VehicleAssignmentRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.VehicleAssignmentResponseDto;

import java.util.List;
import java.util.UUID;

public interface VehicleAssignmentService {

    VehicleAssignmentResponseDto createAssignment(VehicleAssignmentRequestDto requestDto);
    List<VehicleAssignmentResponseDto> getAllAssignments();
    VehicleAssignmentResponseDto getAssignmentById(UUID id);
    List<VehicleAssignmentResponseDto> getAssignmentsByDriverId(UUID driverId);
    List<VehicleAssignmentResponseDto> getAssignmentsByVehicleId(UUID vehicleId);
    List<VehicleAssignmentResponseDto> getAssignmentsByAssignedById(UUID assignedById);
    VehicleAssignmentResponseDto updateAssignment(UUID id, VehicleAssignmentRequestDto requestDto);
    void deleteAssignment(UUID id);
    
}