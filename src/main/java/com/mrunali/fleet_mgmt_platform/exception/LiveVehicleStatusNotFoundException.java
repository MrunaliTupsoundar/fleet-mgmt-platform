package com.mrunali.fleet_mgmt_platform.exception;

public class LiveVehicleStatusNotFoundException extends RuntimeException {
    public LiveVehicleStatusNotFoundException(String message) {
        super(message);
    }
}