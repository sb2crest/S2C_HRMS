package com.employee.management.service.impl;

import com.employee.management.DTO.EmployeeDTO;
import com.employee.management.converters.Mapper;
import com.employee.management.exception.CompanyException;
import com.employee.management.exception.ResCodes;
import com.employee.management.models.Employee;
import com.employee.management.repository.EmployeeRepository;
import com.employee.management.service.EmployeeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    Mapper mapper;
    @Override
    public EmployeeDTO getEmployee(String id){
       Employee employee= employeeRepository.findById(id).orElseThrow(()->new CompanyException(ResCodes.EMPLOYEE_NOT_FOUND));
      return mapper.convertToEmployeeDTO(employee);
    }
    @Override
    public boolean verifyUser(Long id, String password) {
        Employee employee=employeeRepository.findById("")
                .orElseThrow(()->new CompanyException(ResCodes.EMPLOYEE_NOT_FOUND));
        if(employee.getPassword().equals(password) && employee.getStatus().getName().equals("active")){
            return true;
        }
        if(employee.getStatus().getName().equals("inactive")){
            throw new CompanyException(ResCodes.INACTIVE_EMPLOYEE);
        }
        return false;
    }

}
