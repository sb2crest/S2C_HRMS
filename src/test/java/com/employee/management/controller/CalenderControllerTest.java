package com.employee.management.controller;

import com.employee.management.DTO.CalendarDTO;
import com.employee.management.filter.JWTAuthFilter;
import com.employee.management.service.CalendarService;
import com.employee.management.service.JWTService;
import com.employee.management.service.impl.CalendarServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CalenderController.class)
class CalenderControllerTest {
    @InjectMocks
    CalenderController calenderController;
    @MockBean
    CalendarService calenderService;
    @MockBean
    JWTAuthFilter jwtauthFilter;
    @MockBean
    JWTService jwtService;
    @Autowired
    WebApplicationContext context;
    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc= MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void testAddEvent() throws Exception {
        CalendarDTO calendarDTO = new CalendarDTO();
        calendarDTO.setEvent("Meeting");
        calendarDTO.setDate("12-Feb-2024");
        when(calenderService.addEvent(any())).thenReturn(calendarDTO);
        mockMvc.perform(post("/calendar/add-event")
                     .contentType(MediaType.APPLICATION_JSON)
                     .content(new ObjectMapper().writeValueAsString(calendarDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void TestGetEventsByMonth() throws Exception {
        List<CalendarDTO> events=new ArrayList<>();
        CalendarDTO dto1=new CalendarDTO();
        dto1.setEvent("Meeting");
        dto1.setDate("12-Feb-2024");
        CalendarDTO dto2=new CalendarDTO();
        dto2.setEvent("Meeting");
        dto2.setDate("13-Feb-2024");
        String month="February 2024";
        events.add(dto1);
        events.add(dto2);
        when(calenderService.getEventsByMonth(anyString())).thenReturn(events);
        mockMvc.perform(get("/calendar/events-by-month")
                        .param("month",month)
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(new ObjectMapper().writeValueAsString(events)))
                .andExpect(status().isOk());

    }

    @Test
    void testGetEvents() throws Exception {
        List<CalendarDTO> events=new ArrayList<>();
        CalendarDTO dto1=new CalendarDTO();
        dto1.setEvent("Meeting");
        dto1.setDate("12-Feb-2024");
        CalendarDTO dto2=new CalendarDTO();
        dto2.setEvent("Meeting");
        dto2.setDate("13-Feb-2024");
        events.add(dto1);
        events.add(dto2);
        when(calenderService.getEvents()).thenReturn(events);
        mockMvc.perform(get("/calendar/events")
                     .contentType(MediaType.APPLICATION_JSON)
                     .content(new ObjectMapper().writeValueAsString(events)))
                .andExpect(status().isOk());
    }
}