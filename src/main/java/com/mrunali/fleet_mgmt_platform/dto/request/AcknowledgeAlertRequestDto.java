package com.mrunali.fleet_mgmt_platform.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AcknowledgeAlertRequestDto {

    @NotNull(message = "Alert ID is required")
    private UUID alertId;

    @NotNull(message = "User ID is required")
    private UUID userId;
}