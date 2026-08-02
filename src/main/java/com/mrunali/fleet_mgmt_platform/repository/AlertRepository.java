package com.mrunali.fleet_mgmt_platform.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mrunali.fleet_mgmt_platform.entity.Alert;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertSeverity;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertStatus;
import com.mrunali.fleet_mgmt_platform.entity.enums.AlertType;

public interface AlertRepository extends JpaRepository<Alert, UUID> {

    List<Alert> findByVehicleId(UUID vehicleId);
    List<Alert> findByTelemetryId(UUID telemetryId);
    List<Alert> findByStatus(AlertStatus status);
    List<Alert> findByType(AlertType type);
    List<Alert> findBySeverity(AlertSeverity severity);
    List<Alert> findByStatusAndType(AlertStatus status, AlertType type);
    List<Alert> findByStatusAndSeverity(AlertStatus status, AlertSeverity severity);
    List<Alert> findByTypeAndSeverity(AlertType type, AlertSeverity severity);
    List<Alert> findByStatusAndTypeAndSeverity(AlertStatus status, AlertType type, AlertSeverity severity);
    List<Alert> findByAcknowledgedById(UUID userId);
    List<Alert> findByResolvedById(UUID userId);
    boolean existsByVehicleIdAndTypeAndStatus(UUID vehicleId, AlertType type, AlertStatus status);
    Optional<Alert> findByVehicleIdAndTypeAndStatus(UUID vehicleId, AlertType type, AlertStatus status);
}