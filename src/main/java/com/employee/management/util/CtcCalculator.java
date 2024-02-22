package com.employee.management.util;

import com.employee.management.DTO.CtcData;
import com.employee.management.converters.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CtcCalculator {
    private final double BASIC_PERCENTAGE=0.6;
    private final double HRA_PERCENTAGE=0.5;
    private final double MED_PERCENTAGE=0.7;
    private Double monthlyGrossSalary;
    @Autowired
    Mapper mapper;

    public CtcData compensationDetails(String grossSalary){
        Double yearlyGrossEarnings=mapper.convertStringToDoubleAmount(grossSalary);
        CtcData data=new CtcData();
        monthlyGrossSalary=yearlyGrossEarnings/12;
        data.setMonthlyGrossCtc((double) Math.round(monthlyGrossSalary));
        data.setMonthlyBasic((double) Math.round(monthlyGrossSalary*BASIC_PERCENTAGE));
        data.setMonthlyHRA((double) Math.round(data.getMonthlyBasic()*HRA_PERCENTAGE));
        Double monthlyBalance=monthlyGrossSalary-(data.getMonthlyBasic()+data.getMonthlyHRA());
        Double monthlyMedAllowance=monthlyBalance*MED_PERCENTAGE;
        data.setMonthlyMedAllowance((double) Math.round(monthlyMedAllowance));
        data.setMonthlyOtherAllowance((double) Math.round(monthlyBalance-monthlyMedAllowance));

        data.setYearlyGrossCtc((double) Math.round(yearlyGrossEarnings));
        data.setYearlyBasic((double) Math.round(yearlyGrossEarnings*BASIC_PERCENTAGE));
        data.setYearlyHRA((double) Math.round(data.getYearlyBasic()*HRA_PERCENTAGE));
        Double yearlyBalance=yearlyGrossEarnings-(data.getYearlyBasic()+data.getYearlyHRA());
        Double yearlyMedAllowance=yearlyBalance*MED_PERCENTAGE;
        data.setYearlyMedAllowance((double) Math.round(yearlyMedAllowance));
        data.setYearlyOtherAllowance((double) Math.round(yearlyBalance-monthlyMedAllowance));
        return data;
    }
}
