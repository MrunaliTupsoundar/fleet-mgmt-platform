package com.mrunali.fleet_mgmt_platform.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mrunali.fleet_mgmt_platform.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle,UUID>{

    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);
    boolean existsByVehicleNumber(String vehicleNumber);
    
}
