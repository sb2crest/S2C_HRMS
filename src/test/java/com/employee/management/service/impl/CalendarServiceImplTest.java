package com.employee.management.service.impl;

import com.employee.management.DTO.CalendarDTO;
import com.employee.management.converters.DateTimeConverter;
import com.employee.management.converters.Mapper;
import com.employee.management.models.CalendarEntity;
import com.employee.management.repository.CalendarRepository;
import com.employee.management.service.CalendarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarServiceImplTest {
    @InjectMocks
    private CalendarServiceImpl calendarService;
    @Mock
    private CalendarRepository calendarRepository;
    @Mock
    Mapper mapper;
    @MockBean
    DateTimeConverter dateTimeConverter;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddEvent() throws ParseException {
        CalendarDTO calendarDTO = getCalendarDTO();

        CalendarEntity calendarEntity = getCalendarEntity();
        when((mapper.getCalendarEntity(calendarDTO))).thenReturn(calendarEntity);
        when((calendarRepository.save(calendarEntity))).thenReturn(calendarEntity);
        CalendarDTO res= calendarService.addEvent(calendarDTO);

        assertNotNull(res);
        assertEquals(calendarDTO,res);
    }
    private CalendarDTO getCalendarDTO(){
        CalendarDTO calendarDTO = new CalendarDTO();
        calendarDTO.setDate("20-Feb-2005");
        calendarDTO.setEvent("Meeting");
        return calendarDTO;
    }
    private CalendarEntity getCalendarEntity(){
        CalendarEntity calendarEntity = new CalendarEntity();
        calendarEntity.setDate(new Date());
        calendarEntity.setEvent("Meeting");
        calendarEntity.setPeriod("February 2005");
        return calendarEntity;
    }


    @Test
    void testGetEvents() {
        List<CalendarEntity> calendarEntityList =List.of(getCalendarEntity(),getCalendarEntity());
        List<CalendarDTO> calendarDTOList =List.of(getCalendarDTO(),getCalendarDTO());

        when((calendarRepository.findAll())).thenReturn(calendarEntityList);
        when(mapper.getCalendarDTO(any())).thenReturn(getCalendarDTO());

        List<CalendarDTO> result = calendarService.getEvents();

        assertNotNull(result);
        assertEquals(calendarDTOList,result);
    }

    @Test
    void testGetEventsByMonth() {
        List<CalendarEntity> calendarEntityList =List.of(getCalendarEntity(),getCalendarEntity());
        List<CalendarDTO> calendarDTOList =List.of(getCalendarDTO(),getCalendarDTO());
        String month = "February 2005";

        when((calendarRepository.findAll())).thenReturn(calendarEntityList);
        when(mapper.getCalendarDTO(any())).thenReturn(getCalendarDTO());

        List<CalendarDTO> result = calendarService.getEventsByMonth(month);

        assertNotNull(result);
        assertEquals(calendarDTOList,result);
    }

}