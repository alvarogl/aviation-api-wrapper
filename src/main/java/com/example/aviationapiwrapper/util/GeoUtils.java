package com.example.aviationapiwrapper.util;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class GeoUtils {
    private static final Pattern DMS_PATTERN = Pattern.compile("(\\d{2,3})-(\\d{2})-(\\d{2}\\.?\\d*)([NSEW])");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US);

    private GeoUtils() {
    }

    public static Optional<Double> dmsToDecimal(String dms) {
        if (StringUtils.isBlank(dms)) {
            return Optional.empty();
        }
        Matcher m = DMS_PATTERN.matcher(dms.trim().toUpperCase());
        if (!m.matches()) {
            return Optional.empty();
        }
        double deg = Double.parseDouble(m.group(1));
        double min = Double.parseDouble(m.group(2));
        double sec = Double.parseDouble(m.group(3));
        double val = deg + (min / 60d) + (sec / 3600d);
        char hemi = m.group(4).charAt(0);
        if (hemi == 'S' || hemi == 'W') {
            val = -val;
        }
        return Optional.of(val);
    }

    public static Optional<Integer> parseMagneticVariation(String in) {
        if (StringUtils.isBlank(in)) {
            return Optional.empty();
        }
        in = in.trim().toUpperCase();
        try {
            int v = Integer.parseInt(in.replaceAll("\\D", ""));
            return Optional.of(in.endsWith("E") ? v : -v);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<LocalDate> parseEffectiveDate(String date) {
        if (StringUtils.isBlank(date)) {
            return Optional.empty();
        }
        try {
            return Optional.of(LocalDate.parse(date.trim(), DATE_FMT));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}