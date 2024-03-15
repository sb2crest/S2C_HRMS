package com.employee.management.converters;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class DateTimeConverterTest {
    @Test
    void testConvertDateToLocalDateTime() {
        DateTimeConverter converter = new DateTimeConverter();
        Date date = new Date();
        LocalDateTime localDateTime = DateTimeConverter.convertDateToLocalDateTime(date);
        assertEquals(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), localDateTime.toLocalDate());
        assertEquals(0, localDateTime.getHour());
        assertEquals(0, localDateTime.getMinute());
        assertEquals(0, localDateTime.getSecond());

        // Test with null input
        assertNull(DateTimeConverter.convertDateToLocalDateTime(null));
    }

    @Test
    void testLocalDateTimeToStringConverter() {
        DateTimeConverter converter = new DateTimeConverter();
        Date date = new Date();
        String formattedDate = converter.localDateTimeToStringConverter(date);
        assertEquals(DateTimeFormatter.ofPattern("dd-MMM-yyyy").format(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()), formattedDate);

        // Test with null input
        assertNull(converter.localDateTimeToStringConverter(null));
    }

    @Test
    void testStringToLocalDateTimeConverter() {
        DateTimeConverter converter = new DateTimeConverter();
        String dateString = "01-Jan-2023";
        Date date = converter.stringToLocalDateTimeConverter(dateString);
        assertEquals(LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH)), date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        // Test with null input
        assertNull(converter.stringToLocalDateTimeConverter(null));
    }

}