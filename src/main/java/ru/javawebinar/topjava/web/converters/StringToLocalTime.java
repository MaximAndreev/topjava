package ru.javawebinar.topjava.web.converters;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class StringToLocalTime implements Converter<String, LocalTime> {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm[:ss]");

    @Override
    public LocalTime convert(String source) {
        if (source.isEmpty()) {
            return null;
        }
        try {
            return LocalTime.parse(source, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException | ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
