package com.mrunali.fleet_mgmt_platform.exception;

public class TripNotActiveException extends RuntimeException {
    public TripNotActiveException(String message) {
        super(message);
    }
}