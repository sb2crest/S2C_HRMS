package com.employee.management.repository;

import com.employee.management.models.Role;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<Role,Long> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO roles (name) SELECT 'ROLE_ADMIN' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN') " +
            "UNION ALL " +
            "SELECT 'ROLE_USER' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_USER')", nativeQuery = true)
    void insertInitialRolesIfNotExist();

}
