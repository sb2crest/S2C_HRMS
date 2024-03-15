package com.employee.management.converters;

import static org.junit.jupiter.api.Assertions.*;

import com.employee.management.DTO.*;
import com.employee.management.exception.CompanyException;
import com.employee.management.models.*;
import com.employee.management.util.CtcCalculator;
import com.employee.management.converters.DateTimeConverter;
import com.employee.management.util.Formatters;
import com.employee.management.util.PasswordGenerator;
import com.employee.management.util.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class MapperTest {

    @Mock
    private PasswordGenerator passwordGenerator;

    @Mock
    private DateTimeConverter dateConverter;

    @Mock
    private Formatters formatter;

    @Mock
    private CtcCalculator calculator;

    @Mock
    private Util util;

    @InjectMocks
    private Mapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConvertToEmployeeDTO() {
        Status status = new Status();
        status.setStatusID(1L);
        status.setName("active");
        // Given
        Employee employee = new Employee();
        employee.setEmployeeID("123");
        employee.setEmployeeName("John Doe");
        employee.setDesignation("Developer");
        employee.setEmail("john.doe@example.com");
        employee.setLocation("New York");
        employee.setBankName("Bank of America");
        employee.setAccountNo("123456789");
        employee.setStatus(status);
        employee.setPfNumber("PF123");
        employee.setDepartment("IT");
        employee.setUanNumber("UAN123");
        employee.setDateOfJoin(new Date());
        employee.setGrossSalary(50000.0);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        Date nextMonthDate = calendar.getTime();

        employee.setNextHikeDate(nextMonthDate);

        when(dateConverter.localDateTimeToStringConverter(employee.getDateOfJoin())).thenReturn("2023-04-01");
        when(dateConverter.localDateTimeToStringConverter(employee.getNextHikeDate())).thenReturn("2023-05-01");

        // When
        EmployeeDTO employeeDTO = mapper.convertToEmployeeDTO(employee);

        // Then
        assertEquals("123", employeeDTO.getEmployeeID());
        assertEquals("John Doe", employeeDTO.getEmployeeName());
        assertEquals("Developer", employeeDTO.getDesignation());
        assertEquals("john.doe@example.com", employeeDTO.getEmail());
        assertEquals("New York", employeeDTO.getLocation());
        assertEquals("Bank of America", employeeDTO.getBankName());
        assertEquals("123456789", employeeDTO.getAccountNo());
        assertEquals("active", employeeDTO.getStatus());
        assertEquals("PF123", employeeDTO.getPfNumber());
        assertEquals("IT", employeeDTO.getDepartment());
        assertEquals("UAN123", employeeDTO.getUanNumber());
        assertEquals("2023-04-01", employeeDTO.getDateOfJoin());
        assertEquals("50,000.00", employeeDTO.getGrossSalary());
        assertEquals("2023-05-01", employeeDTO.getNextHikeDate());
    }

    @Test
    void testConvertToEmployeeDTOWhenEmployeeNull() {
        Employee employee=null;

        EmployeeDTO employeeDTO=mapper.convertToEmployeeDTO(employee);

        assertNotNull(employeeDTO);
    }

    @Test
    void testConvertToEmployeeEntity() {
        Status status = new Status();
        status.setStatusID(1L);
        status.setName("active");
        String password="passw123";
        // Given
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeID("123");
        employeeDTO.setEmployeeName("John Doe");
        employeeDTO.setDesignation("Developer");
        employeeDTO.setEmail("john.doe@example.com");
        employeeDTO.setLocation("New York");
        employeeDTO.setBankName("Bank of America");
        employeeDTO.setAccountNo("123456789");
        employeeDTO.setStatus("active");
        employeeDTO.setPfNumber("PF123");
        employeeDTO.setDepartment("IT");
        employeeDTO.setUanNumber("UAN123");
        employeeDTO.setDateOfJoin("23-Feb-2015");
        employeeDTO.setGrossSalary("1,50,000.00");

        when(passwordGenerator.generatePassword(8)).thenReturn(password);
        when(dateConverter.stringToLocalDateTimeConverter(anyString())).thenReturn(new Date());

        Employee employee = mapper.convertToEmployeeEntity(employeeDTO);
        employee.setStatus(status);

        assertEquals("John Doe", employee.getEmployeeName());
        assertEquals("Developer", employee.getDesignation());
        assertEquals("john.doe@example.com", employee.getEmail());
        assertEquals("New York", employee.getLocation());
        assertEquals("Bank of America", employee.getBankName());
        assertEquals("123456789", employee.getAccountNo());
        assertEquals("active", employee.getStatus().getName());
        assertEquals("PF123", employee.getPfNumber());
        assertEquals("IT", employee.getDepartment());
        assertEquals("UAN123", employee.getUanNumber());
        assertEquals(new Date(employee.getDateOfJoin().getTime()), employee.getDateOfJoin());
        assertEquals(150000.0, employee.getGrossSalary());
        Date nextHikeDate = new Date(employee.getDateOfJoin().getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nextHikeDate);
        calendar.add(Calendar.YEAR, 1);
        Date nextYearDate = calendar.getTime();
        assertEquals(nextYearDate, employee.getNextHikeDate());
    }
    @Test
    void testConvertToEmployeeEntity_whenEmpty() {
        Status status = new Status();
        status.setStatusID(1L);
        status.setName("active");
        String password="passw123";
        // Given
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeID("123");
        employeeDTO.setEmployeeName("");
        employeeDTO.setDesignation("");
        employeeDTO.setEmail("john.doe@example.com");
        employeeDTO.setLocation("");
        employeeDTO.setBankName("");
        employeeDTO.setAccountNo("");
        employeeDTO.setStatus("active");
        employeeDTO.setPfNumber("PF123");
        employeeDTO.setDepartment("IT");
        employeeDTO.setUanNumber("UAN123");
        employeeDTO.setDateOfJoin("23-Feb-2015");
        employeeDTO.setGrossSalary("1,50,000.00");

        assertThrows(CompanyException.class,()->mapper.convertToEmployeeEntity(employeeDTO));
    }
    @Test
    void testConvertToEmployeeEntity_Exception() {
        Status status = new Status();
        status.setStatusID(1L);
        status.setName("active");
        String password="passw123";
        // Given
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeID("123");


        employeeDTO.setEmail("john.doe@example.com");
        employeeDTO.setLocation("New York");
        employeeDTO.setBankName("Bank of America");

        employeeDTO.setStatus("active");
        employeeDTO.setPfNumber("PF123");
        employeeDTO.setDepartment("IT");
        employeeDTO.setUanNumber("UAN123");
        employeeDTO.setDateOfJoin("23-Feb-2015");
        employeeDTO.setGrossSalary("1,50,000.00");

         assertThrows(CompanyException.class,()->mapper.convertToEmployeeEntity(employeeDTO));

    }

    @Test
    void testConvertToPayrollDTO() {
        // Given
        Employee employee=new Employee();
        employee.setEmployeeID("123");

        Payroll payroll = new Payroll();
        payroll.setId(1L);
        payroll.setPayPeriod("April 2023");
        payroll.setPayDate(new Date());
        payroll.setEmployee(new Employee());
        payroll.setEmployee(employee);
        payroll.setBasic(50000.0);
        payroll.setHouseRentAllowance(10000.0);
        payroll.setMedicalAllowance(5000.0);
        payroll.setOtherAllowance(2000.0);
        payroll.setGrossEarnings(63000.0);
        payroll.setProvidentFund(5000.0);
        payroll.setTotalDeductions(12000.0);
        payroll.setTotalNetPayable(51000.0);
        payroll.setTotalPaidDays(22);
        payroll.setIncomeTax(10000.0);
        payroll.setTotalLopDays(2);
        payroll.setLeaveDeduction(4000.0);
        payroll.setProfessionalTax(1000.0);


        when(dateConverter.localDateTimeToStringConverter(payroll.getPayDate())).thenReturn("14-March-2024");
        // When
        PayrollDTO payrollDTO = mapper.convertToPayRollDTO(payroll);

        // Then
        assertEquals(1L, payrollDTO.getId());
        assertEquals("April 2023", payrollDTO.getPayPeriod());
        assertEquals("14-March-2024", payrollDTO.getPayDate());
        assertEquals("123", payrollDTO.getEmployeeId());
        assertEquals("50,000.00", payrollDTO.getBasic());
        assertEquals("10,000.00", payrollDTO.getHouseRentAllowance());
        assertEquals("5,000.00", payrollDTO.getMedicalAllowance());
        assertEquals("2,000.00", payrollDTO.getOtherAllowance());
        assertEquals("63,000.00", payrollDTO.getGrossEarnings());
        assertEquals("5,000.00", payrollDTO.getProvidentFund());
        assertEquals("12,000.00", payrollDTO.getTotalDeductions());
        assertEquals("51,000.00", payrollDTO.getTotalNetPayable());
        assertEquals(22, payrollDTO.getTotalDaysPaid());
        assertEquals("10,000.00", payrollDTO.getIncomeTax());
        assertEquals(2, payrollDTO.getTotalLopDays());
        assertEquals("4,000.00", payrollDTO.getLeaveDeduction());
        assertEquals("1,000.00", payrollDTO.getProfessionalTax());
    }
    @Test
    void testConvertToPayrollDTOWhenNull(){
        // Given
        PayrollDTO payrollDTO = null;

        // When
        Payroll payroll = mapper.convertToPayroll(payrollDTO);

        // Then
        assertNotNull(payroll);
    }

    @Test
    void testConvertToPayroll() {
        // Given
        PayrollDTO payrollDTO = new PayrollDTO();
        payrollDTO.setId(1L);
        payrollDTO.setPayPeriod("April 2023");
        payrollDTO.setPayDate("20-Feb-2024");
        payrollDTO.setEmployeeId("123");
        payrollDTO.setBasic("50,000.00");
        payrollDTO.setHouseRentAllowance("10,000.00");
        payrollDTO.setMedicalAllowance("5,000.00");
        payrollDTO.setOtherAllowance("2,000.00");
        payrollDTO.setGrossEarnings("63,000.00");
        payrollDTO.setProvidentFund("5,000.00");
        payrollDTO.setTotalDeductions("12,000.00");
        payrollDTO.setTotalNetPayable("51,000.00");
        payrollDTO.setTotalDaysPaid(22);
        payrollDTO.setIncomeTax("10,000.00");
        payrollDTO.setTotalLopDays(2);
        payrollDTO.setLeaveDeduction("4,000.00");
        payrollDTO.setProfessionalTax("1,000.00");
        payrollDTO.setEmployeeId("123");

        when(dateConverter.stringToLocalDateTimeConverter(payrollDTO.getPayDate())).thenReturn(new Date());

        // When
        Payroll payroll = mapper.convertToPayroll(payrollDTO);


        // Then
        assertEquals("April 2023", payroll.getPayPeriod());
        assertEquals((new Date(payroll.getPayDate().getTime())), payroll.getPayDate());
        assertEquals(50000.0, payroll.getBasic());
        assertEquals(10000.0, payroll.getHouseRentAllowance());
        assertEquals(5000.0, payroll.getMedicalAllowance());
        assertEquals(2000.0, payroll.getOtherAllowance());
        assertEquals(63000.0, payroll.getGrossEarnings());
        assertEquals(5000.0, payroll.getProvidentFund());
        assertEquals(12000.0, payroll.getTotalDeductions());
        assertEquals(51000.0, payroll.getTotalNetPayable());
        assertEquals(22, payroll.getTotalPaidDays());
        assertEquals(10000.0, payroll.getIncomeTax());
        assertEquals(2, payroll.getTotalLopDays());
        assertEquals(4000.0, payroll.getLeaveDeduction());
        assertEquals(1000.0, payroll.getProfessionalTax());
    }

    @Test
    void testMapCtcDataToPayroll() {
        // Given
        AddMonthlyPayRollRequest request = new AddMonthlyPayRollRequest();
        request.setLopDays("2");
        request.setPayDate("2023-04-01");
        request.setPayPeriod("April 2023");

        Employee employee = new Employee();
        employee.setGrossSalary(60000.0);

        CtcData ctcData = new CtcData();
        ctcData.setMonthlyBasic("30,000.00");
        ctcData.setMonthlyHRA("10,000.00");
        ctcData.setMonthlyMedAllowance("5,000.00");
        ctcData.setMonthlyOtherAllowance("2,000.00");
        ctcData.setMonthlyGrossCtc("63,000.00");
        ctcData.setMonthlyIncomeTax("10,000.00");
        ctcData.setMonthlyProfessionalTax("1,000.00");
        ctcData.setMonthlyProvidentFund("5,000.00");
        ctcData.setMonthlyTotalDeduction("12,000.00");

        when(calculator.compensationDetails(anyDouble())).thenReturn(ctcData);
        when(dateConverter.stringToLocalDateTimeConverter(request.getPayDate())).thenReturn(new Date());
        when(util.getNumberOfDaysInMonth(anyString())).thenReturn(22);

        // When
        Payroll payroll = mapper.mapCtcDataToPayroll(request, employee);

        // Then
        assertNotNull(payroll);
        assertEquals((new Date(payroll.getPayDate().getTime())), payroll.getPayDate());
        assertEquals("April 2023", payroll.getPayPeriod());
        assertEquals(30000.0, payroll.getBasic());
        assertEquals(10000.0, payroll.getHouseRentAllowance());
        assertEquals(5000.0, payroll.getMedicalAllowance());
        assertEquals(2000.0, payroll.getOtherAllowance());
        assertEquals(63000.0, payroll.getGrossEarnings());
        assertEquals(10000.0, payroll.getIncomeTax());
        assertEquals(1000.0, payroll.getProfessionalTax());
        assertEquals(5000.0, payroll.getProvidentFund());
        assertEquals(15134.0, payroll.getTotalDeductions());
        assertEquals(47866.0, payroll.getTotalNetPayable());
        assertEquals(20, payroll.getTotalPaidDays());
        assertEquals(2, payroll.getTotalLopDays());
        assertEquals(3134.0, payroll.getLeaveDeduction());
    }
    @Test
    void testMapCtcDataToPayroll_LopDaysNull() {
        // Given
        AddMonthlyPayRollRequest request = new AddMonthlyPayRollRequest();

        request.setPayDate("2023-04-01");
        request.setPayPeriod("April 2023");

        Employee employee = new Employee();
        employee.setGrossSalary(60000.0);

        CtcData ctcData = new CtcData();
        ctcData.setMonthlyBasic("30,000.00");
        ctcData.setMonthlyHRA("10,000.00");
        ctcData.setMonthlyMedAllowance("5,000.00");
        ctcData.setMonthlyOtherAllowance("2,000.00");
        ctcData.setMonthlyGrossCtc("63,000.00");
        ctcData.setMonthlyIncomeTax("10,000.00");
        ctcData.setMonthlyProfessionalTax("1,000.00");
        ctcData.setMonthlyProvidentFund("5,000.00");
        ctcData.setMonthlyTotalDeduction("12,000.00");

        when(calculator.compensationDetails(anyDouble())).thenReturn(ctcData);
        when(dateConverter.stringToLocalDateTimeConverter(request.getPayDate())).thenReturn(new Date());
        when(util.getNumberOfDaysInMonth(anyString())).thenReturn(22);

        // When
        Payroll payroll = mapper.mapCtcDataToPayroll(request, employee);

        // Then
        assertNotNull(payroll);
        assertEquals((new Date(payroll.getPayDate().getTime())), payroll.getPayDate());
        assertEquals("April 2023", payroll.getPayPeriod());
        assertEquals(30000.0, payroll.getBasic());
        assertEquals(10000.0, payroll.getHouseRentAllowance());
        assertEquals(5000.0, payroll.getMedicalAllowance());
        assertEquals(2000.0, payroll.getOtherAllowance());
        assertEquals(63000.0, payroll.getGrossEarnings());
        assertEquals(10000.0, payroll.getIncomeTax());
        assertEquals(1000.0, payroll.getProfessionalTax());
        assertEquals(5000.0, payroll.getProvidentFund());
        assertEquals(12000.0, payroll.getTotalDeductions());
        assertEquals(51000.0, payroll.getTotalNetPayable());
        assertEquals(22, payroll.getTotalPaidDays());
        assertEquals(0, payroll.getTotalLopDays());
        assertEquals(0.0, payroll.getLeaveDeduction());
    }

    @Test
    void testMapCtcDataToPayrollWhenNull() {
        CtcData data = null;
        AddMonthlyPayRollRequest request = new AddMonthlyPayRollRequest();
        request.setLopDays("2");
        request.setPayDate("2023-04-01");
        request.setPayPeriod("April 2023");

        Employee employee = new Employee();
        employee.setGrossSalary(60000.0);

        Payroll payroll=mapper.mapCtcDataToPayroll(request,employee);

        assertNull(payroll);
    }

    @Test
    void testConvertToOfferLetterEntity() {
        // Given
        OfferLetterDTO offerLetterDTO = new OfferLetterDTO();
        offerLetterDTO.setCtc("60,000.00");
        offerLetterDTO.setFullName("John Doe");
        offerLetterDTO.setEmail("john.doe@example.com");
        offerLetterDTO.setJoiningDate("2023-04-01");
        offerLetterDTO.setIssuedDate("2023-04-01");
        offerLetterDTO.setPhoneNumber("1234567890");
        offerLetterDTO.setDesignation("Developer");
        offerLetterDTO.setDepartment("IT");

        when(dateConverter.stringToLocalDateTimeConverter(anyString())).thenReturn(new Date());

        // When
        OfferLetterEntity offerLetterEntity = mapper.convertToOfferLetterEntity(offerLetterDTO);

        // Then
        assertNotNull(offerLetterEntity);
        assertEquals(60000.0, offerLetterEntity.getCtc());
        assertEquals("John Doe", offerLetterEntity.getFullName());
        assertEquals("john.doe@example.com", offerLetterEntity.getEmail());
        assertEquals(new Date(offerLetterEntity.getJoiningDate().getTime()), offerLetterEntity.getJoiningDate());
        assertEquals(new Date(offerLetterEntity.getIssuedDate().getTime()), offerLetterEntity.getIssuedDate());
        assertEquals("1234567890", offerLetterEntity.getPhoneNumber());
        assertEquals("Developer", offerLetterEntity.getDesignation());
        assertEquals("IT", offerLetterEntity.getDepartment());
    }

    @Test
    void testConvertToOfferLetterEntityWhenNull(){
        OfferLetterDTO offerLetterDTO=null;
        assertThrows(RuntimeException.class,()->mapper.convertToOfferLetterEntity(offerLetterDTO));
    }
    @Test
    void testConvertToOfferLetterDto() {
        // Given
        OfferLetterEntity offerLetterEntity = new OfferLetterEntity();
        offerLetterEntity.setCtc(60000.0);
        offerLetterEntity.setFullName("John Doe");
        offerLetterEntity.setEmail("john.doe@example.com");
        offerLetterEntity.setJoiningDate(new Date());
        offerLetterEntity.setIssuedDate(new Date());
        offerLetterEntity.setPhoneNumber("1234567890");
        offerLetterEntity.setDesignation("Developer");
        offerLetterEntity.setDepartment("IT");

        when(dateConverter.localDateTimeToStringConverter(any())).thenReturn("2023-04-01");

        // When
        OfferLetterDTO offerLetterDTO = mapper.convertToOfferLetterDto(offerLetterEntity);

        // Then
        assertNotNull(offerLetterDTO);
        assertEquals("60,000.00", offerLetterDTO.getCtc());
        assertEquals("John Doe", offerLetterDTO.getFullName());
        assertEquals("john.doe@example.com", offerLetterDTO.getEmail());
        assertEquals("2023-04-01", offerLetterDTO.getJoiningDate());
        assertEquals("2023-04-01", offerLetterDTO.getIssuedDate());
        assertEquals("1234567890", offerLetterDTO.getPhoneNumber());
        assertEquals("Developer", offerLetterDTO.getDesignation());
        assertEquals("IT", offerLetterDTO.getDepartment());
    }

    @Test
    void testConvertToHikeEntityDto() {
        // Given
        Employee employee=new Employee();
        employee.setEmployeeID("123");

        HikeEntity hikeEntity = new HikeEntity();
        hikeEntity.setId(1L);
        hikeEntity.setHikePercentage(10.0);
        hikeEntity.setEffectiveDate(new Date());
        hikeEntity.setEmployee(new Employee());
        hikeEntity.setEmployee(employee);
        hikeEntity.setReason("Performance");
        hikeEntity.setApprovedDate(new Date());
        hikeEntity.setNewSalary(66000.0);
        hikeEntity.setPrevSalary(60000.0);
        hikeEntity.setIsApproved(true);
        hikeEntity.setNewPosition("Senior Developer");
        hikeEntity.setPrevPosition("Developer");
        hikeEntity.setApprovedBy(new Employee());
        hikeEntity.getApprovedBy().setEmployeeID("456");

        when(dateConverter.localDateTimeToStringConverter(any())).thenReturn("2023-04-01");

        // When
        HikeEntityDTO hikeEntityDTO = mapper.convertToHikeEntityDto(hikeEntity);

        // Then
        assertNotNull(hikeEntityDTO);
        assertEquals(1L, hikeEntityDTO.getId());
        assertEquals("10.0", hikeEntityDTO.getHikePercentage());
        assertEquals("2023-04-01", hikeEntityDTO.getEffectiveDate());
        assertEquals("123", hikeEntityDTO.getEmployeeId());
        assertEquals("Performance", hikeEntityDTO.getReason());
        assertEquals("2023-04-01", hikeEntityDTO.getApprovedDate());
        assertEquals("66,000.00", hikeEntityDTO.getNewSalary());
        assertEquals("60,000.00", hikeEntityDTO.getPrevSalary());
        assertTrue(hikeEntityDTO.getStatus());
        assertEquals("Senior Developer", hikeEntityDTO.getNewPosition());
        assertEquals("Developer", hikeEntityDTO.getPrevPosition());
        assertEquals("456", hikeEntityDTO.getApprovedBy());
    }
    @Test
    void testConvertToHikeEntityDtoWhenApprovedByNull() {
        // Given
        Employee employee=new Employee();
        employee.setEmployeeID("123");

        HikeEntity hikeEntity = new HikeEntity();
        hikeEntity.setId(1L);
        hikeEntity.setHikePercentage(10.0);
        hikeEntity.setEffectiveDate(new Date());
        hikeEntity.setEmployee(new Employee());
        hikeEntity.setEmployee(employee);
        hikeEntity.setReason("Performance");
        hikeEntity.setApprovedDate(new Date());
        hikeEntity.setNewSalary(66000.0);
        hikeEntity.setPrevSalary(60000.0);
        hikeEntity.setIsApproved(true);
        hikeEntity.setNewPosition("Senior Developer");
        hikeEntity.setPrevPosition("Developer");
        hikeEntity.setApprovedBy(null);

        when(dateConverter.localDateTimeToStringConverter(any())).thenReturn("2023-04-01");

        // When
        HikeEntityDTO hikeEntityDTO = mapper.convertToHikeEntityDto(hikeEntity);

        // Then
        assertNotNull(hikeEntityDTO);
        assertEquals(1L, hikeEntityDTO.getId());
        assertEquals("10.0", hikeEntityDTO.getHikePercentage());
        assertEquals("2023-04-01", hikeEntityDTO.getEffectiveDate());
        assertEquals("123", hikeEntityDTO.getEmployeeId());
        assertEquals("Performance", hikeEntityDTO.getReason());
        assertEquals("2023-04-01", hikeEntityDTO.getApprovedDate());
        assertEquals("66,000.00", hikeEntityDTO.getNewSalary());
        assertEquals("60,000.00", hikeEntityDTO.getPrevSalary());
        assertTrue(hikeEntityDTO.getStatus());
        assertEquals("Senior Developer", hikeEntityDTO.getNewPosition());
        assertEquals("Developer", hikeEntityDTO.getPrevPosition());

    }

    @Test
    void testGetCalendarDTO() {
        // Given
        CalendarEntity calendarEntity = new CalendarEntity();
        calendarEntity.setDate(new Date());
        calendarEntity.setEvent("Team Meeting");

        when(dateConverter.localDateTimeToStringConverter(any())).thenReturn("2024-03-01");

        // When
        CalendarDTO calendarDTO = mapper.getCalendarDTO(calendarEntity);

        // Then
        assertNotNull(calendarDTO);
        assertEquals("2024-03-01", calendarDTO.getDate());
        assertEquals("Team Meeting", calendarDTO.getEvent());
        assertEquals("March 2024", calendarDTO.getPeriod());
    }

    @Test
    void testGetCalendarEntity() {
        // Given
        CalendarDTO calendarDTO = new CalendarDTO();
        calendarDTO.setDate("2024-03-01");
        calendarDTO.setEvent("Team Meeting");
        calendarDTO.setPeriod("April 2023");

        when(dateConverter.stringToLocalDateTimeConverter(anyString())).thenReturn(new Date());

        // When
        CalendarEntity calendarEntity = mapper.getCalendarEntity(calendarDTO);

        // Then
        assertNotNull(calendarEntity);
        assertEquals(new Date(calendarEntity.getDate().getTime()), calendarEntity.getDate());
        assertEquals("Team Meeting", calendarEntity.getEvent());
        assertEquals("March 2024", calendarEntity.getPeriod());
    }

}
