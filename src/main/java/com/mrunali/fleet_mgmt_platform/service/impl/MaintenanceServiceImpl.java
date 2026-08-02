package com.mrunali.fleet_mgmt_platform.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mrunali.fleet_mgmt_platform.dto.request.CompleteMaintenanceRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.CreateMaintenanceRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.UpdateMaintenanceRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.MaintenanceResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.Maintenance;
import com.mrunali.fleet_mgmt_platform.entity.User;
import com.mrunali.fleet_mgmt_platform.entity.Vehicle;
import com.mrunali.fleet_mgmt_platform.entity.enums.MaintenanceStatus;
import com.mrunali.fleet_mgmt_platform.exception.MaintenanceNotFoundException;
import com.mrunali.fleet_mgmt_platform.exception.UserNotFoundException;
import com.mrunali.fleet_mgmt_platform.exception.VehicleNotFoundException;
import com.mrunali.fleet_mgmt_platform.repository.MaintenanceRepository;
import com.mrunali.fleet_mgmt_platform.repository.UserRepository;
import com.mrunali.fleet_mgmt_platform.repository.VehicleRepository;
import com.mrunali.fleet_mgmt_platform.service.MaintenanceService;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public MaintenanceServiceImpl(MaintenanceRepository maintenanceRepository, VehicleRepository vehicleRepository, UserRepository userRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public MaintenanceResponseDto createMaintenance(CreateMaintenanceRequestDto requestDto) {
        Vehicle vehicle = vehicleRepository.findById(requestDto.getVehicleId())
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found"));

        User reportedBy = userRepository.findById(requestDto.getReportedById())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Maintenance maintenance = Maintenance.builder()
                .vehicle(vehicle)
                .reportedBy(reportedBy)
                .description(requestDto.getDescription())
                .status(MaintenanceStatus.SCHEDULED)
                .scheduledDate(requestDto.getScheduledDate())
                .build();

        Maintenance savedMaintenance = maintenanceRepository.save(maintenance);
        return mapToResponseDto(savedMaintenance);
    }

    @Override
    public List<MaintenanceResponseDto> getAllMaintenance() {
        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        return maintenanceList.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public MaintenanceResponseDto getMaintenanceById(UUID id) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new MaintenanceNotFoundException("Maintenance not found"));
        return mapToResponseDto(maintenance);
    }

    @Override
    @Transactional
    public MaintenanceResponseDto updateMaintenance(UUID id, UpdateMaintenanceRequestDto requestDto) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new MaintenanceNotFoundException("Maintenance not found"));

        if (requestDto.getDescription() != null) {
            maintenance.setDescription(requestDto.getDescription());
        }

        if (requestDto.getScheduledDate() != null) {
            maintenance.setScheduledDate(requestDto.getScheduledDate());
        }

        Maintenance updatedMaintenance = maintenanceRepository.save(maintenance);
        return mapToResponseDto(updatedMaintenance);
    }

    @Override
    @Transactional
    public MaintenanceResponseDto markAsInProgress(UUID id) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new MaintenanceNotFoundException("Maintenance not found"));

        maintenance.setStatus(MaintenanceStatus.IN_PROGRESS);
        Maintenance updatedMaintenance = maintenanceRepository.save(maintenance);
        return mapToResponseDto(updatedMaintenance);
    }

    @Override
    @Transactional
    public MaintenanceResponseDto markAsCompleted(UUID id, CompleteMaintenanceRequestDto requestDto) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new MaintenanceNotFoundException("Maintenance not found"));

        maintenance.setStatus(MaintenanceStatus.COMPLETED);
        maintenance.setRemarks(requestDto.getRemarks());
        maintenance.setCompletedDate(LocalDateTime.now());
        Maintenance updatedMaintenance = maintenanceRepository.save(maintenance);
        return mapToResponseDto(updatedMaintenance);
    }

    @Override
    @Transactional
    public void deleteMaintenance(UUID id) {
        maintenanceRepository.deleteById(id);
    }

    private MaintenanceResponseDto mapToResponseDto(Maintenance maintenance) {
        MaintenanceResponseDto responseDto = new MaintenanceResponseDto();
        responseDto.setId(maintenance.getId());
        responseDto.setVehicleId(maintenance.getVehicle().getId());
        responseDto.setReportedById(maintenance.getReportedBy().getId());
        responseDto.setDescription(maintenance.getDescription());
        responseDto.setRemarks(maintenance.getRemarks());
        responseDto.setStatus(maintenance.getStatus());
        responseDto.setScheduledDate(maintenance.getScheduledDate());
        responseDto.setCompletedDate(maintenance.getCompletedDate());
        responseDto.setCreatedAt(maintenance.getCreatedAt());
        return responseDto;
    }
}