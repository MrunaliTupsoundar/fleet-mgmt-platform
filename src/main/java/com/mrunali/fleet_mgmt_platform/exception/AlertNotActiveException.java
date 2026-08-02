package com.mrunali.fleet_mgmt_platform.exception;

public class AlertNotActiveException extends RuntimeException {
    public AlertNotActiveException(String message) {
        super(message);
    }
}