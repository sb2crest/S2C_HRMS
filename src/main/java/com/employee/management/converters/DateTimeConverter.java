package com.employee.management.converters;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;


@Component
public class DateTimeConverter {
    public static LocalDateTime convertDateToLocalDateTime(Date date) {
        if(date==null)
            return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String dateString = dateFormat.format(date);
            return LocalDateTime.parse(dateString + "T00:00:00");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String localDateTimeToStringConverter(Date date) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = convertDateToLocalDateTime(date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        return localDateTime.format(formatter);
    }

    public Date stringToLocalDateTimeConverter(String dateString) {
        if (dateString == null) {
            return null;
        }
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
        LocalDate localDate = LocalDate.parse(dateString, inputFormatter);
        LocalDateTime localDateTime = localDate.atStartOfDay();
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        String formattedDateTime = localDateTime.format(outputFormatter);
        LocalDateTime parsedDateTime = LocalDateTime.parse(formattedDateTime, outputFormatter);
        return Date.from(parsedDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


}
