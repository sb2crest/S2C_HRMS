package com.employee.management.models;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
@Data
@Entity
@Table(name = "leaves")
public class Leaves {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "leave_type")
    private String leaveType;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "leave_start_date")
    private Date leaveStartDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "leave_end_date")
    private Date leaveEndDate;

    @Column(name = "Status")
    private String status;

    @Column(name = "Reason")
    private String reason;

    @ManyToOne
    @JoinColumn(name = "EmployeeID", referencedColumnName = "employeeID")
    private Employee employee;

}


