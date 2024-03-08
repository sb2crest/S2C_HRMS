package com.employee.management.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "calendar")
public class CalendarEntity {
    @Id
    @GeneratedValue(strategy =
            GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date date;
    @Column(name = "period")
    private String period;
    @Column(name = "event")
    private String event;
}
