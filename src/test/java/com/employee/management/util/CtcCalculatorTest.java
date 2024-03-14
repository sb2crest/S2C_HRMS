package com.employee.management.util;

import com.employee.management.DTO.CtcData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CtcCalculatorTest {
    @InjectMocks
    private CtcCalculator ctcCalculator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testCompensationDetails_Below500000() {
        Double grossSalary = 400000.0;
        CtcData data=ctcCalculator.compensationDetails(grossSalary);
        assertEquals("4,00,000.00", data.getYearlyGrossCtc());
        assertEquals("33,333.00", data.getMonthlyGrossCtc());
        assertEquals("0.00", data.getMonthlyIncomeTax());
    }
    @Test
    void testCompensationDetails_Above500000() {
        CtcData data = ctcCalculator.compensationDetails(500001.0);
        assertEquals("5,00,001.00", data.getYearlyGrossCtc());
        assertEquals("41,667.00", data.getMonthlyGrossCtc());
        assertEquals("24,996.00", data.getYearlyIncomeTax());

    }
    @Test
    void testCompensationDetails_Above1000000() {
        Double grossSalary = 1000001D;
        CtcData data = ctcCalculator.compensationDetails(grossSalary);

        assertEquals("10,00,001.00", data.getYearlyGrossCtc());
        assertEquals("83,333.00", data.getMonthlyGrossCtc());

    }
    @Test
    void testCompensationDetails_Above2000000() {
        CtcData data = ctcCalculator.compensationDetails(2000001.0);
        assertEquals("20,00,001.00", data.getYearlyGrossCtc());
        assertEquals("1,66,667.00", data.getMonthlyGrossCtc());

    }
}