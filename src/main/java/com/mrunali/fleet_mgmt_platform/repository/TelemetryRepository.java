package com.mrunali.fleet_mgmt_platform.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mrunali.fleet_mgmt_platform.entity.Telemetry;

public interface TelemetryRepository extends JpaRepository<Telemetry,UUID>{

    List<Telemetry> findByTripId(UUID tripId);
    Optional<Telemetry> findFirstByTripIdOrderByTimestampDesc(UUID tripId);

}
