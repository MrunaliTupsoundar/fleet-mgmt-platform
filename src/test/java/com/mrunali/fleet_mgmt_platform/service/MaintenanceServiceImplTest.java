package com.mrunali.fleet_mgmt_platform.service;

import com.mrunali.fleet_mgmt_platform.dto.request.CompleteMaintenanceRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.CreateMaintenanceRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.MaintenanceResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.Maintenance;
import com.mrunali.fleet_mgmt_platform.entity.User;
import com.mrunali.fleet_mgmt_platform.entity.Vehicle;
import com.mrunali.fleet_mgmt_platform.entity.enums.MaintenanceStatus;
import com.mrunali.fleet_mgmt_platform.exception.MaintenanceNotFoundException;
import com.mrunali.fleet_mgmt_platform.exception.VehicleNotFoundException;
import com.mrunali.fleet_mgmt_platform.repository.MaintenanceRepository;
import com.mrunali.fleet_mgmt_platform.repository.UserRepository;
import com.mrunali.fleet_mgmt_platform.repository.VehicleRepository;
import com.mrunali.fleet_mgmt_platform.service.impl.MaintenanceServiceImpl;
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

public class MaintenanceServiceImplTest {

    @Mock
    private MaintenanceRepository maintenanceRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MaintenanceServiceImpl maintenanceService;

    private Vehicle vehicle;
    private User user;
    private Maintenance maintenance;
    private UUID maintenanceId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        vehicle = new Vehicle();
        vehicle.setId(UUID.randomUUID());

        user = new User();
        user.setId(UUID.randomUUID());

        maintenanceId = UUID.randomUUID();
        maintenance = Maintenance.builder()
                .id(maintenanceId)
                .vehicle(vehicle)
                .reportedBy(user)
                .description("Routine checkup")
                .status(MaintenanceStatus.SCHEDULED)
                .scheduledDate(LocalDateTime.now().plusDays(5).toLocalDate())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void testCreateMaintenance_Success() {
        CreateMaintenanceRequestDto requestDto = new CreateMaintenanceRequestDto();
        requestDto.setVehicleId(vehicle.getId());
        requestDto.setReportedById(user.getId());
        requestDto.setDescription("Routine checkup");
        requestDto.setScheduledDate(LocalDateTime.now().plusDays(5).toLocalDate());

        when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(maintenance);

        MaintenanceResponseDto responseDto = maintenanceService.createMaintenance(requestDto);

        assertNotNull(responseDto);
        assertEquals(maintenance.getId(), responseDto.getId());
        verify(maintenanceRepository, times(1)).save(any(Maintenance.class));
    }

    @Test
    public void testCreateMaintenance_VehicleNotFound() {
        CreateMaintenanceRequestDto requestDto = new CreateMaintenanceRequestDto();
        requestDto.setVehicleId(UUID.randomUUID());

        when(vehicleRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(VehicleNotFoundException.class, () -> maintenanceService.createMaintenance(requestDto));
    }

    @Test
    public void testGetAllMaintenance() {
        when(maintenanceRepository.findAll()).thenReturn(Collections.singletonList(maintenance));

        List<MaintenanceResponseDto> responseDtos = maintenanceService.getAllMaintenance();

        assertNotNull(responseDtos);
        assertEquals(1, responseDtos.size());
        verify(maintenanceRepository, times(1)).findAll();
    }

    @Test
    public void testGetMaintenanceById_Success() {
        when(maintenanceRepository.findById(maintenanceId)).thenReturn(Optional.of(maintenance));

        MaintenanceResponseDto responseDto = maintenanceService.getMaintenanceById(maintenanceId);

        assertNotNull(responseDto);
        assertEquals(maintenanceId, responseDto.getId());
        verify(maintenanceRepository, times(1)).findById(maintenanceId);
    }

    @Test
    public void testGetMaintenanceById_NotFound() {
        when(maintenanceRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(MaintenanceNotFoundException.class, () -> maintenanceService.getMaintenanceById(UUID.randomUUID()));
    }

    @Test
    public void testMarkAsInProgress_Success() {
        when(maintenanceRepository.findById(maintenanceId)).thenReturn(Optional.of(maintenance));
        when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(maintenance);

        MaintenanceResponseDto responseDto = maintenanceService.markAsInProgress(maintenanceId);

        assertEquals(MaintenanceStatus.IN_PROGRESS, responseDto.getStatus());
        verify(maintenanceRepository, times(1)).save(maintenance);
    }

    @Test
    public void testMarkAsCompleted_Success() {
        CompleteMaintenanceRequestDto requestDto = new CompleteMaintenanceRequestDto();
        requestDto.setRemarks("All checks passed.");

        when(maintenanceRepository.findById(maintenanceId)).thenReturn(Optional.of(maintenance));
        when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(maintenance);

        MaintenanceResponseDto responseDto = maintenanceService.markAsCompleted(maintenanceId, requestDto);

        assertEquals(MaintenanceStatus.COMPLETED, responseDto.getStatus());
        assertEquals("All checks passed.", responseDto.getRemarks());
        assertNotNull(responseDto.getCompletedDate());
        verify(maintenanceRepository, times(1)).save(maintenance);
    }

    @Test
    public void testDeleteMaintenance() {
        doNothing().when(maintenanceRepository).deleteById(maintenanceId);

        maintenanceService.deleteMaintenance(maintenanceId);

        verify(maintenanceRepository, times(1)).deleteById(maintenanceId);
    }
}