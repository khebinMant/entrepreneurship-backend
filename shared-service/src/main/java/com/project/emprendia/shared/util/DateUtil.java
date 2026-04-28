package com.project.emprendia.shared.util;

import org.apache.commons.text.CaseUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter DEFAULT_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateUtil() {
    }

    /**
     * Formats a LocalDateTime to the default pattern yyyy-MM-dd HH:mm:ss.
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DEFAULT_FORMATTER);
    }

    /**
     * Formats a LocalDateTime to a custom pattern.
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Returns the current UTC timestamp.
     */
    public static LocalDateTime nowUtc() {
        return ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime();
    }

    /**
     * Converts a snake_case string to camelCase using Apache Commons Text.
     */
    public static String toCamelCase(String input) {
        if (input == null || input.isBlank()) return input;
        return CaseUtils.toCamelCase(input, false, '_');
    }
}
