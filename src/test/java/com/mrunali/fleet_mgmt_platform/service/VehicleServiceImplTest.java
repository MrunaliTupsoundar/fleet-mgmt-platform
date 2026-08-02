package com.mrunali.fleet_mgmt_platform.service;

import com.mrunali.fleet_mgmt_platform.dto.request.VehicleRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.VehicleResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.Vehicle;
import com.mrunali.fleet_mgmt_platform.entity.enums.VehicleStatus;
import com.mrunali.fleet_mgmt_platform.exception.VehicleAlreadyExistsException;
import com.mrunali.fleet_mgmt_platform.exception.VehicleNotFoundException;
import com.mrunali.fleet_mgmt_platform.repository.VehicleRepository;
import com.mrunali.fleet_mgmt_platform.service.impl.VehicleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VehicleServiceImplTest{

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private Vehicle vehicle;
    private VehicleRequestDto vehicleRequestDto;
    private UUID vehicleId;
    private String vehicleNumber;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        vehicleId = UUID.randomUUID();
        vehicleNumber = "MH12AB1234";

        vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setVehicleNumber(vehicleNumber);
        vehicle.setManufacturer("Tesla");
        vehicle.setModel("Model S");
        vehicle.setStatus(VehicleStatus.AVAILABLE);

        vehicleRequestDto = new VehicleRequestDto();
        vehicleRequestDto.setVehicleNumber(vehicleNumber);
        vehicleRequestDto.setManufacturer("Tesla");
        vehicleRequestDto.setModel("Model S");
        vehicleRequestDto.setManufactureYear(2023);
        vehicleRequestDto.setBatteryCapacity(100.0);
        vehicleRequestDto.setStatus(VehicleStatus.AVAILABLE);
    }

    @Test
    public void testCreateVehicle_Success() {
        when(vehicleRepository.existsByVehicleNumber(anyString())).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleResponseDto responseDto = vehicleService.createVehicle(vehicleRequestDto);

        assertNotNull(responseDto);
        assertEquals(vehicle.getVehicleNumber(), responseDto.getVehicleNumber());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    public void testCreateVehicle_VehicleAlreadyExists() {
        when(vehicleRepository.existsByVehicleNumber(anyString())).thenReturn(true);

        assertThrows(VehicleAlreadyExistsException.class, () -> vehicleService.createVehicle(vehicleRequestDto));
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    public void testGetAllVehicles() {
        when(vehicleRepository.findAll()).thenReturn(Collections.singletonList(vehicle));

        List<VehicleResponseDto> responseDtos = vehicleService.getAllVehicles();

        assertNotNull(responseDtos);
        assertEquals(1, responseDtos.size());
        verify(vehicleRepository, times(1)).findAll();
    }

    @Test
    public void testGetVehicleById_Success() {
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));

        VehicleResponseDto responseDto = vehicleService.getVehicleById(vehicleId);

        assertNotNull(responseDto);
        assertEquals(vehicleId, responseDto.getId());
        verify(vehicleRepository, times(1)).findById(vehicleId);
    }

    @Test
    public void testGetVehicleById_NotFound() {
        when(vehicleRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(VehicleNotFoundException.class, () -> vehicleService.getVehicleById(UUID.randomUUID()));
    }

    @Test
    public void testGetVehicleByVehicleNumber_Success() {
        when(vehicleRepository.findByVehicleNumber(vehicleNumber)).thenReturn(Optional.of(vehicle));

        VehicleResponseDto responseDto = vehicleService.getVehicleByVehicleNumber(vehicleNumber);

        assertNotNull(responseDto);
        assertEquals(vehicleNumber, responseDto.getVehicleNumber());
        verify(vehicleRepository, times(1)).findByVehicleNumber(vehicleNumber);
    }

    @Test
    public void testGetVehicleByVehicleNumber_NotFound() {
        when(vehicleRepository.findByVehicleNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(VehicleNotFoundException.class, () -> vehicleService.getVehicleByVehicleNumber("NONEXISTENT"));
    }

    @Test
    public void testUpdateVehicleByVehicleNumber_Success() {
        when(vehicleRepository.findByVehicleNumber(vehicleNumber)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleResponseDto responseDto = vehicleService.updateVehicleByVehicleNumber(vehicleNumber, vehicleRequestDto);

        assertNotNull(responseDto);
        assertEquals(vehicleRequestDto.getModel(), responseDto.getModel());
        verify(vehicleRepository, times(1)).save(vehicle);
    }

    @Test
    public void testDeleteVehicleByVehicleNumber_Success() {
        when(vehicleRepository.findByVehicleNumber(vehicleNumber)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        vehicleService.deleteVehicleByVehicleNumber(vehicleNumber);

        verify(vehicleRepository, times(1)).save(argThat(v -> v.getStatus() == VehicleStatus.INACTIVE));
    }

    @Test
    public void testDeleteVehicleByVehicleNumber_NotFound() {
        when(vehicleRepository.findByVehicleNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(VehicleNotFoundException.class, () -> vehicleService.deleteVehicleByVehicleNumber("NONEXISTENT"));
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }
}