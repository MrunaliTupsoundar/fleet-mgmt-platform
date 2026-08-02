package com.mrunali.fleet_mgmt_platform.exception;

public class EmailSendingFailedException extends RuntimeException {
    public EmailSendingFailedException(String message) {
        super(message);
    }
}