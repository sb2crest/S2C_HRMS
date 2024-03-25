package com.employee.management.converters;

import com.employee.management.DTO.*;
import com.employee.management.exception.CompanyException;
import com.employee.management.exception.ResCodes;
import com.employee.management.models.*;
import com.employee.management.util.CtcCalculator;
import com.employee.management.util.Formatters;
import com.employee.management.util.PasswordGenerator;
import com.employee.management.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

@Component
public class Mapper {
    @Autowired
    PasswordGenerator passwordGenerator;
    @Autowired
    DateTimeConverter dateConverter;
    @Autowired
    CtcCalculator calculator;
    @Autowired
    Util util;
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
            employeeDTO.setPfNumber(employee.getPfNumber());
            employeeDTO.setDepartment(employee.getDepartment());
            employeeDTO.setUanNumber(employee.getUanNumber());
            employeeDTO.setDateOfJoin(dateConverter.localDateTimeToStringConverter(employee.getDateOfJoin()));
            employeeDTO.setGrossSalary(Formatters.formatAmountWithCommas(employee.getGrossSalary()));
            employeeDTO.setNextHikeDate(dateConverter.localDateTimeToStringConverter(employee.getNextHikeDate()));
        }
        return employeeDTO;
    }
    public PayrollDTO convertToPayRollDTO(Payroll payroll) {
        PayrollDTO dto = new PayrollDTO();
        dto.setId(payroll.getId());
        dto.setPayPeriod(payroll.getPayPeriod());
        dto.setPayDate(dateConverter.localDateTimeToStringConverter(payroll.getPayDate()));
        dto.setEmployeeId(payroll.getEmployee().getEmployeeID());
        dto.setBasic(Formatters.formatAmountWithCommas(payroll.getBasic()));
        dto.setHouseRentAllowance(Formatters.formatAmountWithCommas(payroll.getHouseRentAllowance()));
        dto.setMedicalAllowance(Formatters.formatAmountWithCommas(payroll.getMedicalAllowance()));
        dto.setOtherAllowance(Formatters.formatAmountWithCommas(payroll.getOtherAllowance()));
        dto.setGrossEarnings(Formatters.formatAmountWithCommas(payroll.getGrossEarnings()));
        dto.setProvidentFund(Formatters.formatAmountWithCommas(payroll.getProvidentFund()));
        dto.setTotalDeductions(Formatters.formatAmountWithCommas(payroll.getTotalDeductions()));
        dto.setTotalNetPayable(Formatters.formatAmountWithCommas((double) Math.round(payroll.getTotalNetPayable())));
        dto.setProfessionalTax(Formatters.formatAmountWithCommas(payroll.getProfessionalTax()));
        dto.setTotalDaysPaid(payroll.getTotalPaidDays());
        dto.setIncomeTax(Formatters.formatAmountWithCommas(payroll.getIncomeTax()));
        dto.setTotalLopDays(payroll.getTotalLopDays());
        dto.setLeaveDeduction(Formatters.formatAmountWithCommas(payroll.getLeaveDeduction()));
        return dto;
    }

    public Payroll convertToPayroll(PayrollDTO payrollDTO){
        Payroll payroll=new Payroll();
        if(payrollDTO !=null) {
            payroll.setPayDate(dateConverter.stringToLocalDateTimeConverter(payrollDTO.getPayDate()));
            payroll.setPayPeriod(payrollDTO.getPayPeriod());
            payroll.setBasic(Formatters.convertStringToDoubleAmount(payrollDTO.getBasic()));
            payroll.setHouseRentAllowance(Formatters.convertStringToDoubleAmount(payrollDTO.getHouseRentAllowance()));
            payroll.setMedicalAllowance(Formatters.convertStringToDoubleAmount(payrollDTO.getMedicalAllowance()));
            payroll.setOtherAllowance(Formatters.convertStringToDoubleAmount(payrollDTO.getOtherAllowance()));
            payroll.setGrossEarnings(Formatters.convertStringToDoubleAmount(payrollDTO.getGrossEarnings()));
            payroll.setLeaveDeduction(Formatters.convertStringToDoubleAmount(payrollDTO.getLeaveDeduction()));
            payroll.setProfessionalTax(Formatters.convertStringToDoubleAmount(payrollDTO.getProfessionalTax()));
            payroll.setProvidentFund(Formatters.convertStringToDoubleAmount(payrollDTO.getProvidentFund()));
            payroll.setTotalDeductions(Formatters.convertStringToDoubleAmount(payrollDTO.getTotalDeductions()));
            payroll.setTotalNetPayable(Formatters.convertStringToDoubleAmount(payrollDTO.getTotalNetPayable()));
            payroll.setTotalPaidDays(payrollDTO.getTotalDaysPaid());
            payroll.setTotalLopDays(payrollDTO.getTotalLopDays());
            payroll.setIncomeTax(Formatters.convertStringToDoubleAmount(payrollDTO.getIncomeTax()));
            return payroll;
        }
        return payroll;
    }
    public Payroll mapCtcDataToPayroll(AddMonthlyPayRollRequest request,Employee employee){
        CtcData ctcData=calculator.compensationDetails(employee.getGrossSalary());
        if(ctcData!=null){
            Payroll payroll=new Payroll();
            payroll.setEmployee(employee);
            payroll.setTotalLopDays(request.getLopDays() != null?Integer.parseInt(request.getLopDays()):0);
            payroll.setPayDate(dateConverter.stringToLocalDateTimeConverter(request.getPayDate()));
            payroll.setPayPeriod(request.getPayPeriod());
            payroll.setBasic(Formatters.convertStringToDoubleAmount(ctcData.getMonthlyBasic()));
            payroll.setHouseRentAllowance(Formatters.convertStringToDoubleAmount(ctcData.getMonthlyHRA()));
            payroll.setMedicalAllowance(Formatters.convertStringToDoubleAmount(ctcData.getMonthlyMedAllowance()));
            payroll.setOtherAllowance(Formatters.convertStringToDoubleAmount(ctcData.getMonthlyOtherAllowance()));
            payroll.setGrossEarnings(Formatters.convertStringToDoubleAmount(ctcData.getMonthlyGrossCtc()));

            payroll.setIncomeTax(Formatters.convertStringToDoubleAmount(ctcData.getMonthlyIncomeTax()));
            payroll.setProfessionalTax(Formatters.convertStringToDoubleAmount(ctcData.getMonthlyProfessionalTax()));
            payroll.setProvidentFund(Formatters.convertStringToDoubleAmount(ctcData.getMonthlyProvidentFund()));
            double regularNetPayable= payroll.getGrossEarnings()-(payroll.getIncomeTax()+payroll.getProfessionalTax()+payroll.getProvidentFund());
            long leaveDeductions=Math.round (regularNetPayable/30)*payroll.getTotalLopDays();
            payroll.setLeaveDeduction((double) leaveDeductions);
            payroll.setTotalDeductions(Formatters.convertStringToDoubleAmount(ctcData.getMonthlyTotalDeduction())+payroll.getLeaveDeduction());
            payroll.setTotalNetPayable(Formatters.convertStringToDoubleAmount(ctcData.getMonthlyGrossCtc())-payroll.getTotalDeductions());
            payroll.setTotalPaidDays(util.getNumberOfDaysInMonth(payroll.getPayPeriod())-payroll.getTotalLopDays());
            return payroll;
        }
        return null;
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
        employee.setDepartment(employeeDTO.getDepartment());
//        employee.setPassword(passwordGenerator.generatePassword(6));
        employee.setPassword("HollyMolly");
        employee.setEmail(employeeDTO.getEmail());
        employee.setDateOfJoin(dateConverter.stringToLocalDateTimeConverter(employeeDTO.getDateOfJoin()));

        Date nextHikeDate = new Date(employee.getDateOfJoin().getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTime(nextHikeDate);
        cal.add(Calendar.YEAR, 1);
        nextHikeDate = cal.getTime();
        employee.setNextHikeDate(nextHikeDate);

        employee.setGrossSalary(Formatters.convertStringToDoubleAmount(employeeDTO.getGrossSalary()));
        employee.setPfNumber(employeeDTO.getPfNumber());
        employee.setUanNumber(employeeDTO.getUanNumber());
        return employee;
    }

    public OfferLetterEntity convertToOfferLetterEntity(OfferLetterDTO offerLetterDTO){
        OfferLetterEntity offerLetter=new OfferLetterEntity();
        if(offerLetterDTO!=null){
            offerLetter.setCtc(Formatters.convertStringToDoubleAmount(offerLetterDTO.getCtc()));
            offerLetter.setFullName(offerLetterDTO.getFullName());
            offerLetter.setEmail(offerLetterDTO.getEmail());
            offerLetter.setJoiningDate(dateConverter
                    .stringToLocalDateTimeConverter(offerLetterDTO.getJoiningDate()));
            offerLetter.setIssuedDate(dateConverter.stringToLocalDateTimeConverter(offerLetterDTO.getIssuedDate()));
            offerLetter.setPhoneNumber(offerLetterDTO.getPhoneNumber());
            offerLetter.setDesignation(offerLetterDTO.getDesignation());
            offerLetter.setDepartment(offerLetterDTO.getDepartment());
            return offerLetter;
        }
        throw new RuntimeException("OfferLetter DTO is null");
    }

    public OfferLetterDTO convertToOfferLetterDto(OfferLetterEntity entity){
        OfferLetterDTO offerLetterDTO=new OfferLetterDTO();
        offerLetterDTO.setCtc(Formatters.formatAmountWithCommas(entity.getCtc()));
        offerLetterDTO.setIssuedDate(dateConverter.localDateTimeToStringConverter(entity.getIssuedDate()));
        offerLetterDTO.setJoiningDate(dateConverter.localDateTimeToStringConverter(entity.getJoiningDate()));
        offerLetterDTO.setFullName(entity.getFullName());
        offerLetterDTO.setEmail(entity.getEmail());
        offerLetterDTO.setDesignation(entity.getDesignation());
        offerLetterDTO.setJoiningDate(dateConverter.localDateTimeToStringConverter(entity.getJoiningDate()));
        offerLetterDTO.setPhoneNumber(entity.getPhoneNumber());
        offerLetterDTO.setDepartment(entity.getDepartment());
        return offerLetterDTO;
    }

    public HikeEntityDTO convertToHikeEntityDto(HikeEntity hike){
        HikeEntityDTO hikeEntityDTO=new HikeEntityDTO();
        hikeEntityDTO.setId(hike.getId());
        hikeEntityDTO.setHikePercentage(String.valueOf(hike.getHikePercentage()));
        hikeEntityDTO.setEffectiveDate(dateConverter.localDateTimeToStringConverter(hike.getEffectiveDate()));
        hikeEntityDTO.setEmployeeId(hike.getEmployee().getEmployeeID());
        hikeEntityDTO.setReason(hike.getReason());
        hikeEntityDTO.setApprovedDate(dateConverter.localDateTimeToStringConverter(hike.getApprovedDate()));
        hikeEntityDTO.setNewSalary(Formatters.formatAmountWithCommas(hike.getNewSalary()));
        hikeEntityDTO.setPrevSalary(Formatters.formatAmountWithCommas(hike.getPrevSalary()));
        hikeEntityDTO.setStatus(hike.getIsApproved());
        hikeEntityDTO.setNewPosition(hike.getNewPosition());
        hikeEntityDTO.setPrevPosition(hike.getPrevPosition());
        if(hike.getApprovedBy() !=null)
            hikeEntityDTO.setApprovedBy(hike.getApprovedBy().getEmployeeID());
        return hikeEntityDTO;
    }

    public CalendarDTO getCalendarDTO(CalendarEntity calendarEntity){
        CalendarDTO calendarDTO=new CalendarDTO();
        calendarDTO.setDate(dateConverter.localDateTimeToStringConverter(calendarEntity.getDate()));
        calendarDTO.setEvent(calendarEntity.getEvent());
        calendarDTO.setPeriod(formatMonthYear(calendarEntity.getDate()));
        return calendarDTO;
    }
    public CalendarEntity getCalendarEntity(CalendarDTO calendarDTO){
        CalendarEntity calendarEntity=new CalendarEntity();
        calendarEntity.setDate(dateConverter.stringToLocalDateTimeConverter(calendarDTO.getDate()));
        calendarEntity.setEvent(calendarDTO.getEvent());
        calendarEntity.setPeriod(formatMonthYear(calendarEntity.getDate()));
        return calendarEntity;
    }

    private boolean validateEmployeeDto(EmployeeDTO employeeDTO) {
        return Stream.of(employeeDTO.getEmployeeName(), employeeDTO.getDesignation(),
                        employeeDTO.getLocation(), employeeDTO.getBankName(),
                        employeeDTO.getAccountNo())
                .allMatch(field -> field != null && !field.isEmpty());
    }

    public String formatMonthYear(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
        return dateFormat.format(date);
    }

}
