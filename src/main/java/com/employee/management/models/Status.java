package com.employee.management.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "status")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statusId")
    private Long statusID;

    @Column(name = "name")
    private String name;
}
