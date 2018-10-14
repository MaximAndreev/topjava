package ru.javawebinar.topjava.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static <T extends Comparable<T>> boolean isBetween(T date, T start, T end) {
        return date.compareTo(start) >= 0 && date.compareTo(end) <= 0;
    }

    public static String toString(LocalDateTime ldt) {
        return ldt == null ? "" : ldt.format(DATE_TIME_FORMATTER);
    }

    public static LocalTime toLocalTime(String localTimeString) {
        try {
            return LocalTime.parse(localTimeString, TIME_FORMATTER);
        } catch (NullPointerException | DateTimeParseException e) {
            return null;
        }
    }

    public static LocalDate toLocalDate(String localDateString) {
        try {
            return LocalDate.parse(localDateString, DATE_FORMATTER);
        } catch (NullPointerException | DateTimeParseException e) {
            return null;
        }
    }
}
