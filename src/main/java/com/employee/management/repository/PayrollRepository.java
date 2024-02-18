package com.employee.management.repository;

import com.employee.management.models.Employee;
import com.employee.management.models.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<Payroll,Long> {
   Optional<Payroll> findByEmployee(Employee employee);
   @Query("select a from Payroll a where a.payPeriod=:payPeriod and a.employee=:employee ")
   Optional<Payroll> getPayPeriodDetails(String payPeriod,Employee employee);
   @Query("select a from Payroll a where a.payPeriod=:payPeriod ")
   Optional<List<Payroll>>getPayDetails(String payPeriod);
}
