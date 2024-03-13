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
    @Mock
    private Formatters formatters;
    @InjectMocks
    private CtcCalculator ctcCalculator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testCompensationDetails_Below500000() {
        CtcData data = new CtcData();
        Double grossSalary = 400000.0;
        when(ctcCalculator.compensationDetails(eq(grossSalary))).thenReturn(data);
        data.setYearlyGrossCtc(formatters.formatAmountWithCommas(grossSalary));
        assertEquals("4,00,000.00", data.getYearlyGrossCtc());
        assertEquals("33,333.00", data.getMonthlyGrossCtc());

    }
    @Test
    void testCompensationDetails_Above500000() {
        CtcData data = ctcCalculator.compensationDetails(500001.0);
        assertEquals("5,00,001.00", data.getYearlyGrossCtc());
        assertEquals("41,667.00", data.getMonthlyGrossCtc());

    }
    @Test
    void testCompensationDetails_Above1000000() {
        Double grossSalary = 400000.0;
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