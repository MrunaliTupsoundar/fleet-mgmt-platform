package com.mrunali.fleet_mgmt_platform.dto.response;

import com.mrunali.fleet_mgmt_platform.entity.enums.AssignmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleAssignmentResponseDto {

    private UUID id;
    private UUID driverId;
    private UUID vehicleId;
    private UUID assignedById;
    private LocalDateTime assignedFrom;
    private LocalDateTime assignedTo;
    private AssignmentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}