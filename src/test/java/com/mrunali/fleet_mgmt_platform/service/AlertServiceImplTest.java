package com.mrunali.fleet_mgmt_platform.service;

import com.mrunali.fleet_mgmt_platform.dto.request.AcknowledgeAlertRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.CreateAlertRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.ResolveAlertRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.AlertResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.Alert;
import com.mrunali.fleet_mgmt_platform.entity.Telemetry;
import com.mrunali.fleet_mgmt_platform.entity.Trip;
import com.mrunali.fleet_mgmt_platform.entity.User;
import com.mrunali.fleet_mgmt_platform.entity.Vehicle;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertSeverity;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertStatus;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertType;
import com.mrunali.fleet_mgmt_platform.exception.AlertNotAcknowledgedException;
import com.mrunali.fleet_mgmt_platform.exception.AlertNotActiveException;
import com.mrunali.fleet_mgmt_platform.exception.AlertNotFoundException;
import com.mrunali.fleet_mgmt_platform.exception.TelemetryNotFoundException;
import com.mrunali.fleet_mgmt_platform.exception.UserNotFoundException;
import com.mrunali.fleet_mgmt_platform.repository.AlertRepository;
import com.mrunali.fleet_mgmt_platform.repository.TelemetryRepository;
import com.mrunali.fleet_mgmt_platform.repository.UserRepository;
import com.mrunali.fleet_mgmt_platform.service.impl.AlertServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AlertServiceImplTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private TelemetryRepository telemetryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AlertServiceImpl alertService;

    private Alert alert;
    private Telemetry telemetry;
    private Trip trip;
    private Vehicle vehicle;
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        vehicle = new Vehicle();
        vehicle.setId(UUID.randomUUID());

        trip = new Trip();
        trip.setId(UUID.randomUUID());
        trip.setVehicle(vehicle);

        telemetry = new Telemetry();
        telemetry.setId(UUID.randomUUID());
        telemetry.setTrip(trip);

        user = new User();
        user.setId(UUID.randomUUID());

        alert = new Alert();
        alert.setId(UUID.randomUUID());
        alert.setVehicle(vehicle);
        alert.setTelemetry(telemetry);
        alert.setType(AlertType.LOW_BATTERY);
        alert.setSeverity(AlertSeverity.HIGH);
        alert.setStatus(AlertStatus.ACTIVE);
        alert.setMessage("Low battery alert");
        alert.setCreatedAt(LocalDateTime.now());
    }

    @Test
    public void testGetAlerts() {
        when(alertRepository.findAll()).thenReturn(Arrays.asList(alert));

        List<AlertResponseDto> alerts = alertService.getAlerts();

        assertNotNull(alerts);
        assertEquals(1, alerts.size());
        assertEquals(alert.getId(), alerts.get(0).getId());

        verify(alertRepository, times(1)).findAll();
    }

    @Test
    public void testCreateAlert() {
        CreateAlertRequestDto requestDto = new CreateAlertRequestDto();
        requestDto.setTelemetryId(telemetry.getId());
        requestDto.setType(AlertType.LOW_BATTERY);
        requestDto.setSeverity(AlertSeverity.HIGH);
        requestDto.setMessage("Low battery alert");

        when(telemetryRepository.findById(telemetry.getId())).thenReturn(Optional.of(telemetry));
        when(alertRepository.save(any(Alert.class))).thenReturn(alert);

        AlertResponseDto responseDto = alertService.createAlert(requestDto);

        assertNotNull(responseDto);
        assertEquals(alert.getId(), responseDto.getId());
        assertEquals(alert.getVehicle().getId(), responseDto.getVehicleId());
        assertEquals(alert.getTelemetry().getId(), responseDto.getTelemetryId());
        assertEquals(alert.getType(), responseDto.getType());
        assertEquals(alert.getSeverity(), responseDto.getSeverity());
        assertEquals(alert.getStatus(), responseDto.getStatus());
        assertEquals(alert.getMessage(), responseDto.getMessage());

        verify(telemetryRepository, times(1)).findById(telemetry.getId());
        verify(alertRepository, times(1)).save(any(Alert.class));
    }

    @Test
    public void testCreateAlert_TelemetryNotFound() {
        CreateAlertRequestDto requestDto = new CreateAlertRequestDto();
        requestDto.setTelemetryId(UUID.randomUUID());
        requestDto.setType(AlertType.LOW_BATTERY);
        requestDto.setSeverity(AlertSeverity.HIGH);
        requestDto.setMessage("Low battery alert");

        when(telemetryRepository.findById(requestDto.getTelemetryId())).thenReturn(Optional.empty());

        assertThrows(TelemetryNotFoundException.class, () -> alertService.createAlert(requestDto));

        verify(telemetryRepository, times(1)).findById(requestDto.getTelemetryId());
        verify(alertRepository, never()).save(any(Alert.class));
    }

    @Test
    public void testGetAlertsByVehicleId() {
        when(alertRepository.findByVehicleId(vehicle.getId())).thenReturn(Arrays.asList(alert));

        List<AlertResponseDto> alerts = alertService.getAlertsByVehicleId(vehicle.getId());

        assertNotNull(alerts);
        assertEquals(1, alerts.size());
        assertEquals(alert.getId(), alerts.get(0).getId());

        verify(alertRepository, times(1)).findByVehicleId(vehicle.getId());
    }

    @Test
    public void testGetAlertsByTelemetryId() {
        when(alertRepository.findByTelemetryId(telemetry.getId())).thenReturn(Arrays.asList(alert));

        List<AlertResponseDto> alerts = alertService.getAlertsByTelemetryId(telemetry.getId());

        assertNotNull(alerts);
        assertEquals(1, alerts.size());
        assertEquals(alert.getId(), alerts.get(0).getId());

        verify(alertRepository, times(1)).findByTelemetryId(telemetry.getId());
    }

    @Test
    public void testGetAlertsByStatus() {
        when(alertRepository.findByStatus(AlertStatus.ACTIVE)).thenReturn(Arrays.asList(alert));

        List<AlertResponseDto> alerts = alertService.getAlertsByStatus(AlertStatus.ACTIVE);

        assertNotNull(alerts);
        assertEquals(1, alerts.size());
        assertEquals(alert.getId(), alerts.get(0).getId());

        verify(alertRepository, times(1)).findByStatus(AlertStatus.ACTIVE);
    }

    @Test
    public void testGetAlertsByType() {
        when(alertRepository.findByType(AlertType.LOW_BATTERY)).thenReturn(Arrays.asList(alert));

        List<AlertResponseDto> alerts = alertService.getAlertsByType(AlertType.LOW_BATTERY);

        assertNotNull(alerts);
        assertEquals(1, alerts.size());
        assertEquals(alert.getId(), alerts.get(0).getId());

        verify(alertRepository, times(1)).findByType(AlertType.LOW_BATTERY);
    }

    @Test
    public void testGetAlertsBySeverity() {
        when(alertRepository.findBySeverity(AlertSeverity.HIGH)).thenReturn(Arrays.asList(alert));

        List<AlertResponseDto> alerts = alertService.getAlertsBySeverity(AlertSeverity.HIGH);

        assertNotNull(alerts);
        assertEquals(1, alerts.size());
        assertEquals(alert.getId(), alerts.get(0).getId());

        verify(alertRepository, times(1)).findBySeverity(AlertSeverity.HIGH);
    }

    @Test
    public void testGetAlertsByStatusAndType() {
        when(alertRepository.findByStatusAndType(AlertStatus.ACTIVE, AlertType.LOW_BATTERY)).thenReturn(Arrays.asList(alert));

        List<AlertResponseDto> alerts = alertService.getAlertsByStatusAndType(AlertStatus.ACTIVE, AlertType.LOW_BATTERY);

        assertNotNull(alerts);
        assertEquals(1, alerts.size());
        assertEquals(alert.getId(), alerts.get(0).getId());

        verify(alertRepository, times(1)).findByStatusAndType(AlertStatus.ACTIVE, AlertType.LOW_BATTERY);
    }

    @Test
    public void testGetAlertsByStatusAndSeverity() {
        when(alertRepository.findByStatusAndSeverity(AlertStatus.ACTIVE, AlertSeverity.HIGH)).thenReturn(Arrays.asList(alert));

        List<AlertResponseDto> alerts = alertService.getAlertsByStatusAndSeverity(AlertStatus.ACTIVE, AlertSeverity.HIGH);

        assertNotNull(alerts);
        assertEquals(1, alerts.size());
        assertEquals(alert.getId(), alerts.get(0).getId());

        verify(alertRepository, times(1)).findByStatusAndSeverity(AlertStatus.ACTIVE, AlertSeverity.HIGH);
    }

    @Test
    public void testGetAlertsByTypeAndSeverity() {
        when(alertRepository.findByTypeAndSeverity(AlertType.LOW_BATTERY, AlertSeverity.HIGH)).thenReturn(Arrays.asList(alert));

        List<AlertResponseDto> alerts = alertService.getAlertsByTypeAndSeverity(AlertType.LOW_BATTERY, AlertSeverity.HIGH);

        assertNotNull(alerts);
        assertEquals(1, alerts.size());
        assertEquals(alert.getId(), alerts.get(0).getId());

        verify(alertRepository, times(1)).findByTypeAndSeverity(AlertType.LOW_BATTERY, AlertSeverity.HIGH);
    }

    @Test
    public void testGetAlertsByStatusAndTypeAndSeverity() {
        when(alertRepository.findByStatusAndTypeAndSeverity(AlertStatus.ACTIVE, AlertType.LOW_BATTERY, AlertSeverity.HIGH)).thenReturn(Arrays.asList(alert));

        List<AlertResponseDto> alerts = alertService.getAlertsByStatusAndTypeAndSeverity(AlertStatus.ACTIVE, AlertType.LOW_BATTERY, AlertSeverity.HIGH);

        assertNotNull(alerts);
        assertEquals(1, alerts.size());
        assertEquals(alert.getId(), alerts.get(0).getId());

        verify(alertRepository, times(1)).findByStatusAndTypeAndSeverity(AlertStatus.ACTIVE, AlertType.LOW_BATTERY, AlertSeverity.HIGH);
    }

    @Test
    public void testAcknowledgeAlert() {
        AcknowledgeAlertRequestDto requestDto = new AcknowledgeAlertRequestDto();
        requestDto.setAlertId(alert.getId());
        requestDto.setUserId(user.getId());

        when(alertRepository.findById(alert.getId())).thenReturn(Optional.of(alert));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(alertRepository.save(any(Alert.class))).thenReturn(alert);

        AlertResponseDto responseDto = alertService.acknowledgeAlert(requestDto);

        assertNotNull(responseDto);
        assertEquals(alert.getId(), responseDto.getId());
        assertEquals(AlertStatus.ACKNOWLEDGED, responseDto.getStatus());
        assertEquals(user.getId(), responseDto.getAcknowledgedById());

        verify(alertRepository, times(1)).findById(alert.getId());
        verify(userRepository, times(1)).findById(user.getId());
        verify(alertRepository, times(1)).save(any(Alert.class));
    }

    @Test
    public void testAcknowledgeAlert_AlertNotFound() {
        AcknowledgeAlertRequestDto requestDto = new AcknowledgeAlertRequestDto();
        requestDto.setAlertId(UUID.randomUUID());
        requestDto.setUserId(user.getId());

        when(alertRepository.findById(requestDto.getAlertId())).thenReturn(Optional.empty());

        assertThrows(AlertNotFoundException.class, () -> alertService.acknowledgeAlert(requestDto));

        verify(alertRepository, times(1)).findById(requestDto.getAlertId());
        verify(userRepository, never()).findById(user.getId());
        verify(alertRepository, never()).save(any(Alert.class));
    }

    @Test
    public void testAcknowledgeAlert_UserNotFound() {
        AcknowledgeAlertRequestDto requestDto = new AcknowledgeAlertRequestDto();
        requestDto.setAlertId(alert.getId());
        requestDto.setUserId(UUID.randomUUID());

        when(alertRepository.findById(alert.getId())).thenReturn(Optional.of(alert));
        when(userRepository.findById(requestDto.getUserId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> alertService.acknowledgeAlert(requestDto));

        verify(alertRepository, times(1)).findById(alert.getId());
        verify(userRepository, times(1)).findById(requestDto.getUserId());
        verify(alertRepository, never()).save(any(Alert.class));
    }

    @Test
    public void testAcknowledgeAlert_AlertNotActive() {
        AcknowledgeAlertRequestDto requestDto = new AcknowledgeAlertRequestDto();
        requestDto.setAlertId(alert.getId());
        requestDto.setUserId(user.getId());

        alert.setStatus(AlertStatus.ACKNOWLEDGED); // Not active

        when(alertRepository.findById(alert.getId())).thenReturn(Optional.of(alert));

        assertThrows(AlertNotActiveException.class, () -> alertService.acknowledgeAlert(requestDto));

        verify(alertRepository, times(1)).findById(alert.getId());
        verify(userRepository, never()).findById(any(UUID.class));
        verify(alertRepository, never()).save(any(Alert.class));
    }

    @Test
    public void testResolveAlert() {
        alert.setStatus(AlertStatus.ACKNOWLEDGED);

        ResolveAlertRequestDto requestDto = new ResolveAlertRequestDto();
        requestDto.setAlertId(alert.getId());
        requestDto.setUserId(user.getId());

        when(alertRepository.findById(alert.getId())).thenReturn(Optional.of(alert));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(alertRepository.save(any(Alert.class))).thenReturn(alert);

        AlertResponseDto responseDto = alertService.resolveAlert(requestDto);

        assertNotNull(responseDto);
        assertEquals(alert.getId(), responseDto.getId());
        assertEquals(AlertStatus.RESOLVED, responseDto.getStatus());
        assertEquals(user.getId(), responseDto.getResolvedById());

        verify(alertRepository, times(1)).findById(alert.getId());
        verify(userRepository, times(1)).findById(user.getId());
        verify(alertRepository, times(1)).save(any(Alert.class));
    }

    @Test
    public void testResolveAlert_AlertNotFound() {
        ResolveAlertRequestDto requestDto = new ResolveAlertRequestDto();
        requestDto.setAlertId(UUID.randomUUID());
        requestDto.setUserId(user.getId());

        when(alertRepository.findById(requestDto.getAlertId())).thenReturn(Optional.empty());

        assertThrows(AlertNotFoundException.class, () -> alertService.resolveAlert(requestDto));

        verify(alertRepository, times(1)).findById(requestDto.getAlertId());
        verify(userRepository, never()).findById(user.getId());
        verify(alertRepository, never()).save(any(Alert.class));
    }

    @Test
    public void testResolveAlert_UserNotFound() {
        ResolveAlertRequestDto requestDto = new ResolveAlertRequestDto();
        requestDto.setAlertId(alert.getId());
        requestDto.setUserId(UUID.randomUUID());

        alert.setStatus(AlertStatus.ACKNOWLEDGED);

        when(alertRepository.findById(alert.getId())).thenReturn(Optional.of(alert));
        when(userRepository.findById(requestDto.getUserId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> alertService.resolveAlert(requestDto));

        verify(alertRepository, times(1)).findById(alert.getId());
        verify(userRepository, times(1)).findById(requestDto.getUserId());
        verify(alertRepository, never()).save(any(Alert.class));
    }

    @Test
    public void testResolveAlert_AlertNotAcknowledged() {
        ResolveAlertRequestDto requestDto = new ResolveAlertRequestDto();
        requestDto.setAlertId(alert.getId());
        requestDto.setUserId(user.getId());

        alert.setStatus(AlertStatus.ACTIVE); // Not acknowledged

        when(alertRepository.findById(alert.getId())).thenReturn(Optional.of(alert));

        assertThrows(AlertNotAcknowledgedException.class, () -> alertService.resolveAlert(requestDto));

        verify(alertRepository, times(1)).findById(alert.getId());
        verify(userRepository, never()).findById(any(UUID.class));
        verify(alertRepository, never()).save(any(Alert.class));
    }

    @Test
    public void testGetAlertsByAcknowledgedById() {
        when(alertRepository.findByAcknowledgedById(user.getId())).thenReturn(Arrays.asList(alert));

        List<AlertResponseDto> alerts = alertService.getAlertsByAcknowledgedById(user.getId());

        assertNotNull(alerts);
        assertEquals(1, alerts.size());
        assertEquals(alert.getId(), alerts.get(0).getId());

        verify(alertRepository, times(1)).findByAcknowledgedById(user.getId());
    }

    @Test
    public void testGetAlertsByResolvedById() {
        when(alertRepository.findByResolvedById(user.getId())).thenReturn(Arrays.asList(alert));

        List<AlertResponseDto> alerts = alertService.getAlertsByResolvedById(user.getId());

        assertNotNull(alerts);
        assertEquals(1, alerts.size());
        assertEquals(alert.getId(), alerts.get(0).getId());

        verify(alertRepository, times(1)).findByResolvedById(user.getId());
    }
}