package com.employee.management.service.impl;

import com.employee.management.DTO.*;
import com.employee.management.converters.Mapper;
import com.employee.management.exception.CompanyException;
import com.employee.management.models.Employee;
import com.employee.management.models.Payroll;
import com.employee.management.repository.EmployeeRepository;
import com.employee.management.repository.PayrollRepository;
import com.employee.management.util.CtcCalculator;
import com.employee.management.util.Formatters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PayRollServiceImplTest {

    @Mock
    PayrollRepository payrollRepository;

    @Mock
    EmployeeRepository employeeRepository;

    @Mock
    Mapper mapper;

    @MockBean
    CtcCalculator ctcCalculator;

    @InjectMocks
    PayRollServiceImpl payRollService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPaySlip() {

        String empId = "1";
        String payPeriod = "2023-04";
        Employee employee = new Employee();
        employee.setEmployeeID(empId);
        Payroll payroll = new Payroll();
        PaySlip expectedPaySlip = new PaySlip();
        EmployeeDTO employeeDTO = new EmployeeDTO();
        PayrollDTO payrollDTO = new PayrollDTO();

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));
        when(payrollRepository.getPayPeriodDetails(payPeriod, employee)).thenReturn(Optional.of(payroll));

        when(mapper.convertToEmployeeDTO(employee)).thenReturn(employeeDTO);
        when(mapper.convertToPayRollDTO(payroll)).thenReturn(payrollDTO);

        PaySlip result = payRollService.getPaySlip(empId, payPeriod);

        assertEquals(employeeDTO, result.getEmployeeDTO());
        assertEquals(payrollDTO, result.getPayrollDTO());

        verify(employeeRepository, times(1)).findById(empId);
        verify(payrollRepository, times(1)).getPayPeriodDetails(payPeriod, employee);
        verify(mapper, times(1)).convertToEmployeeDTO(employee);
        verify(mapper, times(1)).convertToPayRollDTO(payroll);
    }

    @Test
    void testGetPaySlip_throwsEmployeeNotFound() {
        String empId = "1";
        String payPeriod = "2023-04";
        Employee employee = new Employee();
        employee.setEmployeeID(empId);
        when(employeeRepository.findById(empId)).thenReturn(Optional.empty());

        assertThrows(CompanyException.class, () -> payRollService.getPaySlip(empId, payPeriod));
        verify(employeeRepository, times(1)).findById(empId);
    }

    @Test
    void testGetPaySlip_throwsSalarySlipExists() {
        String empId = "1";
        String payPeriod = "2023-04";
        Employee employee = new Employee();
        employee.setEmployeeID(empId);
        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));
        when(payrollRepository.getPayPeriodDetails(payPeriod, employee)).thenReturn(Optional.empty());


        assertThrows(CompanyException.class, () -> payRollService.getPaySlip(empId, payPeriod));
        verify(employeeRepository, times(1)).findById(empId);
    }

    @Test
    void testGetPayRollDetails() {
        String empId = "S2C01";
        Employee employee = new Employee();
        employee.setEmployeeID(empId);
        employee.setGrossSalary(100000D);
        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));

        CtcData result = payRollService.getPayrollDetails(empId);

        assertNotNull(result);
        assertEquals(result.getYearlyGrossCtc(), "1,00,000.00");
        verify(employeeRepository, times(1)).findById(empId);
    }

    @Test
    void testGetPayRollDetailsWithException() {
        String empId = "S2C01";
        Employee employee = new Employee();
        employee.setEmployeeID(empId);
        employee.setGrossSalary(100000D);
        when(employeeRepository.findById(empId)).thenReturn(Optional.empty());

        assertThrows(CompanyException.class, () -> payRollService.getPayrollDetails(empId));
        verify(employeeRepository, times(1)).findById(empId);
    }
    @Test
    void testGetPayrollDetailsWithLeaveDeduction_Success() {
        // Arrange
        AddMonthlyPayRollRequest request = new AddMonthlyPayRollRequest();
        request.setEmployeeId("1");
        request.setLopDays("5");

        Employee employee = new Employee();
        employee.setGrossSalary(100000D);

        CtcData ctcData = new CtcData();
        ctcData.setMonthlyTotalDeduction("1,000.00");
        ctcData.setMonthlyNetPayable("9,000.00");

        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        when(ctcCalculator.compensationDetails(anyDouble())).thenReturn(ctcData);

        // Act
        CtcData result = payRollService.getPayrollDetailsWithLeaveDeduction(request);

        // Assert
        assertNotNull(result);
        assertEquals(result.getMonthlyTotalDeduction(),"2,589.00");
    }
    @Test
    void testGetPayrollDetailsWithLeaveDeduction_Exception() {
        AddMonthlyPayRollRequest request = new AddMonthlyPayRollRequest();
        request.setEmployeeId("1");
        request.setLopDays("5");
        when(employeeRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(CompanyException.class, () -> payRollService.getPayrollDetailsWithLeaveDeduction(request));
    }

}
