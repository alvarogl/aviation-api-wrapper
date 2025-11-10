package com.example.aviationapiwrapper.provider.aviationapi;

import com.example.aviationapiwrapper.model.Airport;
import com.example.aviationapiwrapper.observability.MetricsService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AviationApiClientTest {
    private MockWebServer server;
    private AviationApiClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        WebClient webClient = WebClient.builder().baseUrl(server.url("/").toString()).build();
        Cache<String, Mono<Airport>> cache = Caffeine.newBuilder().build();
        MetricsService metricsService = new MetricsService(new SimpleMeterRegistry());
        client = new AviationApiClient(webClient, new AviationApiMapper(), "/v1/airports", metricsService, cache);
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void shouldCacheAirportResponses() {
        server.enqueue(new MockResponse().setResponseCode(200)
                                         .addHeader("Content-Type", "application/json")
                                         .setBody("""
                                                  {
                                                    \"KAVL\": [
                                                      {
                                                        \"site_number\": \"12345\",
                                                        \"type\": \"AIRPORT\",
                                                        \"facility_name\": \"ASHEVILLE RGNL\",
                                                        \"faa_ident\": \"AVL\",
                                                        \"icao_ident\": \"KAVL\",
                                                        \"region\": \"AEA\",
                                                        \"district_office\": \"CLT\",
                                                        \"state\": \"NC\",
                                                        \"state_full\": \"NORTH CAROLINA\",
                                                        \"county\": \"BUNCOMBE\",
                                                        \"city\": \"ASHEVILLE\",
                                                        \"ownership\": \"PU\",
                                                        \"use\": \"PU\",
                                                        \"manager\": \"JOHN DOE\",
                                                        \"manager_phone\": \"(828) 555-0000\",
                                                        \"latitude\": \"35-26-14.4000N\",
                                                        \"latitude_sec\": \"127574.4000N\",
                                                        \"longitude\": \"082-32-40.1000W\",
                                                        \"longitude_sec\": \"297160.1000W\",
                                                        \"elevation\": \"2165\",
                                                        \"magnetic_variation\": \"06W\",
                                                        \"vfr_sectional\": \"CHARLOTTE\",
                                                        \"boundary_artcc\": \"ZTL\",
                                                        \"boundary_artcc_name\": \"ATLANTA\",
                                                        \"responsible_artcc\": \"ZTL\",
                                                        \"responsible_artcc_name\": \"ATLANTA\",
                                                        \"fss_phone_number\": \"8001234567\",
                                                        \"fss_phone_number_tollfree\": \"8007654321\",
                                                        \"notam_facility_ident\": \"AVL\",
                                                        \"status\": \"O\",
                                                        \"certification_typedate\": \"I A 01/2000\",
                                                        \"customs_airport_of_entry\": \"N\",
                                                        \"military_joint_use\": \"N\",
                                                        \"military_landing\": \"N\",
                                                        \"lighting_schedule\": \"SUNSET-SUNRISE\",
                                                        \"beacon_schedule\": \"SS-SR\",
                                                        \"control_tower\": \"Y\",
                                                        \"unicom\": \"123.050\",
                                                        \"ctaf\": \"118.200\",
                                                        \"effective_date\": \"11/04/2021\"
                                                      }
                                                    ]
                                                  }
                                                  """));

        StepVerifier.create(client.getAirportByIcao("KAVL"))
                    .assertNext(a -> assertEquals("ASHEVILLE RGNL", a.getFacilityName()))
                    .verifyComplete();

        StepVerifier.create(client.getAirportByIcao("KAVL"))
                    .assertNext(a -> assertEquals("ASHEVILLE RGNL", a.getFacilityName()))
                    .verifyComplete();

        assertEquals(1, server.getRequestCount());
    }
}
