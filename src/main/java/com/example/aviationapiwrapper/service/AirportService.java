package com.example.aviationapiwrapper.service;

import com.example.aviationapiwrapper.model.Airport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AirportService {

    public Mono<Optional<Airport>> getAirportByIcao(String icao) {
        return Mono.just(Optional.empty());
    }
}
