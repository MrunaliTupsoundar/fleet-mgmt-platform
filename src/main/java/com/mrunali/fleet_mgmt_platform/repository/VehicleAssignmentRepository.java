package com.mrunali.fleet_mgmt_platform.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mrunali.fleet_mgmt_platform.entity.VehicleAssignment;
import com.mrunali.fleet_mgmt_platform.entity.enums.AssignmentStatus;

public interface VehicleAssignmentRepository extends JpaRepository<VehicleAssignment,UUID>{
    
    List<VehicleAssignment> findByDriverId(UUID driverId);
    List<VehicleAssignment> findByVehicleId(UUID vehicleId);
    List<VehicleAssignment> findByAssignedById(UUID assignedById);
    List<VehicleAssignment> findByStatus(String status);
    Optional<VehicleAssignment> findByDriverIdAndVehicleId(UUID driverId, UUID vehicleId);
    long countByStatus(AssignmentStatus status);
    
}
