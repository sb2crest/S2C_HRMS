package com.employee.management.util;

import com.employee.management.repository.RoleRepository;
import com.employee.management.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class DatabaseInitializer {

    private final RoleRepository roleRepository;
    private final StatusRepository statusRepository;

    @Autowired
    public DatabaseInitializer(RoleRepository roleRepository, StatusRepository statusRepository) {
        this.roleRepository = roleRepository;
        this.statusRepository = statusRepository;
    }

    @PostConstruct
    public void initializeDatabase() {
        roleRepository.insertInitialRolesIfNotExist();
        statusRepository.insertInitialStatusIfNotExist();
    }

}
