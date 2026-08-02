package com.mrunali.fleet_mgmt_platform.service;

import com.mrunali.fleet_mgmt_platform.dto.request.VehicleAssignmentRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.VehicleAssignmentResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.User;
import com.mrunali.fleet_mgmt_platform.entity.Vehicle;
import com.mrunali.fleet_mgmt_platform.entity.VehicleAssignment;
import com.mrunali.fleet_mgmt_platform.entity.enums.AssignmentStatus;
import com.mrunali.fleet_mgmt_platform.entity.enums.Role;
import com.mrunali.fleet_mgmt_platform.entity.enums.UserStatus;
import com.mrunali.fleet_mgmt_platform.entity.enums.VehicleStatus;
import com.mrunali.fleet_mgmt_platform.exception.*;
import com.mrunali.fleet_mgmt_platform.repository.UserRepository;
import com.mrunali.fleet_mgmt_platform.repository.VehicleAssignmentRepository;
import com.mrunali.fleet_mgmt_platform.repository.VehicleRepository;
import com.mrunali.fleet_mgmt_platform.service.impl.VehicleAssignmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VehicleAssignmentServiceImplTest{

    @Mock
    private VehicleAssignmentRepository vehicleAssignmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private VehicleAssignmentServiceImpl vehicleAssignmentService;

    private User assignedBy;
    private User driver;
    private Vehicle vehicle;
    private VehicleAssignment assignment;
    private VehicleAssignmentRequestDto requestDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("manager@example.com");

        assignedBy = new User();
        assignedBy.setId(UUID.randomUUID());
        assignedBy.setEmail("manager@example.com");
        assignedBy.setRole(Role.MANAGER);

        driver = new User();
        driver.setId(UUID.randomUUID());
        driver.setRole(Role.DRIVER);
        driver.setStatus(UserStatus.ACTIVE);

        vehicle = new Vehicle();
        vehicle.setId(UUID.randomUUID());
        vehicle.setStatus(VehicleStatus.AVAILABLE);

        assignment = new VehicleAssignment();
        assignment.setId(UUID.randomUUID());
        assignment.setDriver(driver);
        assignment.setVehicle(vehicle);
        assignment.setAssignedBy(assignedBy);
        assignment.setStatus(AssignmentStatus.ACTIVE);

        requestDto = new VehicleAssignmentRequestDto();
        requestDto.setDriverId(driver.getId());
        requestDto.setVehicleId(vehicle.getId());
        requestDto.setAssignedFrom(LocalDateTime.now());
        requestDto.setAssignedTo(LocalDateTime.now().plusDays(7));
    }

    @Test
    public void testCreateAssignment_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(assignedBy));
        when(userRepository.findById(driver.getId())).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));
        when(vehicleAssignmentRepository.save(any(VehicleAssignment.class))).thenReturn(assignment);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleAssignmentResponseDto responseDto = vehicleAssignmentService.createAssignment(requestDto);

        assertNotNull(responseDto);
        assertEquals(assignment.getId(), responseDto.getId());
        verify(vehicleRepository, times(1)).save(argThat(v -> v.getStatus() == VehicleStatus.ASSIGNED));
        verify(vehicleAssignmentRepository, times(1)).save(any(VehicleAssignment.class));
    }

    @Test
    public void testCreateAssignment_DriverNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(assignedBy));
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class, () -> vehicleAssignmentService.createAssignment(requestDto));
    }

    @Test
    public void testCreateAssignment_InvalidDriver() {
        driver.setRole(Role.MANAGER); // Not a driver
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(assignedBy));
        when(userRepository.findById(driver.getId())).thenReturn(Optional.of(driver));

        assertThrows(InvalidDriverException.class, () -> vehicleAssignmentService.createAssignment(requestDto));
    }

    @Test
    public void testCreateAssignment_VehicleNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(assignedBy));
        when(userRepository.findById(driver.getId())).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(VehicleNotFoundException.class, () -> vehicleAssignmentService.createAssignment(requestDto));
    }

    @Test
    public void testCreateAssignment_VehicleNotAvailable() {
        vehicle.setStatus(VehicleStatus.ON_TRIP); // Not available
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(assignedBy));
        when(userRepository.findById(driver.getId())).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));

        assertThrows(VehicleNotAvailableException.class, () -> vehicleAssignmentService.createAssignment(requestDto));
    }

    @Test
    public void testGetAllAssignments() {
        when(vehicleAssignmentRepository.findAll()).thenReturn(Collections.singletonList(assignment));

        List<VehicleAssignmentResponseDto> responseDtos = vehicleAssignmentService.getAllAssignments();

        assertNotNull(responseDtos);
        assertEquals(1, responseDtos.size());
        verify(vehicleAssignmentRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteAssignment_Success() {
        when(vehicleAssignmentRepository.findById(assignment.getId())).thenReturn(Optional.of(assignment));
        when(vehicleAssignmentRepository.save(any(VehicleAssignment.class))).thenReturn(assignment);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        vehicleAssignmentService.deleteAssignment(assignment.getId());

        verify(vehicleAssignmentRepository, times(1)).save(argThat(a -> a.getStatus() == AssignmentStatus.COMPLETED));
        verify(vehicleRepository, times(1)).save(argThat(v -> v.getStatus() == VehicleStatus.AVAILABLE));
    }

    @Test
    public void testDeleteAssignment_NotFound() {
        when(vehicleAssignmentRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(AssignmentNotFoundException.class, () -> vehicleAssignmentService.deleteAssignment(UUID.randomUUID()));
    }

    @Test
    public void testDeleteAssignment_NotActive() {
        assignment.setStatus(AssignmentStatus.COMPLETED);
        when(vehicleAssignmentRepository.findById(assignment.getId())).thenReturn(Optional.of(assignment));

        assertThrows(AssignmentNotActiveException.class, () -> vehicleAssignmentService.deleteAssignment(assignment.getId()));
    }
}