package com.example.aviationapiwrapper.provider.aviationapi;

import com.example.aviationapiwrapper.model.Airport;
import com.example.aviationapiwrapper.util.GeoUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AviationApiMapper {

    public Airport toAirport(String lookupKey, Map<?, ?> raw) {
        Map<?, ?> m = unwrapSingleAirportObject(lookupKey, raw);

        return Airport.builder()
                      .siteNumber(s(m, "site_number"))
                      .type(s(m, "type"))
                      .facilityName(s(m, "facility_name"))
                      .faaIdent(s(m, "faa_ident"))
                      .icaoIdent(s(m, "icao_ident"))
                      .region(s(m, "region"))
                      .districtOffice(s(m, "district_office"))
                      .state(s(m, "state"))
                      .stateFull(s(m, "state_full"))
                      .county(s(m, "county"))
                      .city(s(m, "city"))
                      .ownership(s(m, "ownership"))
                      .use(s(m, "use"))
                      .manager(s(m, "manager"))
                      .managerPhone(s(m, "manager_phone"))
                      .latitude(GeoUtils.dmsToDecimal(s(m, "latitude")).orElse(null))
                      .latitudeSec(s(m, "latitude_sec"))
                      .longitude(GeoUtils.dmsToDecimal(s(m, "longitude")).orElse(null))
                      .longitudeSec(s(m, "latitude_sec"))
                      .elevation(intOrNull(s(m, "elevation")))
                      .tpa(intOrNull(s(m, "tpa")))
                      .magneticVariation(GeoUtils.parseMagneticVariation(s(m, "magnetic_variation")).orElse(null))
                      .vfrSectional(s(m, "vfr_sectional"))
                      .boundaryArtcc(s(m, "boundary_artcc"))
                      .boundaryArtccName(s(m, "boundary_artcc_name"))
                      .responsibleArtcc(s(m, "responsible_artcc"))
                      .responsibleArtccName(s(m, "responsible_artcc_name"))
                      .fssPhoneNumber(s(m, "fss_phone_number"))
                      .fssPhoneNumberTollfree(s(m, "fss_phone_number_tollfree"))
                      .notamFacilityIdent(s(m, "notam_facility_ident"))
                      .status(s(m, "status"))
                      .certificationTypedate(s(m, "certification_typedate"))
                      .customsAirportOfEntry(boolYN(s(m, "customs_airport_of_entry")))
                      .militaryJointUse(boolYN(s(m, "military_joint_use")))
                      .militaryLanding(boolYN(s(m, "military_landing")))
                      .lightingSchedule(s(m, "lighting_schedule"))
                      .beaconSchedule(s(m, "beacon_schedule"))
                      .controlTower(boolYN(s(m, "control_tower")))
                      .unicom(doubleOrNull(s(m, "unicom")))
                      .ctaf(doubleOrNull(s(m, "ctaf")))
                      .effectiveDate(GeoUtils.parseEffectiveDate(s(m, "effective_date")).orElse(null))
                      .build();
    }

    private Map<?, ?> unwrapSingleAirportObject(String lookupKey, Map<?, ?> raw) {
        Object byKey = raw.get(lookupKey);
        if (byKey instanceof Map<?, ?> map) {
            return map;
        }
        if (byKey instanceof java.util.List<?> list) {
            Object first = list.isEmpty() ? null : list.get(0);
            if (first instanceof Map<?, ?> m) {
                return m;
            }
        }

        if (raw.size() == 1) {
            Object onlyVal = raw.values().iterator().next();
            if (onlyVal instanceof Map<?, ?> map) {
                return map;
            }
            if (onlyVal instanceof java.util.List<?> list) {
                Object first = list.isEmpty() ? null : list.get(0);
                if (first instanceof Map<?, ?> m) {
                    return m;
                }
            }
        }

        return raw;
    }

    private String s(Map<?, ?> m, String key) {
        Object v = m.get(key);
        return org.apache.commons.lang3.StringUtils.trimToNull(v == null ? null : v.toString());
    }

    private Integer intOrNull(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(s);
        } catch (Exception e) {
            return null;
        }
    }

    private Double doubleOrNull(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return Double.valueOf(s);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean boolYN(String s) {
        return ("Y".equalsIgnoreCase(s) || "TRUE".equalsIgnoreCase(s));
    }
}

