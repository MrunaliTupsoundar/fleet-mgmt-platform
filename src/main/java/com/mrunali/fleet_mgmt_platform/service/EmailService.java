package com.mrunali.fleet_mgmt_platform.service;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
}