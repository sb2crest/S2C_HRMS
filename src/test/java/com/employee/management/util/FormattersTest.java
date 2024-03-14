package com.employee.management.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormattersTest {
    Formatters formatters=new Formatters();

    @Test
    void testConvertStringToDoubleAmount() {
        assertEquals(1234.56, formatters.convertStringToDoubleAmount("1234.56"));

        assertEquals(1234567.89, formatters.convertStringToDoubleAmount("1,234,567.89"));
        assertEquals(11234567.89, formatters.convertStringToDoubleAmount("11,234,567.89"));

        assertEquals(1234567.0, formatters.convertStringToDoubleAmount("1,234,567"));

    }
    @Test
    void testFormatAmountWithCommas() {
        assertEquals("2,000.00", formatters.formatAmountWithCommas(2000D));

        assertEquals("1,12,34,568.00", formatters.formatAmountWithCommas(11234567.89));

        assertEquals("", formatters.formatAmountWithCommas(null));

        assertEquals("0.00", formatters.formatAmountWithCommas(0.0));

        assertEquals("200.00", formatters.formatAmountWithCommas(200D));
    }
}