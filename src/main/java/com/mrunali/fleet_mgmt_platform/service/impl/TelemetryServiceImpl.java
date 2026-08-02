package com.mrunali.fleet_mgmt_platform.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mrunali.fleet_mgmt_platform.dto.request.TelemetryRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.TelemetryResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.Alert;
import com.mrunali.fleet_mgmt_platform.entity.LiveVehicleStatus;
import com.mrunali.fleet_mgmt_platform.entity.Telemetry;
import com.mrunali.fleet_mgmt_platform.entity.Trip;
import com.mrunali.fleet_mgmt_platform.entity.Vehicle;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertSeverity;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertStatus;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertType;
import com.mrunali.fleet_mgmt_platform.entity.enums.ConnectionStatus;
import com.mrunali.fleet_mgmt_platform.entity.enums.TripStatus;
import com.mrunali.fleet_mgmt_platform.exception.AlertAlreadyExistsException;
import com.mrunali.fleet_mgmt_platform.exception.NoTelemetryFoundException;
import com.mrunali.fleet_mgmt_platform.exception.TripNotActiveException;
import com.mrunali.fleet_mgmt_platform.exception.TripNotFoundException;
import com.mrunali.fleet_mgmt_platform.repository.AlertRepository;
import com.mrunali.fleet_mgmt_platform.repository.LiveVehicleStatusRepository;
import com.mrunali.fleet_mgmt_platform.repository.TelemetryRepository;
import com.mrunali.fleet_mgmt_platform.repository.TripRepository;
import com.mrunali.fleet_mgmt_platform.service.TelemetryService;
import com.mrunali.fleet_mgmt_platform.service.TripService;

@Service
public class TelemetryServiceImpl implements TelemetryService {

    private final TelemetryRepository telemetryRepository;
    private final TripRepository tripRepository;
    private final LiveVehicleStatusRepository liveVehicleStatusRepository;
    private final TripService tripService;
    private final AlertRepository alertRepository;

    public TelemetryServiceImpl(TelemetryRepository telemetryRepository, TripRepository tripRepository, LiveVehicleStatusRepository liveVehicleStatusRepository, TripService tripService, AlertRepository alertRepository) {
        this.telemetryRepository = telemetryRepository;
        this.tripRepository = tripRepository;
        this.liveVehicleStatusRepository = liveVehicleStatusRepository;
        this.tripService = tripService;
        this.alertRepository = alertRepository;
    }

    @Override
    @Transactional
    public TelemetryResponseDto ingestTelemetry(TelemetryRequestDto requestDto) {
        Trip trip = tripRepository.findById(requestDto.getTripId())
                .orElseThrow(() -> new TripNotFoundException("Trip not found"));

        if (!trip.getStatus().equals(TripStatus.ACTIVE)) {
            throw new TripNotActiveException("Trip is not active");
        }

        Telemetry telemetry = new Telemetry();
        telemetry.setTrip(trip);
        telemetry.setTimestamp(requestDto.getTimestamp());
        telemetry.setLatitude(requestDto.getLatitude());
        telemetry.setLongitude(requestDto.getLongitude());
        telemetry.setSpeed(requestDto.getSpeed());
        telemetry.setBatteryPercentage(requestDto.getBatteryPercentage());
        telemetry.setVoltage(requestDto.getVoltage());
        telemetry.setCurrent(requestDto.getCurrent());
        telemetry.setMotorTemperature(requestDto.getMotorTemperature());
        telemetry.setControllerTemperature(requestDto.getControllerTemperature());
        telemetry.setStateOfHealth(requestDto.getStateOfHealth());
        telemetry.setOdometer(requestDto.getOdometer());

        Telemetry savedTelemetry = telemetryRepository.save(telemetry);

        // Update live vehicle status
        Vehicle vehicle = trip.getVehicle();
        LiveVehicleStatus liveVehicleStatus = liveVehicleStatusRepository.findByVehicleId(vehicle.getId())
                .orElseGet(() -> {
                    LiveVehicleStatus newLiveVehicleStatus = new LiveVehicleStatus();
                    newLiveVehicleStatus.setVehicle(vehicle);
                    return newLiveVehicleStatus;
                });

        liveVehicleStatus.setLatitude(requestDto.getLatitude());
        liveVehicleStatus.setLongitude(requestDto.getLongitude());
        liveVehicleStatus.setBatteryPercentage(requestDto.getBatteryPercentage());
        liveVehicleStatus.setStateOfHealth(requestDto.getStateOfHealth());
        liveVehicleStatus.setOdometer(requestDto.getOdometer());
        liveVehicleStatus.setLastSeen(requestDto.getTimestamp());
        liveVehicleStatus.setConnectionStatus(ConnectionStatus.CONNECTED);

        liveVehicleStatusRepository.save(liveVehicleStatus);

        // Update max speed
        tripService.updateMaxSpeed(trip.getId(), requestDto.getSpeed());

        // Execute alert rules
        executeAlertRules(telemetry);

        return mapToResponseDto(savedTelemetry);
    }

    @Override
    public List<TelemetryResponseDto> getTripTelemetry(UUID tripId) {
        List<Telemetry> telemetryList = telemetryRepository.findByTripId(tripId);
        return telemetryList.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public TelemetryResponseDto getLatestTelemetry(UUID tripId) {
        Telemetry telemetry = telemetryRepository.findFirstByTripIdOrderByTimestampDesc(tripId)
                .orElseThrow(() -> new NoTelemetryFoundException("No telemetry found for the trip"));
        return mapToResponseDto(telemetry);
    }

    private TelemetryResponseDto mapToResponseDto(Telemetry telemetry) {
        TelemetryResponseDto responseDto = new TelemetryResponseDto();
        responseDto.setId(telemetry.getId());
        responseDto.setTripId(telemetry.getTrip().getId());
        responseDto.setTimestamp(telemetry.getTimestamp());
        responseDto.setLatitude(telemetry.getLatitude());
        responseDto.setLongitude(telemetry.getLongitude());
        responseDto.setSpeed(telemetry.getSpeed());
        responseDto.setBatteryPercentage(telemetry.getBatteryPercentage());
        responseDto.setVoltage(telemetry.getVoltage());
        responseDto.setCurrent(telemetry.getCurrent());
        responseDto.setMotorTemperature(telemetry.getMotorTemperature());
        responseDto.setControllerTemperature(telemetry.getControllerTemperature());
        responseDto.setStateOfHealth(telemetry.getStateOfHealth());
        responseDto.setOdometer(telemetry.getOdometer());
        responseDto.setCreatedAt(telemetry.getCreatedAt());
        return responseDto;
    }

    private void executeAlertRules(Telemetry telemetry) {
        Vehicle vehicle = telemetry.getTrip().getVehicle();

        // Threshold values
        final double LOW_BATTERY_THRESHOLD = 20.0;
        final double HIGH_MOTOR_TEMPERATURE_THRESHOLD = 80.0;
        final double LOW_SOH_THRESHOLD = 20.0;

        // Low Battery Alert
        boolean lowBatteryAlertExists = alertRepository.existsByVehicleIdAndTypeAndStatus(
                vehicle.getId(), AlertType.LOW_BATTERY, AlertStatus.ACTIVE);

        if (telemetry.getBatteryPercentage() < LOW_BATTERY_THRESHOLD && !lowBatteryAlertExists) {
            createAlert(vehicle, telemetry, AlertType.LOW_BATTERY, AlertSeverity.HIGH, "Battery percentage is below " + LOW_BATTERY_THRESHOLD + "%");
        }

        // High Motor Temperature Alert
        boolean highMotorTemperatureAlertExists = alertRepository.existsByVehicleIdAndTypeAndStatus(
                vehicle.getId(), AlertType.HIGH_MOTOR_TEMPERATURE, AlertStatus.ACTIVE);

        if (telemetry.getMotorTemperature() > HIGH_MOTOR_TEMPERATURE_THRESHOLD && !highMotorTemperatureAlertExists) {
            createAlert(vehicle, telemetry, AlertType.HIGH_MOTOR_TEMPERATURE, AlertSeverity.HIGH, "Motor temperature is above " + HIGH_MOTOR_TEMPERATURE_THRESHOLD + "°C");
        }

        // Low State of Health Alert
        boolean lowSohAlertExists = alertRepository.existsByVehicleIdAndTypeAndStatus(
                vehicle.getId(), AlertType.LOW_SOH, AlertStatus.ACTIVE);

        if (telemetry.getStateOfHealth() < LOW_SOH_THRESHOLD && !lowSohAlertExists) {
            createAlert(vehicle, telemetry, AlertType.LOW_SOH, AlertSeverity.HIGH, "State of Health is below " + LOW_SOH_THRESHOLD + "%");
        }

        // Geofence Breach Alert
        boolean geofenceBreachAlertExists = alertRepository.existsByVehicleIdAndTypeAndStatus(
                vehicle.getId(), AlertType.GEOFENCE_BREACH, AlertStatus.ACTIVE);

        if (isGeofenceBreached(telemetry) && !geofenceBreachAlertExists) {
            createAlert(vehicle, telemetry, AlertType.GEOFENCE_BREACH, AlertSeverity.HIGH, "Geofence breached");
        }

        // Disconnected Alert
        boolean disconnectedAlertExists = alertRepository.existsByVehicleIdAndTypeAndStatus(
                vehicle.getId(), AlertType.DISCONNECTED, AlertStatus.ACTIVE);

        if (isDisconnected(telemetry) && !disconnectedAlertExists) {
            createAlert(vehicle, telemetry, AlertType.DISCONNECTED, AlertSeverity.HIGH, "Vehicle disconnected");
        }

    }

    private boolean isGeofenceBreached(Telemetry telemetry) {
        // Implement geofence breach logic here
        // For example, check if the telemetry coordinates are outside the allowed area
        // This is a placeholder implementation
        double latitude = telemetry.getLatitude();
        double longitude = telemetry.getLongitude();

        // Define the allowed area (e.g., a rectangle)
        double minLatitude = 12.9716; // Example value
        double maxLatitude = 12.9716; // Example value
        double minLongitude = 77.5946; // Example value
        double maxLongitude = 77.5946; // Example value

        return latitude < minLatitude || latitude > maxLatitude || longitude < minLongitude || longitude > maxLongitude;
    }

    private boolean isDisconnected(Telemetry telemetry) {
        // Implement disconnected logic here
        // For example, check if the telemetry timestamp is older than a certain threshold
        // This is a placeholder implementation
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime telemetryTimestamp = telemetry.getTimestamp();

        // Define the threshold (e.g., 5 minutes)
        long thresholdMinutes = 5;

        return telemetryTimestamp.isBefore(now.minusMinutes(thresholdMinutes));
    }

    private void createAlert(Vehicle vehicle, Telemetry telemetry, AlertType type, AlertSeverity severity, String message) {
        if (alertRepository.existsByVehicleIdAndTypeAndStatus(vehicle.getId(), type, AlertStatus.ACTIVE)) {
            throw new AlertAlreadyExistsException("Alert of type " + type + " already exists for the vehicle");
        }
    Alert alert = new Alert();
        alert.setVehicle(vehicle);
        alert.setTelemetry(telemetry);
        alert.setType(type);
        alert.setSeverity(severity);
        alert.setStatus(AlertStatus.ACTIVE);
        alert.setMessage(message);
        alertRepository.save(alert);
    }
}