package com.mrunali.fleet_mgmt_platform.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mrunali.fleet_mgmt_platform.dto.response.LiveVehicleStatusResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.LiveVehicleStatus;
import com.mrunali.fleet_mgmt_platform.entity.enums.ConnectionStatus;
import com.mrunali.fleet_mgmt_platform.exception.LiveVehicleStatusNotFoundException;
import com.mrunali.fleet_mgmt_platform.repository.LiveVehicleStatusRepository;
import com.mrunali.fleet_mgmt_platform.service.LiveVehicleStatusService;

@Service
public class LiveVehicleStatusServiceImpl implements LiveVehicleStatusService {

    private final LiveVehicleStatusRepository liveVehicleStatusRepository;

    public LiveVehicleStatusServiceImpl(LiveVehicleStatusRepository liveVehicleStatusRepository) {
        this.liveVehicleStatusRepository = liveVehicleStatusRepository;
    }

    @Override
    public LiveVehicleStatusResponseDto getLiveVehicleStatus(UUID vehicleId) {
        LiveVehicleStatus liveVehicleStatus = liveVehicleStatusRepository.findByVehicleId(vehicleId)
                .orElseThrow(() -> new LiveVehicleStatusNotFoundException("Live vehicle status not found"));
        return mapToResponseDto(liveVehicleStatus);
    }

    @Override
    public List<LiveVehicleStatusResponseDto> getAllLiveVehicleStatuses() {
        List<LiveVehicleStatus> liveVehicleStatuses = liveVehicleStatusRepository.findAll();
        return liveVehicleStatuses.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LiveVehicleStatusResponseDto> getConnectedVehicles() {
        List<LiveVehicleStatus> liveVehicleStatuses = liveVehicleStatusRepository.findByConnectionStatus(ConnectionStatus.CONNECTED);
        return liveVehicleStatuses.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LiveVehicleStatusResponseDto> getDisconnectedVehicles() {
        List<LiveVehicleStatus> liveVehicleStatuses = liveVehicleStatusRepository.findByConnectionStatus(ConnectionStatus.DISCONNECTED);
        return liveVehicleStatuses.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private LiveVehicleStatusResponseDto mapToResponseDto(LiveVehicleStatus liveVehicleStatus) {
        LiveVehicleStatusResponseDto responseDto = new LiveVehicleStatusResponseDto();
        responseDto.setId(liveVehicleStatus.getId());
        responseDto.setVehicleId(liveVehicleStatus.getVehicle().getId());
        responseDto.setLatitude(liveVehicleStatus.getLatitude());
        responseDto.setLongitude(liveVehicleStatus.getLongitude());
        responseDto.setBatteryPercentage(liveVehicleStatus.getBatteryPercentage());
        responseDto.setStateOfHealth(liveVehicleStatus.getStateOfHealth());
        responseDto.setOdometer(liveVehicleStatus.getOdometer());
        responseDto.setLastSeen(liveVehicleStatus.getLastSeen());
        responseDto.setConnectionStatus(liveVehicleStatus.getConnectionStatus());
        return responseDto;
    }
}