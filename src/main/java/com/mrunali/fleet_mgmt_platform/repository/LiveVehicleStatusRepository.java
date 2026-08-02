package com.mrunali.fleet_mgmt_platform.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mrunali.fleet_mgmt_platform.entity.LiveVehicleStatus;
import com.mrunali.fleet_mgmt_platform.entity.enums.ConnectionStatus;

public interface LiveVehicleStatusRepository extends JpaRepository<LiveVehicleStatus,UUID>{
    
        Optional<LiveVehicleStatus> findByVehicleId(UUID vehicleId);
        List<LiveVehicleStatus> findByConnectionStatus(ConnectionStatus connected);

}
