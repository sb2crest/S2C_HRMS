package com.employee.management.repository;

import com.employee.management.models.CalendarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarRepository extends JpaRepository<CalendarEntity,Long> {
}
