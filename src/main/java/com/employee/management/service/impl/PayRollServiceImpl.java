package com.employee.management.service.impl;

import com.employee.management.DTO.AddMonthlyPayRollRequest;
import com.employee.management.DTO.CtcData;
import com.employee.management.DTO.PaySlip;
import com.employee.management.converters.Mapper;
import com.employee.management.exception.CompanyException;
import com.employee.management.exception.ResCodes;
import com.employee.management.models.Employee;
import com.employee.management.models.Payroll;
import com.employee.management.repository.EmployeeRepository;
import com.employee.management.repository.PayrollRepository;
import com.employee.management.service.PayRollService;
import com.employee.management.util.CtcCalculator;
import com.employee.management.util.Formatters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayRollServiceImpl implements PayRollService {
    @Autowired
    PayrollRepository payrollRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    Mapper mapper;
    @Autowired
    Formatters formatters;

    @Override
    public PaySlip getPaySlip(String empId, String payPeriod) {
        Employee employee = employeeRepository.findById(empId).orElseThrow(() -> new CompanyException(ResCodes.EMPLOYEE_NOT_FOUND));
        Payroll payroll = payrollRepository.getPayPeriodDetails(payPeriod, employee).orElseThrow(() -> new CompanyException(ResCodes.SALARY_DETAILS_NOT_FOUND));
        PaySlip paySlip = new PaySlip();
        paySlip.setEmployeeDTO(mapper.convertToEmployeeDTO(employee));
        paySlip.setPayrollDTO(mapper.convertToPayRollDTO(payroll));
        return paySlip;
    }

    @Override
    public CtcData getPayrollDetails(String empId) {
        Employee employee = employeeRepository.findById(empId).orElseThrow(() -> new CompanyException(ResCodes.EMPLOYEE_NOT_FOUND));
        return new CtcCalculator().compensationDetails(employee.getGrossSalary());
    }

    @Override
    public CtcData getPayrollDetailsWithLeaveDeduction(AddMonthlyPayRollRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId()).orElseThrow(() -> new CompanyException(ResCodes.EMPLOYEE_NOT_FOUND));
        CtcData ctcData = new CtcCalculator().compensationDetails(employee.getGrossSalary());
        int totalLeaves= Integer.parseInt(request.getLopDays());
        double payPerDay = (employee.getGrossSalary()/12)/30;
        Double monthlyLeaveDeduction=(payPerDay*totalLeaves);
        Double yearlyLeaveDeduction=monthlyLeaveDeduction*12;
        ctcData.setMonthlyLeaveDeduction(Formatters.formatAmountWithCommas(monthlyLeaveDeduction));
        ctcData.setYearlyLeaveDeduction(Formatters.formatAmountWithCommas(yearlyLeaveDeduction));
        double monthlyTotalDeduction = Formatters.convertStringToDoubleAmount(ctcData.getMonthlyTotalDeduction())+monthlyLeaveDeduction;
        Double yearlyTotalDeduction = monthlyTotalDeduction*12;
        ctcData.setMonthlyTotalDeduction(Formatters.formatAmountWithCommas(monthlyTotalDeduction));
        ctcData.setYearlyTotalDeduction(Formatters.formatAmountWithCommas(yearlyTotalDeduction));
        double monthlyNetPay=Formatters.convertStringToDoubleAmount(ctcData.getMonthlyNetPayable())-monthlyLeaveDeduction;
        Double yearlyNetPay=monthlyNetPay*12;
        ctcData.setMonthlyNetPayable(Formatters.formatAmountWithCommas(monthlyNetPay));
        ctcData.setYearlyNetPayable(Formatters.formatAmountWithCommas(yearlyNetPay));
        return ctcData;
    }

}
