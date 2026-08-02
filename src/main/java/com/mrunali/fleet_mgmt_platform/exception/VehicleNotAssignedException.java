package com.mrunali.fleet_mgmt_platform.exception;

public class VehicleNotAssignedException extends RuntimeException {
    public VehicleNotAssignedException(String message) {
        super(message);
    }
}