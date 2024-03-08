package com.employee.management.models;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
@Data
@Entity
@Table(name = "Leaves")
public class Leaves {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "LeaveType")
    private String leaveType;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "LeaveStartDate")
    private Date leaveStartDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "LeaveEndDate")
    private Date leaveEndDate;

    @Column(name = "Status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "EmployeeID", referencedColumnName = "employeeID")
    private Employee employee;

}


