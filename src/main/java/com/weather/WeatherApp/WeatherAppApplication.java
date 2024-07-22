package com.weather.WeatherApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
//import org.springframework.web.client.RestOperations;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class WeatherAppApplication {

	/*@Bean
	public RestOperations restTemplate(RestTemplateBuilder builder)
	{
		return builder.build();
	}*/

	public static void main(String[] args) {
		SpringApplication.run(WeatherAppApplication.class, args);
	}

	@Bean
	public WebClient.Builder webClientBuilder() {
		return WebClient.builder();
	}

}
