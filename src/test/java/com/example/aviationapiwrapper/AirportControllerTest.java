package com.example.aviationapiwrapper;

import com.example.aviationapiwrapper.controller.AirportController;
import com.example.aviationapiwrapper.model.Airport;
import com.example.aviationapiwrapper.service.AirportService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

class AirportControllerTest {
    @Test
    void shouldReturn200WhenFound() {
        var airport = Airport.builder().icaoIdent("KAVL").facilityName("ASHEVILLE RGNL").build();
        var service = new AirportService(icao -> Mono.just(airport));
        var controller = new AirportController(service);
        ResponseEntity<Airport> res = controller.getAirportByIcao("KAVL").block();
        assertEquals(HttpStatusCode.valueOf(200), res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals("KAVL", res.getBody().getIcaoIdent());
    }

    @Test
    void shouldReturn404WhenMissing() {
        var service = new AirportService(icao -> Mono.empty());
        var controller = new AirportController(service);
        ResponseEntity<Airport> res = controller.getAirportByIcao("XXXX").block();
        assertEquals(HttpStatusCode.valueOf(404), res.getStatusCode());
        assertNull(res.getBody());
    }

    @Test
    void shouldPropagateUpstreamErrors() {
        var service = new AirportService(icao -> Mono.error(new IllegalStateException("boom")));
        var controller = new AirportController(service);
        assertThrows(IllegalStateException.class, () -> controller.getAirportByIcao("KAVL").block());
    }
}
