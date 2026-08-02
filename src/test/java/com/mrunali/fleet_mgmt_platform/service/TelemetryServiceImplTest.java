package com.mrunali.fleet_mgmt_platform.service;

import com.mrunali.fleet_mgmt_platform.dto.request.TelemetryRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.TelemetryResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.*;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertStatus;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertType;
import com.mrunali.fleet_mgmt_platform.entity.enums.TripStatus;
import com.mrunali.fleet_mgmt_platform.exception.NoTelemetryFoundException;
import com.mrunali.fleet_mgmt_platform.exception.TripNotActiveException;
import com.mrunali.fleet_mgmt_platform.exception.TripNotFoundException;
import com.mrunali.fleet_mgmt_platform.repository.AlertRepository;
import com.mrunali.fleet_mgmt_platform.repository.LiveVehicleStatusRepository;
import com.mrunali.fleet_mgmt_platform.repository.TelemetryRepository;
import com.mrunali.fleet_mgmt_platform.repository.TripRepository;
import com.mrunali.fleet_mgmt_platform.service.impl.TelemetryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TelemetryServiceImplTest {

    @Mock
    private TelemetryRepository telemetryRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private LiveVehicleStatusRepository liveVehicleStatusRepository;

    @Mock
    private TripService tripService;

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private TelemetryServiceImpl telemetryService;

    private Trip trip;
    private Vehicle vehicle;
    private TelemetryRequestDto telemetryRequestDto;
    private Telemetry telemetry;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        vehicle = new Vehicle();
        vehicle.setId(UUID.randomUUID());

        trip = new Trip();
        trip.setId(UUID.randomUUID());
        trip.setVehicle(vehicle);
        trip.setStatus(TripStatus.ACTIVE);

        telemetryRequestDto = new TelemetryRequestDto();
        telemetryRequestDto.setTripId(trip.getId());
        telemetryRequestDto.setTimestamp(LocalDateTime.now());
        telemetryRequestDto.setLatitude(12.9716);
        telemetryRequestDto.setLongitude(77.5946);
        telemetryRequestDto.setSpeed(60.0);
        telemetryRequestDto.setBatteryPercentage(80.0);
        telemetryRequestDto.setMotorTemperature(65.0);
        telemetryRequestDto.setStateOfHealth(95.0);
        telemetryRequestDto.setOdometer(15000.0);

        telemetry = new Telemetry();
        telemetry.setId(UUID.randomUUID());
        telemetry.setTrip(trip);
        telemetry.setTimestamp(telemetryRequestDto.getTimestamp());
    }

    @Test
    public void testIngestTelemetry_Success() {
        when(tripRepository.findById(trip.getId())).thenReturn(Optional.of(trip));
        when(telemetryRepository.save(any(Telemetry.class))).thenReturn(telemetry);
        when(liveVehicleStatusRepository.findByVehicleId(vehicle.getId())).thenReturn(Optional.of(new LiveVehicleStatus()));
        when(liveVehicleStatusRepository.save(any(LiveVehicleStatus.class))).thenReturn(new LiveVehicleStatus());
        doNothing().when(tripService).updateMaxSpeed(any(UUID.class), any(Double.class));

        TelemetryResponseDto responseDto = telemetryService.ingestTelemetry(telemetryRequestDto);

        assertNotNull(responseDto);
        assertEquals(telemetry.getId(), responseDto.getId());
        verify(tripRepository, times(1)).findById(trip.getId());
        verify(telemetryRepository, times(1)).save(any(Telemetry.class));
        verify(liveVehicleStatusRepository, times(1)).save(any(LiveVehicleStatus.class));
        verify(tripService, times(1)).updateMaxSpeed(trip.getId(), telemetryRequestDto.getSpeed());
    }

    @Test
    public void testIngestTelemetry_TripNotFound() {
        when(tripRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(TripNotFoundException.class, () -> telemetryService.ingestTelemetry(telemetryRequestDto));
    }

    @Test
    public void testIngestTelemetry_TripNotActive() {
        trip.setStatus(TripStatus.COMPLETED);
        when(tripRepository.findById(trip.getId())).thenReturn(Optional.of(trip));

        assertThrows(TripNotActiveException.class, () -> telemetryService.ingestTelemetry(telemetryRequestDto));
    }

    @Test
    public void testIngestTelemetry_CreatesLowBatteryAlert() {
        telemetryRequestDto.setBatteryPercentage(19.0);
        when(tripRepository.findById(trip.getId())).thenReturn(Optional.of(trip));
        when(telemetryRepository.save(any(Telemetry.class))).thenReturn(telemetry);
        when(liveVehicleStatusRepository.findByVehicleId(vehicle.getId())).thenReturn(Optional.of(new LiveVehicleStatus()));
        when(alertRepository.existsByVehicleIdAndTypeAndStatus(vehicle.getId(), AlertType.LOW_BATTERY, AlertStatus.ACTIVE)).thenReturn(false);

        telemetryService.ingestTelemetry(telemetryRequestDto);

        verify(alertRepository, times(1)).save(argThat(alert ->
                alert.getType() == AlertType.LOW_BATTERY && alert.getVehicle().getId().equals(vehicle.getId())
        ));
    }

    @Test
    public void testIngestTelemetry_DoesNotCreateDuplicateAlert() {
        telemetryRequestDto.setBatteryPercentage(19.0);
        when(tripRepository.findById(trip.getId())).thenReturn(Optional.of(trip));
        when(telemetryRepository.save(any(Telemetry.class))).thenReturn(telemetry);
        when(liveVehicleStatusRepository.findByVehicleId(vehicle.getId())).thenReturn(Optional.of(new LiveVehicleStatus()));
        // Pretend an active alert already exists
        when(alertRepository.existsByVehicleIdAndTypeAndStatus(vehicle.getId(), AlertType.LOW_BATTERY, AlertStatus.ACTIVE)).thenReturn(true);

        telemetryService.ingestTelemetry(telemetryRequestDto);

        // Verify save is NEVER called for a new alert
        verify(alertRepository, never()).save(any(Alert.class));
    }

    @Test
    public void testIngestTelemetry_CreatesHighTempAlert() {
        telemetryRequestDto.setMotorTemperature(81.0);
        when(tripRepository.findById(trip.getId())).thenReturn(Optional.of(trip));
        when(telemetryRepository.save(any(Telemetry.class))).thenReturn(telemetry);
        when(liveVehicleStatusRepository.findByVehicleId(vehicle.getId())).thenReturn(Optional.of(new LiveVehicleStatus()));
        when(alertRepository.existsByVehicleIdAndTypeAndStatus(vehicle.getId(), AlertType.HIGH_MOTOR_TEMPERATURE, AlertStatus.ACTIVE)).thenReturn(false);

        telemetryService.ingestTelemetry(telemetryRequestDto);

        verify(alertRepository, times(1)).save(argThat(alert ->
                alert.getType() == AlertType.HIGH_MOTOR_TEMPERATURE && alert.getVehicle().getId().equals(vehicle.getId())
        ));
    }

    @Test
    public void testGetTripTelemetry() {
        when(telemetryRepository.findByTripId(trip.getId())).thenReturn(Collections.singletonList(telemetry));

        List<TelemetryResponseDto> responseDtos = telemetryService.getTripTelemetry(trip.getId());

        assertNotNull(responseDtos);
        assertEquals(1, responseDtos.size());
        assertEquals(telemetry.getId(), responseDtos.get(0).getId());
        verify(telemetryRepository, times(1)).findByTripId(trip.getId());
    }

    @Test
    public void testGetLatestTelemetry_Success() {
        when(telemetryRepository.findFirstByTripIdOrderByTimestampDesc(trip.getId())).thenReturn(Optional.of(telemetry));

        TelemetryResponseDto responseDto = telemetryService.getLatestTelemetry(trip.getId());

        assertNotNull(responseDto);
        assertEquals(telemetry.getId(), responseDto.getId());
        verify(telemetryRepository, times(1)).findFirstByTripIdOrderByTimestampDesc(trip.getId());
    }

    @Test
    public void testGetLatestTelemetry_NotFound() {
        when(telemetryRepository.findFirstByTripIdOrderByTimestampDesc(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(NoTelemetryFoundException.class, () -> telemetryService.getLatestTelemetry(UUID.randomUUID()));
    }
}