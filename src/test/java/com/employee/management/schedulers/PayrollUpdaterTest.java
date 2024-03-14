package com.employee.management.schedulers;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import com.employee.management.models.Employee;
import com.employee.management.models.Payroll;
import com.employee.management.repository.AttendanceRepository;
import com.employee.management.repository.EmployeeRepository;
import com.employee.management.repository.PayrollRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
class PayrollUpdaterTest {

    @Mock
    EmployeeRepository employeeRepository;

    @Mock
    PayrollRepository payrollRepository;

    @Mock
    AttendanceRepository attendanceRepository;

    @InjectMocks
    PayrollUpdater payrollUpdater;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdatePayroll() {
        // Given
        Employee employee1 = new Employee();
        employee1.setEmployeeID("1");
        Employee employee2 = new Employee();
        employee2.setEmployeeID("2");
        List<Employee> employees = Arrays.asList(employee1, employee2);

        Payroll payroll1 = new Payroll();
        payroll1.setEmployee(employee1);
        Payroll payroll2 = new Payroll();
        payroll2.setEmployee(employee2);
        payroll1.setGrossEarnings(192000.0);
        payroll2.setGrossEarnings(192000.0);
        payroll1.setProfessionalTax(200D);
        payroll2.setProfessionalTax(200D);
        payroll1.setIncomeTax(200D);
        payroll2.setIncomeTax(200D);
        payroll1.setLeaveDeduction(200D);
        payroll2.setLeaveDeduction(200D);
        payroll1.setProvidentFund(200D);
        payroll2.setProvidentFund(200D);

        when(employeeRepository.findAll()).thenReturn(employees);
        when(payrollRepository.getPayPeriodDetails(anyString(), any())).thenReturn(Optional.of(payroll1));
        when(attendanceRepository.getNoOfAbsence(anyString(), any(), any())).thenReturn(0);


        payrollUpdater.updatePayroll();


    }
}
