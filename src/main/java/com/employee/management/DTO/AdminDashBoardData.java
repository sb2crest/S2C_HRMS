package com.employee.management.DTO;

import com.employee.management.models.HikeEntity;
import lombok.Data;

import java.util.List;

@Data
public class AdminDashBoardData {
    private Double averageSalary;
    private String todayDate;
    private Integer noOfEmployees;
    //private List<HikeEntityDTO> hikeRecommendations;
}
