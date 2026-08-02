package com.mrunali.fleet_mgmt_platform.exception;

public class AlertNotAcknowledgedException extends RuntimeException {
    public AlertNotAcknowledgedException(String message) {
        super(message);
    }
    
}
