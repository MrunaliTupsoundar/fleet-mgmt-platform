package com.mrunali.fleet_mgmt_platform.exception;

public class InvalidAlertStatusException extends RuntimeException {
    public InvalidAlertStatusException(String message) {
        super(message);
    }
}