package com.employee.management.converters;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AmountToWordsConverterTest {

    @Test
    void testConvertToIndianCurrency() {
        AmountToWordsConverter converter = new AmountToWordsConverter();

        assertEquals("Rupees One Hundred Only", converter.convertToIndianCurrency("100"));

        assertEquals("Rupees One Hundred Twenty Only", converter.convertToIndianCurrency("120.00"));

        assertEquals("Rupees One Lakh Only", converter.convertToIndianCurrency("100000"));

        assertEquals("Rupees One Crore Only", converter.convertToIndianCurrency("10000000"));

        assertEquals("Rupees One Lakh Eleven Thousands One Hundred Eleven Only", converter.convertToIndianCurrency("111111"));

        assertEquals("Rupees One Lakh Eleven Thousands One Hundred Twenty Only", converter.convertToIndianCurrency("111120"));
        assertEquals("Rupees One Lakh Eleven Thousands One Hundred Twenty Two Only", converter.convertToIndianCurrency("111122"));

        assertEquals("", converter.convertToIndianCurrency(""));

        assertEquals("", converter.convertToIndianCurrency(null));

        assertEquals("", converter.convertToIndianCurrency(""));

        assertEquals("Rupees Zero Only", converter.convertToIndianCurrency("0"));
    }
}
