package com.mrunali.fleet_mgmt_platform.service;

import java.util.List;
import java.util.UUID;

import com.mrunali.fleet_mgmt_platform.dto.request.StartTripRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.EndTripRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.TripResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.enums.TripStatus;

public interface TripService {

    TripResponseDto startTrip(StartTripRequestDto requestDto);
    TripResponseDto endTrip(EndTripRequestDto requestDto);
    List<TripResponseDto> getAllTrips();
    TripResponseDto getTripById(UUID id);
    List<TripResponseDto> getTripsByDriverId(UUID driverId);
    List<TripResponseDto> getTripsByVehicleId(UUID vehicleId);
    List<TripResponseDto> getTripsByStatus(TripStatus status);
    List<TripResponseDto> getTripsByDriverIdAndStatus(UUID driverId, TripStatus status);
    List<TripResponseDto> getTripsByVehicleIdAndStatus(UUID vehicleId, TripStatus status);
    void updateMaxSpeed(UUID tripId, Double speed);

}