package com.mrunali.fleet_mgmt_platform.exception;

public class InvitationAlreadyExistsException extends RuntimeException {
    public InvitationAlreadyExistsException(String message) {
        super(message);
    }
}