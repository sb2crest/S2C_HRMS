package com.employee.management.service;

import com.employee.management.DTO.CalendarDTO;

import java.text.ParseException;
import java.util.List;

public interface CalendarService {
    CalendarDTO addEvent(CalendarDTO calendarDTO) throws ParseException;

    List<CalendarDTO> getEvents();

    List<CalendarDTO> getEventsByMonth(String month);
}
