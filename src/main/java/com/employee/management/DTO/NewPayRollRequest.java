package com.employee.management.DTO;

import lombok.Data;

@Data
public class NewPayRollRequest {
    private String employeeId;
    private String payPeriod;
    private String payDate;
    private String totalLopDays;
}
