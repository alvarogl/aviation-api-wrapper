package com.example.aviationapiwrapper;


import com.example.aviationapiwrapper.model.Airport;
import com.example.aviationapiwrapper.provider.aviationapi.AviationApiMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AviationApiMapperTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final AviationApiMapper mapper = new AviationApiMapper();

    @Test
    void shouldMapTypedFields() throws JsonProcessingException {
        Map input = OBJECT_MAPPER.readValue("""
                                                    {
                                                        "KJFK": [
                                                            {
                                                                "site_number": "15793.*A",
                                                                "type": "AIRPORT",
                                                                "facility_name": "JOHN F KENNEDY INTL",
                                                                "faa_ident": "JFK",
                                                                "icao_ident": "KJFK",
                                                                "region": "AEA",
                                                                "district_office": "NYC",
                                                                "state": "NY",
                                                                "state_full": "NEW YORK",
                                                                "county": "QUEENS",
                                                                "city": "NEW YORK",
                                                                "ownership": "PU",
                                                                "use": "PU",
                                                                "manager": "CHARLES EVERETT",
                                                                "manager_phone": "(718) 244-3501",
                                                                "latitude": "40-38-23.7400N",
                                                                "latitude_sec": "146303.7400N",
                                                                "longitude": "073-46-43.2930W",
                                                                "longitude_sec": "265603.2930W",
                                                                "elevation": "13",
                                                                "magnetic_variation": "13W",
                                                                "tpa": "",
                                                                "vfr_sectional": "NEW YORK",
                                                                "boundary_artcc": "ZNY",
                                                                "boundary_artcc_name": "NEW YORK",
                                                                "responsible_artcc": "ZNY",
                                                                "responsible_artcc_name": "NEW YORK",
                                                                "fss_phone_number": "",
                                                                "fss_phone_numer_tollfree": "1-800-WX-BRIEF",
                                                                "notam_facility_ident": "JFK",
                                                                "status": "O",
                                                                "certification_typedate": "I E S 05/1973",
                                                                "customs_airport_of_entry": "N",
                                                                "military_joint_use": "N",
                                                                "military_landing": "Y",
                                                                "lighting_schedule": "",
                                                                "beacon_schedule": "SS-SR",
                                                                "control_tower": "Y",
                                                                "unicom": "122.950",
                                                                "ctaf": "",
                                                                "effective_date": "11/04/2021"
                                                            }
                                                        ]
                                                    }
                                                    """, Map.class);
        Airport a = mapper.toAirport("KJFK", input);
        assertEquals("KJFK", a.getIcaoIdent());
        assertTrue(a.isControlTower());
        assertEquals(-13, a.getMagneticVariation());
        assertEquals(40.6399, a.getLatitude(), 0.001);
        assertEquals(-73.7786, a.getLongitude(), 0.001);
    }
}