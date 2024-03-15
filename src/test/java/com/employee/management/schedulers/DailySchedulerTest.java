package com.employee.management.schedulers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import com.employee.management.models.Employee;
import com.employee.management.models.HikeEntity;
import com.employee.management.repository.EmployeeRepository;
import com.employee.management.repository.HikeRepository;
import com.employee.management.service.EmailSenderService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
class DailySchedulerTest {

    @Mock
    EmployeeRepository employeeRepository;

    @Mock
    HikeRepository hikeRepository;

    @Mock
    EmailSenderService emailSenderService;

    @InjectMocks
    DailyScheduler dailyScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHikeTableUpdater() {
        // Given
        Employee employee1 = new Employee();
        employee1.setEmployeeID("1");
        employee1.setNextHikeDate(Date.from(LocalDate.now().plusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        Employee employee2 = new Employee();
        employee2.setEmployeeID("2");
        employee2.setNextHikeDate(Date.from(LocalDate.now().minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        List<Employee> employees = Arrays.asList(employee1, employee2);

        when(employeeRepository.findByStatusName("active")).thenReturn(employees);

        // When
        dailyScheduler.hikeTableUpdater();

        // Then
    }
}
