package com.example.aviationapiwrapper.it;

import com.example.aviationapiwrapper.model.Airport;
import com.github.benmanes.caffeine.cache.Cache;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class AviationApiFlowIT {

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
    private WebTestClient webTestClient;

    @Autowired
    private Cache<String, Mono<Airport>> airportCache;

    @AfterAll
    static void tearDownServer() throws IOException {
        server.shutdown();
    }

    @AfterEach
    void resetState() {
        airportCache.invalidateAll();
    }

    @DynamicPropertySource
    static void overrideBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("app.aviation.base-url", () -> server.url("/").toString());
    }

    @Test
    void shouldReturnAirportFromController() {
        server.enqueue(new MockResponse().setResponseCode(200)
                                         .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                         .setBody("""
                                                  {
                                                    "KAVL": [
                                                      {
                                                        "site_number": "12345",
                                                        "type": "AIRPORT",
                                                        "facility_name": "ASHEVILLE RGNL",
                                                        "faa_ident": "AVL",
                                                        "icao_ident": "KAVL"
                                                      }
                                                    ]
                                                  }
                                                  """));

        webTestClient.get().uri("/api/airports/{icao}", "KAVL")
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody()
                     .jsonPath("$.icaoIdent").isEqualTo("KAVL")
                     .jsonPath("$.facilityName").isEqualTo("ASHEVILLE RGNL");
    }

    @Test
    void shouldPropagateNotFoundFromUpstream() {
        server.enqueue(new MockResponse().setResponseCode(404)
                                         .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                         .setBody("Airport missing"));

        webTestClient.get().uri("/api/airports/{icao}", "XXXX")
                     .exchange()
                     .expectStatus().isNotFound()
                     .expectBody()
                     .jsonPath("$.status").isEqualTo(404)
                     .jsonPath("$.detail").isEqualTo("Airport missing");
    }

    @Test
    void shouldReturnBadGatewayWhenUpstreamFails() {
        server.enqueue(new MockResponse().setResponseCode(500)
                                         .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                         .setBody("Internal error"));

        webTestClient.get().uri("/api/airports/{icao}", "KAVL")
                     .exchange()
                     .expectStatus().isEqualTo(502)
                     .expectBody()
                     .jsonPath("$.status").isEqualTo(502);
    }
}
