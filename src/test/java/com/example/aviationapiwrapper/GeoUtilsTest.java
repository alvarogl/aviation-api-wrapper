package com.example.aviationapiwrapper;

import com.example.aviationapiwrapper.util.GeoUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class GeoUtilsTest {
    @Test
    void shouldConvertDmsToDecimal() {
        assertEquals(35.4344, GeoUtils.dmsToDecimal("35-26-04.0000N").orElseThrow(), 0.0001);
        assertEquals(-82.5427, GeoUtils.dmsToDecimal("082-32-33.8240W").orElseThrow(), 0.0001);
    }

    @Test
    void shouldParseMagVar() {
        assertEquals(-7, GeoUtils.parseMagneticVariation("07W").orElseThrow());
        assertEquals(3, GeoUtils.parseMagneticVariation("03E").orElseThrow());
        assertTrue(GeoUtils.parseMagneticVariation("??").isEmpty());
    }

    @Test
    void shouldParseEffectiveDate() {
        assertEquals(LocalDate.of(2019, 1, 3), GeoUtils.parseEffectiveDate("01/03/2019").orElseThrow());
        assertTrue(GeoUtils.parseEffectiveDate("").isEmpty());
    }
}