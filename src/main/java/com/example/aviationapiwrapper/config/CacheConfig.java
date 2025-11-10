package com.example.aviationapiwrapper.config;

import com.example.aviationapiwrapper.model.Airport;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class CacheConfig {

    @Bean
    public Cache<String, Mono<Airport>> airportCache(
            @Value("${app.cache.airports.spec:maximumSize=1000,expireAfterWrite=10m}") String cacheSpec) {
        return Caffeine.from(cacheSpec).build();
    }
}
