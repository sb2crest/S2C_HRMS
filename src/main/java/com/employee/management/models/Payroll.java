package com.employee.management.models;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Entity
@Table(name = "payroll")
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "pay_period")
    private String payPeriod;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "pay_date")
    private Date payDate;

    @ManyToOne
    @JoinColumn(name = "EmployeeID", referencedColumnName = "employeeID")
    private Employee employee;

    @Column(name = "Basic")
    private Double basic;

    @Column(name = "house_rent_allowance")
    private Double houseRentAllowance;

    @Column(name = "medical_allowance")
    private Double medicalAllowance;

    @Column(name = "other_allowance")
    private Double otherAllowance;

    @Column(name = "gross_earnings")
    private Double grossEarnings;

    @Column(name = "provident_fund")
    private Double providentFund;

    @Column(name="leave_deduction")
    private Double leaveDeduction;

    @Column(name="professional_tax")
    private Double professionalTax;

    @Column(name="income_tax")
    private Double incomeTax;

    @Column(name = "total_deductions")
    private Double totalDeductions;

    @Column(name = "total_net_payable")
    private Double totalNetPayable;

    @Column(name="total_paid_days")
    private Integer totalPaidDays;

    @Column(name="total_lop_days")
    private Integer totalLopDays;
}

