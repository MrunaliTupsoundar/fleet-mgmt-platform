package com.mrunali.fleet_mgmt_platform.service.impl;

import com.mrunali.fleet_mgmt_platform.dto.request.VehicleAssignmentRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.VehicleAssignmentResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.User;
import com.mrunali.fleet_mgmt_platform.entity.Vehicle;
import com.mrunali.fleet_mgmt_platform.entity.VehicleAssignment;
import com.mrunali.fleet_mgmt_platform.entity.enums.AssignmentStatus;
import com.mrunali.fleet_mgmt_platform.entity.enums.Role;
import com.mrunali.fleet_mgmt_platform.entity.enums.UserStatus;
import com.mrunali.fleet_mgmt_platform.entity.enums.VehicleStatus;
import com.mrunali.fleet_mgmt_platform.exception.AssignmentNotActiveException;
import com.mrunali.fleet_mgmt_platform.exception.AssignmentNotFoundException;
import com.mrunali.fleet_mgmt_platform.exception.DriverNotFoundException;
import com.mrunali.fleet_mgmt_platform.exception.InvalidDriverException;
import com.mrunali.fleet_mgmt_platform.exception.UserNotFoundException;
import com.mrunali.fleet_mgmt_platform.exception.VehicleNotAvailableException;
import com.mrunali.fleet_mgmt_platform.exception.VehicleNotFoundException;
import com.mrunali.fleet_mgmt_platform.repository.UserRepository;
import com.mrunali.fleet_mgmt_platform.repository.VehicleAssignmentRepository;
import com.mrunali.fleet_mgmt_platform.repository.VehicleRepository;
import com.mrunali.fleet_mgmt_platform.service.VehicleAssignmentService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VehicleAssignmentServiceImpl implements VehicleAssignmentService {

    private final VehicleAssignmentRepository vehicleAssignmentRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public VehicleAssignmentServiceImpl(VehicleAssignmentRepository vehicleAssignmentRepository, UserRepository userRepository, VehicleRepository vehicleRepository) {
        this.vehicleAssignmentRepository = vehicleAssignmentRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    @Transactional
    public VehicleAssignmentResponseDto createAssignment(VehicleAssignmentRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User assignedBy = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        User driver = userRepository.findById(requestDto.getDriverId())
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));

        if (!driver.getRole().equals(Role.DRIVER) || !driver.getStatus().equals(UserStatus.ACTIVE)) {
            throw new InvalidDriverException("Invalid driver");
        }

        Vehicle vehicle = vehicleRepository.findById(requestDto.getVehicleId())
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found"));

        if (!vehicle.getStatus().equals(VehicleStatus.AVAILABLE)) {
            throw new VehicleNotAvailableException("Vehicle is not available");
        }

        VehicleAssignment assignment = new VehicleAssignment();
        assignment.setDriver(driver);
        assignment.setVehicle(vehicle);
        assignment.setAssignedBy(assignedBy);
        assignment.setAssignedFrom(requestDto.getAssignedFrom());
        assignment.setAssignedTo(requestDto.getAssignedTo());
        assignment.setStatus(AssignmentStatus.ACTIVE);

        vehicle.setStatus(VehicleStatus.ASSIGNED);
        vehicleRepository.save(vehicle);

        VehicleAssignment savedAssignment = vehicleAssignmentRepository.save(assignment);

        return mapToResponseDto(savedAssignment);
    }

    @Override
    public List<VehicleAssignmentResponseDto> getAllAssignments() {
        List<VehicleAssignment> assignments = vehicleAssignmentRepository.findAll();
        return assignments.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleAssignmentResponseDto getAssignmentById(UUID id) {
        VehicleAssignment assignment = vehicleAssignmentRepository.findById(id)
                .orElseThrow(() -> new AssignmentNotFoundException("Assignment not found"));
        return mapToResponseDto(assignment);
    }

    @Override
    public List<VehicleAssignmentResponseDto> getAssignmentsByDriverId(UUID driverId) {
        List<VehicleAssignment> assignments = vehicleAssignmentRepository.findByDriverId(driverId);
        return assignments.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleAssignmentResponseDto> getAssignmentsByVehicleId(UUID vehicleId) {
        List<VehicleAssignment> assignments = vehicleAssignmentRepository.findByVehicleId(vehicleId);
        return assignments.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleAssignmentResponseDto> getAssignmentsByAssignedById(UUID assignedById) {
        List<VehicleAssignment> assignments = vehicleAssignmentRepository.findByAssignedById(assignedById);
        return assignments.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VehicleAssignmentResponseDto updateAssignment(UUID id, VehicleAssignmentRequestDto requestDto) {
        VehicleAssignment assignment = vehicleAssignmentRepository.findById(id)
                .orElseThrow(() -> new AssignmentNotFoundException("Assignment not found"));

        if (!assignment.getStatus().equals(AssignmentStatus.ACTIVE)) {
            throw new AssignmentNotActiveException("Assignment is not active");
        }

        assignment.setAssignedFrom(requestDto.getAssignedFrom());
        assignment.setAssignedTo(requestDto.getAssignedTo());

        VehicleAssignment updatedAssignment = vehicleAssignmentRepository.save(assignment);

        return mapToResponseDto(updatedAssignment);
    }

    @Override
    @Transactional
    public void deleteAssignment(UUID id) {
        VehicleAssignment assignment = vehicleAssignmentRepository.findById(id)
                .orElseThrow(() -> new AssignmentNotFoundException("Assignment not found"));

        if (!assignment.getStatus().equals(AssignmentStatus.ACTIVE)) {
            throw new AssignmentNotActiveException("Assignment is not active");
        }

        assignment.setStatus(AssignmentStatus.COMPLETED);
        vehicleAssignmentRepository.save(assignment);

        Vehicle vehicle = assignment.getVehicle();
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicleRepository.save(vehicle);
    }

    private VehicleAssignmentResponseDto mapToResponseDto(VehicleAssignment assignment) {
        VehicleAssignmentResponseDto responseDto = new VehicleAssignmentResponseDto();
        responseDto.setId(assignment.getId());
        responseDto.setDriverId(assignment.getDriver().getId());
        responseDto.setVehicleId(assignment.getVehicle().getId());
        responseDto.setAssignedById(assignment.getAssignedBy().getId());
        responseDto.setAssignedFrom(assignment.getAssignedFrom());
        responseDto.setAssignedTo(assignment.getAssignedTo());
        responseDto.setStatus(assignment.getStatus());
        responseDto.setCreatedAt(assignment.getCreatedAt());
        responseDto.setUpdatedAt(assignment.getUpdatedAt());
        return responseDto;
    }
}