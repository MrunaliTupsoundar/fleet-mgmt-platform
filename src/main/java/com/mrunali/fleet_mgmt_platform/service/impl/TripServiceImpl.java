package com.mrunali.fleet_mgmt_platform.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mrunali.fleet_mgmt_platform.dto.request.StartTripRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.EndTripRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.TripResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.Trip;
import com.mrunali.fleet_mgmt_platform.entity.User;
import com.mrunali.fleet_mgmt_platform.entity.Vehicle;
import com.mrunali.fleet_mgmt_platform.entity.VehicleAssignment;
import com.mrunali.fleet_mgmt_platform.entity.enums.AssignmentStatus;
import com.mrunali.fleet_mgmt_platform.entity.enums.TripStatus;
import com.mrunali.fleet_mgmt_platform.entity.enums.VehicleStatus;
import com.mrunali.fleet_mgmt_platform.exception.AssignmentNotActiveException;
import com.mrunali.fleet_mgmt_platform.exception.AssignmentNotFoundException;
import com.mrunali.fleet_mgmt_platform.exception.DriverNotFoundException;
import com.mrunali.fleet_mgmt_platform.exception.TripNotActiveException;
import com.mrunali.fleet_mgmt_platform.exception.TripNotFoundException;
import com.mrunali.fleet_mgmt_platform.exception.VehicleNotAssignedException;
import com.mrunali.fleet_mgmt_platform.exception.VehicleNotFoundException;
import com.mrunali.fleet_mgmt_platform.repository.TripRepository;
import com.mrunali.fleet_mgmt_platform.repository.UserRepository;
import com.mrunali.fleet_mgmt_platform.repository.VehicleAssignmentRepository;
import com.mrunali.fleet_mgmt_platform.repository.VehicleRepository;
import com.mrunali.fleet_mgmt_platform.service.TripService;

@Service
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleAssignmentRepository vehicleAssignmentRepository;

    public TripServiceImpl(TripRepository tripRepository, UserRepository userRepository, VehicleRepository vehicleRepository, VehicleAssignmentRepository vehicleAssignmentRepository) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleAssignmentRepository = vehicleAssignmentRepository;
    }

    @Override
    @Transactional
    public TripResponseDto startTrip(StartTripRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User driver = userRepository.findByEmail(email)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));

        Vehicle vehicle = vehicleRepository.findById(requestDto.getVehicleId())
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found"));

        if (!vehicle.getStatus().equals(VehicleStatus.ASSIGNED)) {
            throw new VehicleNotAssignedException("Vehicle is not assigned");
        }

        VehicleAssignment assignment = vehicleAssignmentRepository.findByDriverIdAndVehicleId(driver.getId(), vehicle.getId())
                .orElseThrow(() -> new AssignmentNotFoundException("Assignment not found"));

        if (!assignment.getStatus().equals(AssignmentStatus.ACTIVE)) {
            throw new AssignmentNotActiveException("Assignment is not active");
        }

        Trip trip = new Trip();
        trip.setAssignment(assignment);
        trip.setDriver(driver);
        trip.setVehicle(vehicle);
        trip.setStartTime(requestDto.getStartTime());
        trip.setStartOdometer(requestDto.getStartOdometer());
        trip.setStartBatteryPercentage(requestDto.getStartBatteryPercentage());
        trip.setStatus(TripStatus.ACTIVE);

        vehicle.setStatus(VehicleStatus.ON_TRIP);
        vehicleRepository.save(vehicle);

        Trip savedTrip = tripRepository.save(trip);

        return mapToResponseDto(savedTrip);
    }

    @Override
    @Transactional
    public TripResponseDto endTrip(EndTripRequestDto requestDto) {
        Trip trip = tripRepository.findById(requestDto.getTripId())
                .orElseThrow(() -> new TripNotFoundException("Trip not found"));

        if (!trip.getStatus().equals(TripStatus.ACTIVE)) {
            throw new TripNotActiveException("Trip is not active");
        }

        trip.setEndTime(requestDto.getEndTime());
        trip.setEndOdometer(requestDto.getEndOdometer());
        trip.setEndBatteryPercentage(requestDto.getEndBatteryPercentage());

        // Calculate trip analytics
        double distance = requestDto.getEndOdometer() - trip.getStartOdometer();
        double batteryUsed = trip.getStartBatteryPercentage() - requestDto.getEndBatteryPercentage();
        double tripDuration = java.time.Duration.between(trip.getStartTime(), requestDto.getEndTime()).toMinutes();
        double averageSpeed = distance / tripDuration;

        trip.setDistance(distance);
        trip.setBatteryUsed(batteryUsed);
        trip.setAverageSpeed(averageSpeed);
        trip.setStatus(TripStatus.COMPLETED);

        Vehicle vehicle = trip.getVehicle();
        vehicle.setStatus(VehicleStatus.ASSIGNED);
        vehicleRepository.save(vehicle);

        Trip updatedTrip = tripRepository.save(trip);

        return mapToResponseDto(updatedTrip);
    }

    @Override
    public List<TripResponseDto> getAllTrips() {
        List<Trip> trips = tripRepository.findAll();
        return trips.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public TripResponseDto getTripById(UUID id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException("Trip not found"));
        return mapToResponseDto(trip);
    }

    @Override
    public List<TripResponseDto> getTripsByDriverId(UUID driverId) {
        List<Trip> trips = tripRepository.findByDriverId(driverId);
        return trips.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TripResponseDto> getTripsByVehicleId(UUID vehicleId) {
        List<Trip> trips = tripRepository.findByVehicleId(vehicleId);
        return trips.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TripResponseDto> getTripsByStatus(TripStatus status) {
        List<Trip> trips = tripRepository.findByStatus(status);
        return trips.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TripResponseDto> getTripsByDriverIdAndStatus(UUID driverId, TripStatus status) {
        List<Trip> trips = tripRepository.findByDriverIdAndStatus(driverId, status);
        return trips.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TripResponseDto> getTripsByVehicleIdAndStatus(UUID vehicleId, TripStatus status) {
        List<Trip> trips = tripRepository.findByVehicleIdAndStatus(vehicleId, status);
        return trips.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateMaxSpeed(UUID tripId, Double speed) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found"));

        if (trip.getMaxSpeed() == null || speed > trip.getMaxSpeed()) {
            trip.setMaxSpeed(speed);
            tripRepository.save(trip);
        }
    }

    private TripResponseDto mapToResponseDto(Trip trip) {
        TripResponseDto responseDto = new TripResponseDto();
        responseDto.setId(trip.getId());
        responseDto.setAssignmentId(trip.getAssignment().getId());
        responseDto.setDriverId(trip.getDriver().getId());
        responseDto.setVehicleId(trip.getVehicle().getId());
        responseDto.setStartTime(trip.getStartTime());
        responseDto.setEndTime(trip.getEndTime());
        responseDto.setStartOdometer(trip.getStartOdometer());
        responseDto.setEndOdometer(trip.getEndOdometer());
        responseDto.setStartBatteryPercentage(trip.getStartBatteryPercentage());
        responseDto.setEndBatteryPercentage(trip.getEndBatteryPercentage());
        responseDto.setDistance(trip.getDistance());
        responseDto.setBatteryUsed(trip.getBatteryUsed());
        responseDto.setAverageSpeed(trip.getAverageSpeed());
        responseDto.setMaxSpeed(trip.getMaxSpeed());
        responseDto.setStatus(trip.getStatus());
        responseDto.setCreatedAt(trip.getCreatedAt());
        responseDto.setUpdatedAt(trip.getUpdatedAt());
        return responseDto;
    }
}