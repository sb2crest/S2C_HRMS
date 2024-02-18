package com.employee.management.converters;

import com.employee.management.DTO.EmployeeDTO;
import com.employee.management.DTO.PayrollDTO;
import com.employee.management.exception.CompanyException;
import com.employee.management.exception.ResCodes;
import com.employee.management.models.Employee;
import com.employee.management.models.Payroll;
import com.employee.management.models.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Arrays;

@Component
public class Mapper {
    @Autowired
    DateTimeConverter dateConverter;
    public EmployeeDTO convertToEmployeeDTO(Employee employee){
        EmployeeDTO employeeDTO=new EmployeeDTO();
        if(employee!=null){
            employeeDTO.setRoles(employee.getRoles()
                    .stream()
                    .map(Role::getName)
                    .toList()
            );
            employeeDTO.setEmployeeID(employee.getEmployeeID());
            employeeDTO.setEmployeeName(employee.getEmployeeName());
            employeeDTO.setDesignation(employee.getDesignation());
            employeeDTO.setEmail(employee.getEmail());
            employeeDTO.setLocation(employee.getLocation());
            employeeDTO.setBankName(employee.getBankName());
            employeeDTO.setAccountNo(employee.getAccountNo());
            employeeDTO.setStatus(employee.getStatus().getName());
            employeeDTO.setDateOfJoin(dateConverter.localDateTimeToStringConverter(employee.getDateOfJoin()));
        }
        return employeeDTO;
    }
    public PayrollDTO convertToPayRollDTO(Payroll payroll) {
        PayrollDTO dto = new PayrollDTO();
        dto.setId(payroll.getId());
        dto.setPayPeriod(payroll.getPayPeriod());
        dto.setPayDate(dateConverter.localDateTimeToStringConverter(payroll.getPayDate()));
        dto.setEmployeeId(payroll.getEmployee().getEmployeeID());
        dto.setBasic(formatAmountWithCommas(payroll.getBasic()));
        dto.setHouseRentAllowance(formatAmountWithCommas(payroll.getHouseRentAllowance()));
        dto.setMedicalAllowance(formatAmountWithCommas(payroll.getMedicalAllowance()));
        dto.setOtherAllowance(formatAmountWithCommas(payroll.getOtherAllowance()));
        dto.setGrossEarnings(formatAmountWithCommas(payroll.getGrossEarnings()));
        dto.setProvidentFund(formatAmountWithCommas(payroll.getProvidentFund()));
        dto.setTotalDeductions(formatAmountWithCommas(payroll.getTotalDeductions()));
        dto.setTotalNetPayable(formatAmountWithCommas((double) Math.round(payroll.getTotalNetPayable())));
        dto.setTotalDaysPaid(payroll.getTotalPaidDays());
        dto.setTotalLopDays(payroll.getTotalLopDays());
        dto.setLeaveDeduction(formatAmountWithCommas(payroll.getLeaveDeduction()));
        return dto;
    }

    public Payroll convertToPayroll(PayrollDTO payrollDTO){
        Payroll payroll=new Payroll();
        payroll.setPayDate(dateConverter.stringToLocalDateTimeConverter(payrollDTO.getPayDate()));
        payroll.setPayPeriod(payrollDTO.getPayPeriod());
        payroll.setBasic(convertStringToDoubleAmount(payrollDTO.getBasic()));
        payroll.setHouseRentAllowance(convertStringToDoubleAmount(payrollDTO.getHouseRentAllowance()));
        payroll.setMedicalAllowance(convertStringToDoubleAmount(payrollDTO.getMedicalAllowance()));
        payroll.setOtherAllowance(convertStringToDoubleAmount(payrollDTO.getOtherAllowance()));
        payroll.setGrossEarnings(convertStringToDoubleAmount(payrollDTO.getGrossEarnings()));
        payroll.setLeaveDeduction(convertStringToDoubleAmount(payrollDTO.getLeaveDeduction()));
        payroll.setProfessionalTax(convertStringToDoubleAmount(payrollDTO.getProfessionalTax()));
        payroll.setProvidentFund(convertStringToDoubleAmount(payrollDTO.getProvidentFund()));
        payroll.setTotalDeductions(convertStringToDoubleAmount(payrollDTO.getTotalDeductions()));
        payroll.setTotalNetPayable(convertStringToDoubleAmount(payrollDTO.getTotalNetPayable()));
        payroll.setTotalPaidDays(payrollDTO.getTotalDaysPaid());
        payroll.setTotalLopDays(payrollDTO.getTotalLopDays());
        return payroll;
    }
    public Employee convertToEmployeeEntity(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        if(!validateEmployeeDto(employeeDTO)){
            throw new CompanyException(ResCodes.INVALID_EMPLOYEE_DETAILS);
        }
        employee.setEmployeeName(employeeDTO.getEmployeeName());
        employee.setDesignation(employeeDTO.getDesignation());
        employee.setLocation(employeeDTO.getLocation());
        employee.setBankName(employeeDTO.getBankName());
        employee.setAccountNo(employeeDTO.getAccountNo());
        employee.setPassword(employeeDTO.getPassword());
        employee.setEmail(employeeDTO.getEmail());
        employee.setDateOfJoin(dateConverter.stringToLocalDateTimeConverter(employeeDTO.getDateOfJoin()));
        return employee;
    }
    private boolean validateEmployeeDto(EmployeeDTO employeeDTO) {
        return Arrays.asList(employeeDTO.getEmployeeName(), employeeDTO.getDesignation(),
                        employeeDTO.getLocation(), employeeDTO.getBankName(),
                        employeeDTO.getAccountNo())
                .stream()
                .allMatch(field -> field != null && !field.isEmpty());
    }
    public Double convertStringToDoubleAmount(String amount){
        amount=amount.replace(",","");
       return Double.parseDouble(amount);
    }
    private String formatAmountWithCommas(Double amount) {
        if (amount == null) {
            return "";
        }
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        return formatter.format(amount);
    }

}
