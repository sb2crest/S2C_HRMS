package com.employee.management.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "com.employee.management.util.CustomIdGenerator")
    @Column(name = "employeeId")
    private String employeeID;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "designation")
    private String designation;

    @Column(name="department")
    private String department;

    @Column(name = "location")
    private String location;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_no")
    private String accountNo;

    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(
            name = "employee_role",
            joinColumns = @JoinColumn(name = "employeeId"),
            inverseJoinColumns = @JoinColumn(name = "roleId")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "statusId", referencedColumnName = "statusId")
    private Status status;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_of_join")
    private Date dateOfJoin;

    @Column(name = "password")
    private String password;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "uan_number")
    private String uanNumber;

    @Column(name = "pf_number")
    private String pfNumber;

    @Column(name = "gross_salary")
    private Double grossSalary;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="next-hike-date")
    private Date nextHikeDate;

    @Override
    public String toString() {
        return "Employee{" +
                "employeeID='" + employeeID + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", designation='" + designation + '\'' +
                ", location='" + location + '\'' +
                ", bankName='" + bankName + '\'' +
                ", accountNo='" + accountNo + '\'' +
                ", status=" + status +
                ", dateOfJoin=" + dateOfJoin +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", uanNumber='" + uanNumber + '\'' +
                ", pfNumber='" + pfNumber + '\'' +
                ", grossSalary=" + grossSalary +
                ", nextHikeDate=" + nextHikeDate +
                '}';
    }
}

