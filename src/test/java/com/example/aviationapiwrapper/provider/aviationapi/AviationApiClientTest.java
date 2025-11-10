package com.example.aviationapiwrapper.provider.aviationapi;

import com.example.aviationapiwrapper.model.Airport;
import com.github.benmanes.caffeine.cache.Cache;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class AviationApiClientTest {

    private static final MockWebServer server;

    static {
        try {
            server = new MockWebServer();
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    private AviationApiClient client;

    @Autowired
    private Cache<String, Mono<Airport>> cache;

    @AfterAll
    static void shutdown() throws IOException {
        server.shutdown();
    }

    @AfterEach
    void resetState() {
        cache.invalidateAll();
    }

    @DynamicPropertySource
    static void registerBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("app.aviation.base-url", () -> server.url("/").toString());
    }

    @Test
    void shouldCacheAirportResponses() {
        long initialCount = server.getRequestCount();
        server.enqueue(new MockResponse().setResponseCode(200)
                                         .addHeader("Content-Type", "application/json")
                                         .setBody("""
                                                          {
                                                            "KAVL": [
                                                              {
                                                                "site_number": "12345",
                                                                "type": "AIRPORT",
                                                                "facility_name": "ASHEVILLE RGNL",
                                                                "faa_ident": "AVL",
                                                                "icao_ident": "KAVL",
                                                                "region": "AEA",
                                                                "district_office": "CLT",
                                                                "state": "NC",
                                                                "state_full": "NORTH CAROLINA",
                                                                "county": "BUNCOMBE",
                                                                "city": "ASHEVILLE",
                                                                "ownership": "PU",
                                                                "use": "PU",
                                                                "manager": "JOHN DOE",
                                                                "manager_phone": "(828) 555-0000",
                                                                "latitude": "35-26-14.4000N",
                                                                "latitude_sec": "127574.4000N",
                                                                "longitude": "082-32-40.1000W",
                                                                "longitude_sec": "297160.1000W",
                                                                "elevation": "2165",
                                                                "magnetic_variation": "06W",
                                                                "vfr_sectional": "CHARLOTTE",
                                                                "boundary_artcc": "ZTL",
                                                                "boundary_artcc_name": "ATLANTA",
                                                                "responsible_artcc": "ZTL",
                                                                "responsible_artcc_name": "ATLANTA",
                                                                "fss_phone_number": "8001234567",
                                                                "fss_phone_numer_tollfree": "8007654321",
                                                                "notam_facility_ident": "AVL",
                                                                "status": "O",
                                                                "certification_typedate": "I A 01/2000",
                                                                "customs_airport_of_entry": "N",
                                                                "military_joint_use": "N",
                                                                "military_landing": "N",
                                                                "lighting_schedule": "SUNSET-SUNRISE",
                                                                "beacon_schedule": "SS-SR",
                                                                "control_tower": "Y",
                                                                "unicom": "123.050",
                                                                "ctaf": "118.200",
                                                                "effective_date": "11/04/2021"
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

        assertEquals(initialCount + 1, server.getRequestCount());
    }
}
