package com.peakle.shuttle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Peakle Shuttle 애플리케이션 진입점 */
@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class ShuttleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShuttleApplication.class, args);
	}

}
