package com.mrunali.fleet_mgmt_platform.dto.response;

import com.mrunali.fleet_mgmt_platform.entity.enums.AssignmentStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AssignmentResponseDto {
    private UUID id;
    private UUID driverId;
    private UUID vehicleId;
    private AssignmentStatus status;
    private LocalDateTime assignedFrom;
}