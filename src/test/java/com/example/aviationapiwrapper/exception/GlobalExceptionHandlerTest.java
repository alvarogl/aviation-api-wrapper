package com.example.aviationapiwrapper.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldPreserve4xxStatusFromUpstream() {
        WebClientResponseException ex = WebClientResponseException.create(404, "Not Found", HttpHeaders.EMPTY,
                                                                           "Airport missing".getBytes(StandardCharsets.UTF_8),
                                                                           StandardCharsets.UTF_8);

        StepVerifier.create(handler.onClient(ex))
                    .assertNext(pd -> {
                        assertEquals(HttpStatus.NOT_FOUND.value(), pd.getStatus());
                        assertEquals("Airport missing", pd.getDetail());
                    })
                    .verifyComplete();
    }

    @Test
    void shouldMapNon4xxErrorsToBadGateway() {
        WebClientResponseException ex = WebClientResponseException.create(502, "Bad Gateway", HttpHeaders.EMPTY,
                                                                           new byte[0], StandardCharsets.UTF_8);

        StepVerifier.create(handler.onClient(ex))
                    .assertNext(pd -> {
                        assertEquals(HttpStatus.BAD_GATEWAY.value(), pd.getStatus());
                        assertNotNull(pd.getDetail());
                    })
                    .verifyComplete();
    }
}
