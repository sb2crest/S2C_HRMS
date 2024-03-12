package com.employee.management.util;

import com.employee.management.DTO.CtcData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CtcCalculator {
    @Autowired
    Formatters formatters;

    private static final double HRA_PERCENTAGE = 0.1;
    private static final double MED_PERCENTAGE = 0.3;
    private static double IT_PERCENTAGE = 0;
    private static final double PF = 1000;
    private static final double BASIC = 8333;
    private static final double PROF_TAX = 200;
    private static final double LEAVE_DED = 0;



    public CtcData compensationDetails(Double grossSalary) {
        if (grossSalary > 2000000) {
            IT_PERCENTAGE = 0.30;
        } else if (grossSalary > 1000000) {
            IT_PERCENTAGE = 0.20;
        } else if (grossSalary > 500000) {
            IT_PERCENTAGE = 0.10;
        } else if (grossSalary> 300000) {
            IT_PERCENTAGE = 0.05;
        }
        else IT_PERCENTAGE = 0.0;


        double monthlyGrossSalary = grossSalary / 12;

        CtcData data = new CtcData();
        data.setYearlyGrossCtc((formatters.formatAmountWithCommas(grossSalary)));
        data.setMonthlyGrossCtc((formatters.formatAmountWithCommas(monthlyGrossSalary)));

        calculateMonthlyCompensations(data);

        double monthlyHRA = monthlyGrossSalary * HRA_PERCENTAGE;
        double monthlyMedAllowance = monthlyGrossSalary * MED_PERCENTAGE;
        double monthlyOtherAllowance = monthlyGrossSalary - (monthlyMedAllowance + BASIC + monthlyHRA);

        data.setYearlyBasic((formatters.formatAmountWithCommas(BASIC * 12)));
        data.setMonthlyBasic((formatters.formatAmountWithCommas(BASIC)));
        data.setYearlyHRA((formatters.formatAmountWithCommas(monthlyHRA*12)));
        data.setMonthlyHRA((formatters.formatAmountWithCommas(monthlyHRA)));
        data.setYearlyMedAllowance((formatters.formatAmountWithCommas(monthlyMedAllowance * 12)));
        data.setMonthlyMedAllowance((formatters.formatAmountWithCommas(monthlyMedAllowance)));
        data.setYearlyOtherAllowance((formatters.formatAmountWithCommas(monthlyOtherAllowance * 12)));
        data.setMonthlyOtherAllowance((formatters.formatAmountWithCommas(monthlyOtherAllowance)));
        data.setYearlyIncomeTax((formatters.formatAmountWithCommas(formatters.convertStringToDoubleAmount(data.getMonthlyIncomeTax())*12)));
        data.setYearlyLeaveDeduction((formatters.formatAmountWithCommas(formatters.convertStringToDoubleAmount(data.getMonthlyLeaveDeduction())*12)));
        data.setYearlyProvidentFund((formatters.formatAmountWithCommas(formatters.convertStringToDoubleAmount(data.getMonthlyProvidentFund())*12)));
        data.setYearlyProfessionalTax((formatters.formatAmountWithCommas(formatters.convertStringToDoubleAmount(data.getMonthlyProfessionalTax())*12)));
        data.setYearlyTotalDeduction((formatters.formatAmountWithCommas(formatters.convertStringToDoubleAmount(data.getMonthlyTotalDeduction())*12)));
        data.setYearlyNetPayable((formatters.formatAmountWithCommas(formatters.convertStringToDoubleAmount(data.getYearlyGrossCtc())-formatters.convertStringToDoubleAmount(data.getYearlyTotalDeduction()))));

        return data;
    }
    private void calculateMonthlyCompensations(CtcData data) {
        double monthlyGrossSalary = (formatters.convertStringToDoubleAmount(data.getMonthlyGrossCtc()));
        data.setMonthlyBasic(formatters.formatAmountWithCommas(BASIC));
        data.setMonthlyHRA(formatters.formatAmountWithCommas(monthlyGrossSalary * HRA_PERCENTAGE));

        double monthlyMedAllowance = monthlyGrossSalary * MED_PERCENTAGE;
        Double monthlyOtherAllowance = monthlyGrossSalary - (monthlyMedAllowance + BASIC + formatters.convertStringToDoubleAmount(data.getMonthlyHRA()));

        data.setMonthlyMedAllowance(formatters.formatAmountWithCommas(monthlyMedAllowance));
        data.setMonthlyOtherAllowance(formatters.formatAmountWithCommas(monthlyOtherAllowance));
        data.setMonthlyIncomeTax(formatters.formatAmountWithCommas(monthlyGrossSalary * IT_PERCENTAGE));
        data.setMonthlyProfessionalTax(formatters.formatAmountWithCommas(PROF_TAX));
        data.setMonthlyLeaveDeduction(formatters.formatAmountWithCommas(LEAVE_DED));
        data.setMonthlyProvidentFund(formatters.formatAmountWithCommas(PF));
        double totalDeduction = PROF_TAX + formatters.convertStringToDoubleAmount(data.getMonthlyIncomeTax()) + PF + LEAVE_DED;
        data.setMonthlyTotalDeduction(formatters.formatAmountWithCommas(totalDeduction));
        data.setMonthlyNetPayable(formatters.formatAmountWithCommas(monthlyGrossSalary - totalDeduction));
    }
}
