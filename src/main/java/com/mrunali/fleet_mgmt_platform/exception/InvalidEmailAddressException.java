package com.mrunali.fleet_mgmt_platform.exception;

public class InvalidEmailAddressException extends RuntimeException {
    public InvalidEmailAddressException(String message) {
        super(message);
    }
}