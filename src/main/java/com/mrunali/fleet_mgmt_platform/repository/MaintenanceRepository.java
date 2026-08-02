package com.mrunali.fleet_mgmt_platform.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mrunali.fleet_mgmt_platform.entity.Maintenance;
import com.mrunali.fleet_mgmt_platform.entity.enums.MaintenanceStatus;

public interface MaintenanceRepository extends JpaRepository<Maintenance,UUID>{

    long countByStatus(MaintenanceStatus status);

} 
