package com.employee.management.service;

import com.employee.management.DTO.*;
import net.sf.jasperreports.engine.JRException;

import java.util.List;

public interface AdminService {
    String addNewEmployee(EmployeeDTO employeeDTO);

    AdminDashBoardData loadData();

    List<HikeEntityDTO> hikeRecommendations();

    List<EmployeeDTO> fetchAllActiveEmployees();

    EmployeeDTO editEmployee(String empId,EmployeeDTO employeeDTO);

    String fetchEmployeeDesignation(String empId);

    String changeEmployeeStatus(String empId, String empStatus);

    String addPayroll(PayrollDTO payrollDTO,String empId);

    String addMonthlyPayRoll(AddMonthlyPayRollRequest request);

    byte[] previewPayslipPdf(AddMonthlyPayRollRequest request) throws JRException;

    List<AvgSalaryGraphResponse> getSalaryGraphDataForPastSixMonths();
    String updatePfDetails(PfNumberUpdateRequest request);

    String updateHikeDetails(HikeUpdateRequest request);

    HikeEntityDTO giveHike(HikeUpdateRequest request);

    byte[] previewHikeDetails(HikeUpdateRequest request);

    String sendHikeLetter(Long id);

    HikeEntityDTO editHikeLetter(HikeEntityDTO hikeEntityDTO);
}
