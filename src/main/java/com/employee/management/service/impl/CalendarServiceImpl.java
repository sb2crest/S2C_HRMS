package com.employee.management.service.impl;

import com.employee.management.DTO.CalendarDTO;
import com.employee.management.converters.DateTimeConverter;
import com.employee.management.converters.Mapper;
import com.employee.management.models.CalendarEntity;
import com.employee.management.repository.CalendarRepository;
import com.employee.management.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Service
public class CalendarServiceImpl implements CalendarService {
    @Autowired
    CalendarRepository calendarRepository;
    @Autowired
    Mapper mapper;
    @Autowired
    DateTimeConverter dateTimeConverter;

    @Override
    public CalendarDTO addEvent(CalendarDTO calendarDTO) throws ParseException {
        CalendarEntity calendarEntity=new CalendarEntity();
        calendarEntity.setDate(dateTimeConverter.stringToLocalDateTimeConverter(calendarDTO.getDate()));
        calendarEntity.setEvent(calendarDTO.getEvent());
        calendarEntity.setPeriod(formatMonthYear(calendarEntity.getDate()));
        calendarRepository.save(calendarEntity);
        return calendarDTO;
    }

    @Override
    public List<CalendarDTO> getEvents(){
        List<CalendarEntity> calendarEntities=calendarRepository.findAll();

        return calendarEntities
                .stream()
                .map(calendarEntity->
                        new CalendarDTO(dateTimeConverter.localDateTimeToStringConverter(calendarEntity.getDate()),calendarEntity.getPeriod(),calendarEntity.getEvent())).toList();
    }
    @Override
    public List<CalendarDTO> getEventsByMonth(String month){
        List<CalendarEntity> calendarEntities=calendarRepository.findAll();

        return calendarEntities
                .stream()
                .filter(calendarEntity -> calendarEntity.getPeriod().equals(month))
                .map(calendarEntity->
                        new CalendarDTO(dateTimeConverter.localDateTimeToStringConverter(calendarEntity.getDate()),calendarEntity.getPeriod(),calendarEntity.getEvent())).toList();
    }


    public String formatMonthYear(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
        return dateFormat.format(date);
    }

}
