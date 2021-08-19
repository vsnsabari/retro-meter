package com.vsnsabari.retrometer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(value = {"com.vsnsabari.retrometer.entities"})
public class RetroMeterApplication {

	public static void main(String[] args) {
		SpringApplication.run(RetroMeterApplication.class, args);
	}

}
