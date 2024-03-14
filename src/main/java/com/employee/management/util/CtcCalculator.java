package com.employee.management.util;

import com.employee.management.DTO.CtcData;
import org.springframework.stereotype.Component;


@Component
public class CtcCalculator {

    private final double HRA_PERCENTAGE = 0.1;
    private final double MED_PERCENTAGE = 0.3;
    private double IT_PERCENTAGE = 0;
    private final double PF = 1000;
    private final double BASIC = 8333;
    private final double PROF_TAX = 200;
    private final double LEAVE_DED = 0;



    public CtcData compensationDetails(Double grossSalary) {
        if (grossSalary > 2000000) {
            IT_PERCENTAGE = 0.30;
        } else if (grossSalary > 1200000) {
            IT_PERCENTAGE = 0.20;
        } else if (grossSalary >= 900000) {
            IT_PERCENTAGE = 0.10;
        } else if (grossSalary>= 500000) {
            IT_PERCENTAGE = 0.05;
        }
        else IT_PERCENTAGE = 0.0;


        double monthlyGrossSalary = grossSalary / 12;

        CtcData data = new CtcData();
        data.setYearlyGrossCtc((Formatters.formatAmountWithCommas(grossSalary)));
        data.setMonthlyGrossCtc((Formatters.formatAmountWithCommas(monthlyGrossSalary)));

        calculateMonthlyCompensations(data);

        double monthlyHRA = monthlyGrossSalary * HRA_PERCENTAGE;
        double monthlyMedAllowance = monthlyGrossSalary * MED_PERCENTAGE;
        double monthlyOtherAllowance = monthlyGrossSalary - (monthlyMedAllowance + BASIC + monthlyHRA);

        data.setYearlyBasic((Formatters.formatAmountWithCommas(BASIC * 12)));
        data.setMonthlyBasic((Formatters.formatAmountWithCommas(BASIC)));
        data.setYearlyHRA((Formatters.formatAmountWithCommas(monthlyHRA*12)));
        data.setMonthlyHRA((Formatters.formatAmountWithCommas(monthlyHRA)));
        data.setYearlyMedAllowance((Formatters.formatAmountWithCommas(monthlyMedAllowance * 12)));
        data.setMonthlyMedAllowance((Formatters.formatAmountWithCommas(monthlyMedAllowance)));
        data.setYearlyOtherAllowance((Formatters.formatAmountWithCommas(monthlyOtherAllowance * 12)));
        data.setMonthlyOtherAllowance((Formatters.formatAmountWithCommas(monthlyOtherAllowance)));
        data.setYearlyIncomeTax((Formatters.formatAmountWithCommas(Formatters.convertStringToDoubleAmount(data.getMonthlyIncomeTax())*12)));
        data.setYearlyLeaveDeduction((Formatters.formatAmountWithCommas(Formatters.convertStringToDoubleAmount(data.getMonthlyLeaveDeduction())*12)));
        data.setYearlyProvidentFund((Formatters.formatAmountWithCommas(Formatters.convertStringToDoubleAmount(data.getMonthlyProvidentFund())*12)));
        data.setYearlyProfessionalTax((Formatters.formatAmountWithCommas(Formatters.convertStringToDoubleAmount(data.getMonthlyProfessionalTax())*12)));
        data.setYearlyTotalDeduction((Formatters.formatAmountWithCommas(Formatters.convertStringToDoubleAmount(data.getMonthlyTotalDeduction())*12)));
        data.setYearlyNetPayable((Formatters.formatAmountWithCommas(Formatters.convertStringToDoubleAmount(data.getYearlyGrossCtc())-Formatters.convertStringToDoubleAmount(data.getYearlyTotalDeduction()))));

        return data;
    }
    private void calculateMonthlyCompensations(CtcData data) {
        double monthlyGrossSalary = (Formatters.convertStringToDoubleAmount(data.getMonthlyGrossCtc()));
        data.setMonthlyBasic(Formatters.formatAmountWithCommas(BASIC));
        data.setMonthlyHRA(Formatters.formatAmountWithCommas(monthlyGrossSalary * HRA_PERCENTAGE));

        double monthlyMedAllowance = monthlyGrossSalary * MED_PERCENTAGE;
        Double monthlyOtherAllowance = monthlyGrossSalary - (monthlyMedAllowance + BASIC + Formatters.convertStringToDoubleAmount(data.getMonthlyHRA()));

        data.setMonthlyMedAllowance(Formatters.formatAmountWithCommas(monthlyMedAllowance));
        data.setMonthlyOtherAllowance(Formatters.formatAmountWithCommas(monthlyOtherAllowance));
        data.setMonthlyIncomeTax(Formatters.formatAmountWithCommas(monthlyGrossSalary * IT_PERCENTAGE));
        data.setMonthlyProfessionalTax(Formatters.formatAmountWithCommas(PROF_TAX));
        data.setMonthlyLeaveDeduction(Formatters.formatAmountWithCommas(LEAVE_DED));
        data.setMonthlyProvidentFund(Formatters.formatAmountWithCommas(PF));
        double totalDeduction = PROF_TAX + Formatters.convertStringToDoubleAmount(data.getMonthlyIncomeTax()) + PF + LEAVE_DED;
        data.setMonthlyTotalDeduction(Formatters.formatAmountWithCommas(totalDeduction));
        data.setMonthlyNetPayable(Formatters.formatAmountWithCommas(monthlyGrossSalary - totalDeduction));
    }
}
