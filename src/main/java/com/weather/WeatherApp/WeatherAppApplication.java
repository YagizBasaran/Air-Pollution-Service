package com.weather.WeatherApp;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
//import org.springframework.web.client.RestOperations;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

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
		HttpClient httpClient = HttpClient.create()
				.responseTimeout(Duration.ofMinutes(5))
				.doOnConnected(conn ->
						conn.addHandlerLast(new ReadTimeoutHandler(5))
								.addHandlerLast(new WriteTimeoutHandler(5))
				);

		return WebClient.builder()
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.codecs(configurer -> configurer
						.defaultCodecs()
						.maxInMemorySize(16 * 1024 * 1024)); // 16MB
	}

}
