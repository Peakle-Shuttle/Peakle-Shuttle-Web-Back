package com.peakle.shuttle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ShuttleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShuttleApplication.class, args);
	}

}
