package com.example.biocard.utils;

import androidx.annotation.Nullable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/** Date conversions between storage (ISO yyyy-MM-dd), display, and MaterialDatePicker (UTC millis). */
public final class DateUtils {

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);

    private DateUtils() {
    }

    @Nullable
    public static LocalDate parse(@Nullable String isoDate) {
        if (isoDate == null || isoDate.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(isoDate.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** "2000-01-01" -> "01 January 2000". Returns an empty string for invalid input. */
    public static String toDisplay(@Nullable String isoDate) {
        LocalDate date = parse(isoDate);
        return date == null ? "" : DISPLAY_FORMAT.format(date);
    }

    public static String fromUtcMillis(long utcMillis) {
        return Instant.ofEpochMilli(utcMillis).atZone(ZoneOffset.UTC).toLocalDate().toString();
    }

    @Nullable
    public static Long toUtcMillis(@Nullable String isoDate) {
        LocalDate date = parse(isoDate);
        return date == null ? null : date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }
}

