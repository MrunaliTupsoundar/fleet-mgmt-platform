package com.mrunali.fleet_mgmt_platform.dto.request;

import com.mrunali.fleet_mgmt_platform.entity.enums.VehicleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequestDto {

    @NotBlank(message = "Vehicle number is required")
    private String vehicleNumber;

    @NotNull(message = "Vehicle type is required")
    private String type;

    @NotBlank(message = "Manufacturer is required")
    private String manufacturer;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Manufacture year is required")
    @Positive(message = "Manufacture year must be a positive number")
    private Integer manufactureYear;

    @NotNull(message = "Battery capacity is required")
    @Positive(message = "Battery capacity must be a positive number")
    private Double batteryCapacity;

    @NotNull(message = "Status is required")
    private VehicleStatus status;
}