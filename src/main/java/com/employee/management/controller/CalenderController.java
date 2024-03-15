package com.employee.management.controller;

import com.employee.management.DTO.CalendarDTO;
import com.employee.management.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/calendar")
@CrossOrigin(origins = "http://localhost:3000")
public class CalenderController {
    @Autowired
    private CalendarService calendarService;

    @PostMapping("/add-event")
    public ResponseEntity<CalendarDTO> addEvent(@RequestBody CalendarDTO calendarDTO) throws ParseException {
        return new ResponseEntity<>(calendarService.addEvent(calendarDTO), HttpStatus.CREATED);
    }

    @GetMapping("/events-by-month")
    public ResponseEntity<List<CalendarDTO>> getEventsByMonth(@RequestParam String month){
        return new ResponseEntity<>(calendarService.getEventsByMonth(month), HttpStatus.OK);
    }

    @GetMapping("/events")
    public ResponseEntity<List<CalendarDTO>> getEvents(){
        return new ResponseEntity<>(calendarService.getEvents(), HttpStatus.OK);
    }
}
