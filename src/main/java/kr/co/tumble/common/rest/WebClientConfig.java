package kr.co.tumble.common.rest;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * WebClientConfig
 */
@Configuration
public class WebClientConfig {

	// MAX_IN_MEMORY_SIZE 설정값
	private static final int WEBCLIENT_MAX_IN_MEMORY_SIZE = 100 * 1024 * 1024; // 100M
	
	@Value("5")
	private long connectTimeout;

	@Value("60")
	private long readTimeout;

	@Bean
	public WebClient webClient() {

		HttpClient httpClient = HttpClient.create()
		        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int)Duration.ofSeconds(connectTimeout).toMillis()) // connection timeout
		        .responseTimeout(Duration.ofSeconds(readTimeout)); // response timeout

		WebClient webClient = WebClient.builder()
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(WEBCLIENT_MAX_IN_MEMORY_SIZE))
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.build();

		return webClient;
		
	}

}
