package com.example.aviationapiwrapper.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class Airport {
    String siteNumber;
    String type;
    String facilityName;
    String faaIdent;
    String icaoIdent;
    String region;
    String districtOffice;
    String state;
    String stateFull;
    String county;
    String city;
    String ownership;
    String use;
    String manager;
    String managerPhone;
    Double latitude;
    Double latitudeSec;
    Double longitude;
    Double longitudeSec;
    Integer elevation;
    Integer tpa;
    Integer magneticVariation;
    String vfrSectional;
    String boundaryArtcc;
    String boundaryArtccName;
    String responsibleArtcc;
    String responsibleArtccName;
    String fssPhoneNumber;
    String fssPhoneNumberTollfree;
    String notamFacilityIdent;
    String status;
    String certificationTypedate;
    boolean customsAirportOfEntry;
    boolean militaryJointUse;
    boolean militaryLanding;
    String lightingSchedule;
    String beaconSchedule;
    boolean controlTower;
    Double unicom;
    Double ctaf;
    LocalDate effectiveDate;
}