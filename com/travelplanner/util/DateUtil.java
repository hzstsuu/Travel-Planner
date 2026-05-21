package com.travelplanner.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class DateUtil {
    private DateUtil() {
    }

    public static LocalDate parseDate(String input) {
        try {
            return LocalDate.parse(input.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd.");
        }
    }
}
