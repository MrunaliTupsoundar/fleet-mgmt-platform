package com.mrunali.fleet_mgmt_platform.exception;

public class TelemetryNotFoundException extends RuntimeException {
    public TelemetryNotFoundException(String message) {
        super(message);
    }
}