package com.example.aviationapiwrapper.provider.aviationapi;

import com.example.aviationapiwrapper.model.Airport;
import com.example.aviationapiwrapper.provider.AviationProvider;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.example.aviationapiwrapper.config.WebClientConfig.AVIATION_WEB_CLIENT;

@Component
@Slf4j
public class AviationApiClient implements AviationProvider {
    private final WebClient aviationWebClient;
    private final AviationApiMapper mapper;
    private final String airportsPath;

    public AviationApiClient(@Qualifier(AVIATION_WEB_CLIENT) WebClient aviationWebClient, AviationApiMapper mapper,
                             @Value("${app.aviation.airports-path:/v1/airports}") String airportsPath) {
        this.aviationWebClient = aviationWebClient;
        this.mapper = mapper;
        this.airportsPath = airportsPath;
    }

    @Override
    @CircuitBreaker(name = "aviation", fallbackMethod = "fallback")
    @Retry(name = "aviation")
    @TimeLimiter(name = "aviation")
    public Mono<Airport> getAirportByIcao(String icao) {
        return aviationWebClient.get()
                                .uri(b -> b.path(airportsPath).queryParam("apt", icao).build())
                                .retrieve()
                                .onStatus(HttpStatusCode::is4xxClientError, rsp -> rsp.createException().flatMap(Mono::error))
                                .onStatus(HttpStatusCode::is5xxServerError, rsp -> rsp.createException().flatMap(Mono::error))
                                .bodyToMono(Map.class)
                                .flatMap(body -> Mono.justOrEmpty(mapper.toAirport(icao, body)));
    }

    private Mono<Airport> fallback(String icao, Throwable t) {
        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Airport %s not found".formatted(icao), t));
    }
}
