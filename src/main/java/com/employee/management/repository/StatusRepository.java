package com.employee.management.repository;

import com.employee.management.models.Status;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status,Long> {
    Optional<Status> findByName(String status);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO status (name) SELECT 'active' WHERE NOT EXISTS (SELECT 1 FROM status WHERE name = 'active') " +
            "UNION ALL " +
            "SELECT 'inactive' WHERE NOT EXISTS (SELECT 1 FROM status WHERE name = 'inactive')", nativeQuery = true)
    void insertInitialStatusIfNotExist();
}
