package com.employee.management.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class Formatters {
    public static Double convertStringToDoubleAmount(String amount){
        if(amount!=null) {
            amount = amount.replace(",", "");
            return Double.parseDouble(amount);
        }
        else return null;
    }

    public static String formatAmountWithCommas(Double number) {

        if (number == null) {
            return "";
        }
        double roundedNo=Math.round(number);
        if(number==0){
            return "0.00";
        }
        BigDecimal amount= BigDecimal.valueOf(roundedNo);

        String numb = String.valueOf(amount);
        String numberStr;
        String split = null;
        if(numb.contains(".")) {
            String[] num = numb.split("\\.");
            numberStr = num[0];
            split = num[1];
        } else numberStr = numb;
        StringBuilder result = getStringBuilder(numberStr);
        if(split!=null)
            result.append(".").append(split).append("0");
        else result.append(".00");
        return result.toString();
    }
    private static StringBuilder getStringBuilder(String numberStr) {
        StringBuilder result = new StringBuilder();

        int len = numberStr.length();
        int count = 0;
        for (int i = len - 1; i >= 0; i--) {
            result.insert(0, numberStr.charAt(i));
            count++;
            if (count == 3 && i != 0) {
                result.insert(0, ",");
            }
            if (count == 5 && i != 0) {
                result.insert(0, ",");
            }
            if (count == 7 && i != 0) {
                result.insert(0, ",");
            }
            if(count == 9 && i != 0) {
                result.insert(0, ",");
            }
        }
        return result;
    }
}
