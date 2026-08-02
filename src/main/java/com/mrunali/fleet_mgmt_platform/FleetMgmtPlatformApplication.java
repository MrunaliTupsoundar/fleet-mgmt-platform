package com.mrunali.fleet_mgmt_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FleetMgmtPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(FleetMgmtPlatformApplication.class, args);
		System.out.println("Fleet Management Platform Application started successfully.");
		System.out.println("Access the application at http://localhost:8080");
	}

}
