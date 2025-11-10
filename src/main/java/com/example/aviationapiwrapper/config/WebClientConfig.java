package com.example.aviationapiwrapper.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {
    public static final String AVIATION_WEB_CLIENT = "aviationWebClient";
    @Value("${app.aviation.base-url}")
    private String baseUrl;
    @Value("${app.aviation.connect-timeout-ms}")
    private int connectTimeoutMs;
    @Value("${app.aviation.read-timeout-ms}")
    private int readTimeoutMs;

    @Bean(name = AVIATION_WEB_CLIENT)
    public WebClient aviationWebClient() {
        ConnectionProvider pool =
                ConnectionProvider.builder("aviation-pool").maxConnections(200).pendingAcquireTimeout(Duration.ofSeconds(5))
                                  .build();
        HttpClient httpClient = HttpClient.create(pool)
                                          .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
                                          .responseTimeout(Duration.ofMillis(readTimeoutMs))
                                          .doOnConnected(c -> c.addHandlerLast(
                                                  new ReadTimeoutHandler(readTimeoutMs, TimeUnit.MILLISECONDS)));
        return WebClient.builder()
                        .baseUrl(baseUrl)
                        .clientConnector(new ReactorClientHttpConnector(httpClient))
                        .build();
    }
}
