package com.employee.management.util;

import static org.junit.jupiter.api.Assertions.*;

import com.employee.management.exception.CompanyException;
import com.employee.management.models.Employee;
import com.employee.management.models.OtpEntity;
import com.employee.management.repository.EmployeeRepository;
import com.employee.management.repository.OtpRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VerificationTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private OtpRepository otpRepository;

    @InjectMocks
    private Verification verification;

    @BeforeEach
    public void setUp() {
        // Reset the mocks before each test
        reset(employeeRepository, otpRepository);
    }

    @Test
    public void testVerifyOtp_ValidOtp() {
        // Given
        String empId = "testEmpId";
        String otp = "123456";
        Employee employee = new Employee();
        OtpEntity otpEntity = new OtpEntity();
        otpEntity.setOtpValue(otp);
        otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(5)); // OTP valid for 5 minutes

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));
        when(otpRepository.findByEmployee(employee)).thenReturn(Optional.of(otpEntity));

        // When
        boolean result = verification.verifyOtp(empId, otp);

        // Then
        assertTrue(result);
        verify(employeeRepository).findById(empId);
        verify(otpRepository).findByEmployee(employee);
    }

    @Test
    public void testVerifyOtp_EmployeeNotFound() {
        // Given
        String empId = "testEmpId";
        String otp = "123456";

        when(employeeRepository.findById(empId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CompanyException.class, () -> verification.verifyOtp(empId, otp));
        verify(employeeRepository).findById(empId);
    }

    @Test
    public void testVerifyOtp_InvalidOtp() {
        // Given
        String empId = "testEmpId";
        String otp = "123456";
        Employee employee = new Employee();
        OtpEntity otpEntity = new OtpEntity();
        otpEntity.setOtpValue("654321"); // Different OTP
        otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(5)); // OTP valid for 5 minutes

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));
        when(otpRepository.findByEmployee(employee)).thenReturn(Optional.of(otpEntity));

        // When
        boolean result = verification.verifyOtp(empId, otp);

        // Then
        assertFalse(result);
        verify(employeeRepository).findById(empId);
        verify(otpRepository).findByEmployee(employee);
    }

    @Test
    public void testVerifyOtp_OtpExpired() {
        // Given
        String empId = "testEmpId";
        String otp = "123456";
        Employee employee = new Employee();
        OtpEntity otpEntity = new OtpEntity();
        otpEntity.setOtpValue(otp);
        otpEntity.setExpiryTime(LocalDateTime.now().minusMinutes(5)); // OTP expired

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));
        when(otpRepository.findByEmployee(employee)).thenReturn(Optional.of(otpEntity));

        // When
        boolean result = verification.verifyOtp(empId, otp);

        // Then
        assertFalse(result);
        verify(employeeRepository).findById(empId);
        verify(otpRepository).findByEmployee(employee);
    }
}
