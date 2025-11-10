package com.example.aviationapiwrapper.provider;

import com.example.aviationapiwrapper.model.Airport;
import reactor.core.publisher.Mono;

public interface AviationProvider {
    Mono<Airport> getAirportByIcao(String icao);
}