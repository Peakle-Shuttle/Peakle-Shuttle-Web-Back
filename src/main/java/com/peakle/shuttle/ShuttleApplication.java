package com.peakle.shuttle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/** Peakle Shuttle 애플리케이션 진입점 */
@SpringBootApplication
@EnableFeignClients
public class ShuttleApplication {

	/**
	 * Spring Boot 애플리케이션을 시작한다.
	 *
	 * @param args 애플리케이션에 전달되는 명령줄 인수
	 */
	public static void main(String[] args) {
		SpringApplication.run(ShuttleApplication.class, args);
	}

}