package com.mrunali.fleet_mgmt_platform.exception;

public class InvitationAlreadyAcceptedException extends RuntimeException {
    public InvitationAlreadyAcceptedException(String message) {
        super(message);
    }
}