package com.mrunali.fleet_mgmt_platform.repository;

import com.mrunali.fleet_mgmt_platform.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvitationRepository extends JpaRepository<Invitation, UUID> {
    Optional<Invitation> findByEmail(String email);
    Optional<Invitation> findByToken(String token);
}