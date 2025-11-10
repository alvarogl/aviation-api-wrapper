package com.example.aviationapiwrapper.service;

import com.example.aviationapiwrapper.model.Airport;
import com.example.aviationapiwrapper.provider.AviationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AirportService {
    private final AviationProvider provider;


    public Mono<Optional<Airport>> getAirportByIcao(String icao) {
        return provider.getAirportByIcao(icao)
                       .map(Optional::of)
                       .switchIfEmpty(Mono.just(Optional.empty()));
    }
}
