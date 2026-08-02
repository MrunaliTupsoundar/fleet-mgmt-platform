package com.mrunali.fleet_mgmt_platform.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mrunali.fleet_mgmt_platform.entity.Trip;
import com.mrunali.fleet_mgmt_platform.entity.enums.TripStatus;

public interface TripRepository extends JpaRepository<Trip,UUID>{
    
    List<Trip> findByDriverId(UUID driverId);
    List<Trip> findByVehicleId(UUID vehicleId);
    List<Trip> findByStatus(TripStatus status);
    long countByStatus(TripStatus status);
    List<Trip> findByDriverIdAndStatus(UUID driverId, TripStatus status);
    List<Trip> findByVehicleIdAndStatus(UUID vehicleId, TripStatus status);

}