package com.nai.reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@PropertySource("file:${user.home}\\teetimes\\application.properties")
@EnableScheduling
public class ReservationApplication {
	public static void main(String[] args) {
		SpringApplication.run(ReservationApplication.class, args);
	}
}
