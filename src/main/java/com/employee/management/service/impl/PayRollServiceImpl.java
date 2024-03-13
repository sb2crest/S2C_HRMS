package com.employee.management.service.impl;

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
    public PaySlip getPaySlip(String empId, String payPeriod){
        Employee employee=employeeRepository.findById(empId).orElseThrow(()-> new CompanyException(ResCodes.EMPLOYEE_NOT_FOUND));
        Payroll payroll=payrollRepository.getPayPeriodDetails(payPeriod,employee).orElseThrow(()->new CompanyException(ResCodes.SALARY_DETAILS_NOT_FOUND));
        PaySlip paySlip=new PaySlip();
        paySlip.setEmployeeDTO(mapper.convertToEmployeeDTO(employee));
        paySlip.setPayrollDTO(mapper.convertToPayRollDTO(payroll));
        return paySlip;
    }

    @Override
    public CtcData getPayrollDetails(String empId) {
        Employee employee=employeeRepository.findById(empId).orElseThrow(()-> new CompanyException(ResCodes.EMPLOYEE_NOT_FOUND));
        return new CtcCalculator().compensationDetails(employee.getGrossSalary());
    }

}
