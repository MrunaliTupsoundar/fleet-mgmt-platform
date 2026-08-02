package com.mrunali.fleet_mgmt_platform.exception;

public class AlertAlreadyExistsException extends RuntimeException {
    public AlertAlreadyExistsException(String message) {
        super(message);
    }
}