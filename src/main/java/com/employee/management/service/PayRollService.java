package com.employee.management.service;

import com.employee.management.DTO.AddMonthlyPayRollRequest;
import com.employee.management.DTO.CtcData;
import com.employee.management.DTO.PaySlip;
import com.employee.management.models.Payroll;

public interface PayRollService {
    PaySlip getPaySlip(String empId, String payPeriod);

    CtcData getPayrollDetails(String empId);

    CtcData getPayrollDetailsWithLeaveDeduction(AddMonthlyPayRollRequest request);
}
