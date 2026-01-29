package com.peakle.shuttle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/** Peakle Shuttle 애플리케이션 진입점 */
@SpringBootApplication
@EnableFeignClients
public class ShuttleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShuttleApplication.class, args);
	}

}
