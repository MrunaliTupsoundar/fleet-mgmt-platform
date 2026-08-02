package com.mrunali.fleet_mgmt_platform.exception;

public class TokenGenerationFailedException extends RuntimeException {
    public TokenGenerationFailedException(String message) {
        super(message);
    }
}