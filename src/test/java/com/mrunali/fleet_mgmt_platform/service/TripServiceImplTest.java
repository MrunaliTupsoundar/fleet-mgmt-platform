package com.mrunali.fleet_mgmt_platform.service;

import com.mrunali.fleet_mgmt_platform.dto.request.EndTripRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.StartTripRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.TripResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.Trip;
import com.mrunali.fleet_mgmt_platform.entity.User;
import com.mrunali.fleet_mgmt_platform.entity.Vehicle;
import com.mrunali.fleet_mgmt_platform.entity.VehicleAssignment;
import com.mrunali.fleet_mgmt_platform.entity.enums.AssignmentStatus;
import com.mrunali.fleet_mgmt_platform.entity.enums.Role;
import com.mrunali.fleet_mgmt_platform.entity.enums.TripStatus;
import com.mrunali.fleet_mgmt_platform.entity.enums.VehicleStatus;
import com.mrunali.fleet_mgmt_platform.exception.*;
import com.mrunali.fleet_mgmt_platform.repository.TripRepository;
import com.mrunali.fleet_mgmt_platform.repository.UserRepository;
import com.mrunali.fleet_mgmt_platform.repository.VehicleAssignmentRepository;
import com.mrunali.fleet_mgmt_platform.repository.VehicleRepository;
import com.mrunali.fleet_mgmt_platform.service.impl.TripServiceImpl;
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

public class TripServiceImplTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleAssignmentRepository vehicleAssignmentRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TripServiceImpl tripService;

    private User driver;
    private Vehicle vehicle;
    private VehicleAssignment assignment;
    private Trip trip;
    private StartTripRequestDto startTripRequestDto;
    private EndTripRequestDto endTripRequestDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock SecurityContextHolder
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("driver@example.com");

        driver = new User();
        driver.setId(UUID.randomUUID());
        driver.setEmail("driver@example.com");
        driver.setRole(Role.DRIVER);

        vehicle = new Vehicle();
        vehicle.setId(UUID.randomUUID());
        vehicle.setStatus(VehicleStatus.ASSIGNED);

        assignment = new VehicleAssignment();
        assignment.setId(UUID.randomUUID());
        assignment.setDriver(driver);
        assignment.setVehicle(vehicle);
        assignment.setStatus(AssignmentStatus.ACTIVE);

        trip = new Trip();
        trip.setId(UUID.randomUUID());
        trip.setAssignment(assignment);
        trip.setDriver(driver);
        trip.setVehicle(vehicle);
        trip.setStartTime(LocalDateTime.now().minusHours(1));
        trip.setStartOdometer(100.0);
        trip.setStartBatteryPercentage(80.0);
        trip.setStatus(TripStatus.ACTIVE);

        startTripRequestDto = new StartTripRequestDto();
        startTripRequestDto.setVehicleId(vehicle.getId());
        startTripRequestDto.setStartTime(LocalDateTime.now());
        startTripRequestDto.setStartOdometer(100.0);
        startTripRequestDto.setStartBatteryPercentage(80.0);

        endTripRequestDto = new EndTripRequestDto();
        endTripRequestDto.setTripId(trip.getId());
        endTripRequestDto.setEndTime(LocalDateTime.now());
        endTripRequestDto.setEndOdometer(200.0);
        endTripRequestDto.setEndBatteryPercentage(60.0);
    }

    @Test
    public void testStartTrip_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));
        when(vehicleAssignmentRepository.findByDriverIdAndVehicleId(driver.getId(), vehicle.getId())).thenReturn(Optional.of(assignment));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        TripResponseDto responseDto = tripService.startTrip(startTripRequestDto);

        assertNotNull(responseDto);
        assertEquals(trip.getId(), responseDto.getId());
        assertEquals(TripStatus.ACTIVE, responseDto.getStatus());
        verify(vehicleRepository, times(1)).save(argThat(v -> v.getStatus().equals(VehicleStatus.ON_TRIP)));
        verify(tripRepository, times(1)).save(any(Trip.class));
    }

    @Test
    public void testStartTrip_DriverNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class, () -> tripService.startTrip(startTripRequestDto));
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    public void testStartTrip_VehicleNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.empty());

        assertThrows(VehicleNotFoundException.class, () -> tripService.startTrip(startTripRequestDto));
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    public void testStartTrip_VehicleNotAssigned() {
        vehicle.setStatus(VehicleStatus.AVAILABLE); // Not assigned
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));

        assertThrows(VehicleNotAssignedException.class, () -> tripService.startTrip(startTripRequestDto));
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    public void testStartTrip_AssignmentNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));
        when(vehicleAssignmentRepository.findByDriverIdAndVehicleId(driver.getId(), vehicle.getId())).thenReturn(Optional.empty());

        assertThrows(AssignmentNotFoundException.class, () -> tripService.startTrip(startTripRequestDto));
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    public void testStartTrip_AssignmentNotActive() {
        assignment.setStatus(AssignmentStatus.COMPLETED); // Not active
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));
        when(vehicleAssignmentRepository.findByDriverIdAndVehicleId(driver.getId(), vehicle.getId())).thenReturn(Optional.of(assignment));

        assertThrows(AssignmentNotActiveException.class, () -> tripService.startTrip(startTripRequestDto));
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    public void testEndTrip_Success() {
        when(tripRepository.findById(trip.getId())).thenReturn(Optional.of(trip));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        TripResponseDto responseDto = tripService.endTrip(endTripRequestDto);

        assertNotNull(responseDto);
        assertEquals(trip.getId(), responseDto.getId());
        assertEquals(TripStatus.COMPLETED, responseDto.getStatus());
        assertEquals(100.0, responseDto.getDistance()); // 200 - 100
        assertEquals(20.0, responseDto.getBatteryUsed()); // 80 - 60
        assertNotNull(responseDto.getAverageSpeed());
        verify(vehicleRepository, times(1)).save(argThat(v -> v.getStatus().equals(VehicleStatus.ASSIGNED)));
        verify(tripRepository, times(1)).save(any(Trip.class));
    }

    @Test
    public void testEndTrip_TripNotFound() {
        when(tripRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(TripNotFoundException.class, () -> tripService.endTrip(endTripRequestDto));
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    public void testEndTrip_TripNotActive() {
        trip.setStatus(TripStatus.COMPLETED); // Not active
        when(tripRepository.findById(trip.getId())).thenReturn(Optional.of(trip));

        assertThrows(TripNotActiveException.class, () -> tripService.endTrip(endTripRequestDto));
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    public void testGetAllTrips() {
        when(tripRepository.findAll()).thenReturn(Collections.singletonList(trip));

        List<TripResponseDto> responseDtos = tripService.getAllTrips();

        assertNotNull(responseDtos);
        assertEquals(1, responseDtos.size());
        assertEquals(trip.getId(), responseDtos.get(0).getId());
        verify(tripRepository, times(1)).findAll();
    }

    @Test
    public void testGetTripById_Success() {
        when(tripRepository.findById(trip.getId())).thenReturn(Optional.of(trip));

        TripResponseDto responseDto = tripService.getTripById(trip.getId());

        assertNotNull(responseDto);
        assertEquals(trip.getId(), responseDto.getId());
        verify(tripRepository, times(1)).findById(trip.getId());
    }

    @Test
    public void testGetTripById_NotFound() {
        when(tripRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(TripNotFoundException.class, () -> tripService.getTripById(UUID.randomUUID()));
        verify(tripRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void testGetTripsByDriverId() {
        when(tripRepository.findByDriverId(driver.getId())).thenReturn(Collections.singletonList(trip));

        List<TripResponseDto> responseDtos = tripService.getTripsByDriverId(driver.getId());

        assertNotNull(responseDtos);
        assertEquals(1, responseDtos.size());
        assertEquals(trip.getId(), responseDtos.get(0).getId());
        verify(tripRepository, times(1)).findByDriverId(driver.getId());
    }

    @Test
    public void testGetTripsByVehicleId() {
        when(tripRepository.findByVehicleId(vehicle.getId())).thenReturn(Collections.singletonList(trip));

        List<TripResponseDto> responseDtos = tripService.getTripsByVehicleId(vehicle.getId());

        assertNotNull(responseDtos);
        assertEquals(1, responseDtos.size());
        assertEquals(trip.getId(), responseDtos.get(0).getId());
        verify(tripRepository, times(1)).findByVehicleId(vehicle.getId());
    }

    @Test
    public void testGetTripsByStatus() {
        when(tripRepository.findByStatus(TripStatus.ACTIVE)).thenReturn(Collections.singletonList(trip));

        List<TripResponseDto> responseDtos = tripService.getTripsByStatus(TripStatus.ACTIVE);

        assertNotNull(responseDtos);
        assertEquals(1, responseDtos.size());
        assertEquals(trip.getId(), responseDtos.get(0).getId());
        verify(tripRepository, times(1)).findByStatus(TripStatus.ACTIVE);
    }

    @Test
    public void testGetTripsByDriverIdAndStatus() {
        when(tripRepository.findByDriverIdAndStatus(driver.getId(), TripStatus.ACTIVE)).thenReturn(Collections.singletonList(trip));

        List<TripResponseDto> responseDtos = tripService.getTripsByDriverIdAndStatus(driver.getId(), TripStatus.ACTIVE);

        assertNotNull(responseDtos);
        assertEquals(1, responseDtos.size());
        assertEquals(trip.getId(), responseDtos.get(0).getId());
        verify(tripRepository, times(1)).findByDriverIdAndStatus(driver.getId(), TripStatus.ACTIVE);
    }

    @Test
    public void testGetTripsByVehicleIdAndStatus() {
        when(tripRepository.findByVehicleIdAndStatus(vehicle.getId(), TripStatus.ACTIVE)).thenReturn(Collections.singletonList(trip));

        List<TripResponseDto> responseDtos = tripService.getTripsByVehicleIdAndStatus(vehicle.getId(), TripStatus.ACTIVE);

        assertNotNull(responseDtos);
        assertEquals(1, responseDtos.size());
        assertEquals(trip.getId(), responseDtos.get(0).getId());
        verify(tripRepository, times(1)).findByVehicleIdAndStatus(vehicle.getId(), TripStatus.ACTIVE);
    }

    @Test
    public void testUpdateMaxSpeed_NewMaxSpeed() {
        trip.setMaxSpeed(50.0);
        when(tripRepository.findById(trip.getId())).thenReturn(Optional.of(trip));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        tripService.updateMaxSpeed(trip.getId(), 70.0);

        verify(tripRepository, times(1)).save(argThat(t -> t.getMaxSpeed() == 70.0));
    }

    @Test
    public void testUpdateMaxSpeed_NoChange() {
        trip.setMaxSpeed(50.0);
        when(tripRepository.findById(trip.getId())).thenReturn(Optional.of(trip));

        tripService.updateMaxSpeed(trip.getId(), 40.0);

        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    public void testUpdateMaxSpeed_TripNotFound() {
        when(tripRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(TripNotFoundException.class, () -> tripService.updateMaxSpeed(UUID.randomUUID(), 60.0));
        verify(tripRepository, never()).save(any(Trip.class));
    }
}