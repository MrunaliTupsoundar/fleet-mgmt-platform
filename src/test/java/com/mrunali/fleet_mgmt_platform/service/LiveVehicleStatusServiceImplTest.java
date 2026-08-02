package com.mrunali.fleet_mgmt_platform.service;

import com.mrunali.fleet_mgmt_platform.dto.response.LiveVehicleStatusResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.LiveVehicleStatus;
import com.mrunali.fleet_mgmt_platform.entity.Vehicle;
import com.mrunali.fleet_mgmt_platform.entity.enums.ConnectionStatus;
import com.mrunali.fleet_mgmt_platform.exception.LiveVehicleStatusNotFoundException;
import com.mrunali.fleet_mgmt_platform.repository.LiveVehicleStatusRepository;
import com.mrunali.fleet_mgmt_platform.service.impl.LiveVehicleStatusServiceImpl;
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
import static org.mockito.Mockito.*;

public class LiveVehicleStatusServiceImplTest {

    @Mock
    private LiveVehicleStatusRepository liveVehicleStatusRepository;

    @InjectMocks
    private LiveVehicleStatusServiceImpl liveVehicleStatusService;

    private LiveVehicleStatus liveVehicleStatus;
    private Vehicle vehicle;
    private UUID vehicleId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        vehicleId = UUID.randomUUID();
        vehicle = new Vehicle();
        vehicle.setId(vehicleId);

        liveVehicleStatus = new LiveVehicleStatus();
        liveVehicleStatus.setId(UUID.randomUUID());
        liveVehicleStatus.setVehicle(vehicle);
        liveVehicleStatus.setLatitude(12.9716);
        liveVehicleStatus.setLongitude(77.5946);
        liveVehicleStatus.setBatteryPercentage(85.5);
        liveVehicleStatus.setStateOfHealth(98.0);
        liveVehicleStatus.setOdometer(12345.6);
        liveVehicleStatus.setLastSeen(LocalDateTime.now());
        liveVehicleStatus.setConnectionStatus(ConnectionStatus.CONNECTED);
    }

    @Test
    public void testGetLiveVehicleStatus_Success() {
        when(liveVehicleStatusRepository.findByVehicleId(vehicleId)).thenReturn(Optional.of(liveVehicleStatus));

        LiveVehicleStatusResponseDto responseDto = liveVehicleStatusService.getLiveVehicleStatus(vehicleId);

        assertNotNull(responseDto);
        assertEquals(vehicleId, responseDto.getVehicleId());
        assertEquals(liveVehicleStatus.getLatitude(), responseDto.getLatitude());
        verify(liveVehicleStatusRepository, times(1)).findByVehicleId(vehicleId);
    }

    @Test
    public void testGetLiveVehicleStatus_NotFound() {
        when(liveVehicleStatusRepository.findByVehicleId(vehicleId)).thenReturn(Optional.empty());

        assertThrows(LiveVehicleStatusNotFoundException.class, () -> liveVehicleStatusService.getLiveVehicleStatus(vehicleId));
        verify(liveVehicleStatusRepository, times(1)).findByVehicleId(vehicleId);
    }

    @Test
    public void testGetAllLiveVehicleStatuses() {
        when(liveVehicleStatusRepository.findAll()).thenReturn(Collections.singletonList(liveVehicleStatus));

        List<LiveVehicleStatusResponseDto> responseDtos = liveVehicleStatusService.getAllLiveVehicleStatuses();

        assertNotNull(responseDtos);
        assertEquals(1, responseDtos.size());
        assertEquals(vehicleId, responseDtos.get(0).getVehicleId());
        verify(liveVehicleStatusRepository, times(1)).findAll();
    }

    @Test
    public void testGetConnectedVehicles() {
        when(liveVehicleStatusRepository.findByConnectionStatus(ConnectionStatus.CONNECTED)).thenReturn(Collections.singletonList(liveVehicleStatus));

        List<LiveVehicleStatusResponseDto> responseDtos = liveVehicleStatusService.getConnectedVehicles();

        assertNotNull(responseDtos);
        assertEquals(1, responseDtos.size());
        assertEquals(ConnectionStatus.CONNECTED, responseDtos.get(0).getConnectionStatus());
        verify(liveVehicleStatusRepository, times(1)).findByConnectionStatus(ConnectionStatus.CONNECTED);
    }

    @Test
    public void testGetDisconnectedVehicles() {
        liveVehicleStatus.setConnectionStatus(ConnectionStatus.DISCONNECTED);
        when(liveVehicleStatusRepository.findByConnectionStatus(ConnectionStatus.DISCONNECTED)).thenReturn(Collections.singletonList(liveVehicleStatus));

        List<LiveVehicleStatusResponseDto> responseDtos = liveVehicleStatusService.getDisconnectedVehicles();

        assertNotNull(responseDtos);
        assertEquals(1, responseDtos.size());
        assertEquals(ConnectionStatus.DISCONNECTED, responseDtos.get(0).getConnectionStatus());
        verify(liveVehicleStatusRepository, times(1)).findByConnectionStatus(ConnectionStatus.DISCONNECTED);
    }
}