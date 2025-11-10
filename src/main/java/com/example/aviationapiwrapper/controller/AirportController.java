package com.example.aviationapiwrapper.controller;

import com.example.aviationapiwrapper.model.Airport;
import com.example.aviationapiwrapper.service.AirportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/airports")
@RequiredArgsConstructor
public class AirportController {
    private final AirportService service;

    @Operation(summary = "Get airport details by ICAO code", description = "Returns 404 if the airport is not found.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Airport found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Airport.class),
                            examples = @ExampleObject(value = "{\n  \"icaoIdent\": \"KAVL\",\n  \"facilityName\": \"ASHEVILLE RGNL\",\n  \"state\": \"NC\",\n  \"city\": \"ASHEVILLE\",\n  \"elevation\": 2162,\n  \"controlTower\": true\n}")
                    )),
            @ApiResponse(responseCode = "404", description = "Airport not found",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Airport not found\",\n  \"icao\": \"XXXX\"\n}"))),
            @ApiResponse(responseCode = "502", description = "Upstream provider unavailable")
    })
    @GetMapping("/{icao}")
    public Mono<ResponseEntity<Airport>> getAirportByIcao(@PathVariable String icao) {
        return service.getAirportByIcao(icao)
                      .flatMap(opt -> opt.map(Mono::just).orElseGet(Mono::empty))
                      .map(ResponseEntity::ok)
                      .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
