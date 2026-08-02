package com.mrunali.fleet_mgmt_platform.service.impl;

import com.mrunali.fleet_mgmt_platform.dto.request.VehicleRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.VehicleResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.Vehicle;
import com.mrunali.fleet_mgmt_platform.entity.enums.VehicleStatus;
import com.mrunali.fleet_mgmt_platform.exception.VehicleAlreadyExistsException;
import com.mrunali.fleet_mgmt_platform.exception.VehicleNotFoundException;
import com.mrunali.fleet_mgmt_platform.repository.VehicleRepository;
import com.mrunali.fleet_mgmt_platform.service.VehicleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    @Transactional
    public VehicleResponseDto createVehicle(VehicleRequestDto requestDto) {
        // Check if vehicle with the same number already exists
        if (vehicleRepository.existsByVehicleNumber(requestDto.getVehicleNumber())) {
            throw new VehicleAlreadyExistsException("Vehicle with this number already exists");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNumber(requestDto.getVehicleNumber());
        vehicle.setType(requestDto.getType());
        vehicle.setManufacturer(requestDto.getManufacturer());
        vehicle.setModel(requestDto.getModel());
        vehicle.setManufactureYear(requestDto.getManufactureYear());
        vehicle.setBatteryCapacity(requestDto.getBatteryCapacity());
        vehicle.setStatus(requestDto.getStatus());

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return mapToVehicleResponseDto(savedVehicle);
    }

    @Override
    public List<VehicleResponseDto> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        return vehicles.stream()
                .map(this::mapToVehicleResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleResponseDto getVehicleById(UUID id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found"));
        return mapToVehicleResponseDto(vehicle);
    }

    @Override
    public VehicleResponseDto getVehicleByVehicleNumber(String vehicleNumber) {
        Vehicle vehicle = vehicleRepository.findByVehicleNumber(vehicleNumber)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found"));
        return mapToVehicleResponseDto(vehicle);
    }

    @Override
    @Transactional
    public VehicleResponseDto updateVehicleByVehicleNumber(String vehicleNumber, VehicleRequestDto requestDto) {
        Vehicle vehicle = vehicleRepository.findByVehicleNumber(vehicleNumber)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found"));

        // Update vehicle details
        vehicle.setType(requestDto.getType());
        vehicle.setManufacturer(requestDto.getManufacturer());
        vehicle.setModel(requestDto.getModel());
        vehicle.setManufactureYear(requestDto.getManufactureYear());
        vehicle.setBatteryCapacity(requestDto.getBatteryCapacity());
        vehicle.setStatus(requestDto.getStatus());

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);

        return mapToVehicleResponseDto(updatedVehicle);
    }

    @Override
    @Transactional
    public void deleteVehicleByVehicleNumber(String vehicleNumber) {
        Vehicle vehicle = vehicleRepository.findByVehicleNumber(vehicleNumber)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found"));
        vehicle.setStatus(VehicleStatus.INACTIVE);
        vehicleRepository.save(vehicle);
    }

    private VehicleResponseDto mapToVehicleResponseDto(Vehicle vehicle) {
        return VehicleResponseDto.builder()
                .id(vehicle.getId())
                .vehicleNumber(vehicle.getVehicleNumber())
                .type(vehicle.getType())
                .manufacturer(vehicle.getManufacturer())
                .model(vehicle.getModel())
                .manufactureYear(vehicle.getManufactureYear())
                .batteryCapacity(vehicle.getBatteryCapacity())
                .status(vehicle.getStatus())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}