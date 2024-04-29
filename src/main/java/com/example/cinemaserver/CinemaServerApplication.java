package com.example.cinemaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CinemaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CinemaServerApplication.class, args);
	}

}
