package com.employee.management.controller;

import com.employee.management.DTO.EmployeeDTO;
import com.employee.management.DTO.EmployeeNameDTO;
import com.employee.management.exception.CompanyException;
import com.employee.management.exception.ResCodes;
import com.employee.management.service.AttendanceService;
import com.employee.management.service.EmployeeService;
import com.employee.management.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee")
@CrossOrigin(origins = "http://hrm-service-fe-16185511.ap-south-1.elb.amazonaws.com/")
public class EmployeeController {
    @Autowired
    private JWTService jwtService;
    @Autowired
    EmployeeService employeeService;


    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<EmployeeDTO>getEmployee(@RequestParam("empId")String id, @RequestHeader("Authorization")String jwtToken){
        System.out.println("Token :"+jwtToken);
        String token=jwtToken.substring(7);
        String empId=jwtService.extractUsername(token);
        if(String.valueOf(id).equals(empId))
             return new ResponseEntity<>(employeeService.getEmployee(id), HttpStatus.OK);
        else throw new CompanyException(ResCodes.NOT_AUTHORIZED);
    }

    @GetMapping("/get-by-id")
    public ResponseEntity<EmployeeNameDTO> getEmployeeName(@RequestParam String empId) {
        return new ResponseEntity<>(employeeService.getEmployeeNameById(empId), HttpStatus.OK);
    }
}
