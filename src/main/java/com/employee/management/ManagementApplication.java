package com.employee.management;


import com.employee.management.converters.AmountToWordsConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;


@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class ManagementApplication {
	public static void main(String[] args) {
		SpringApplication.run(ManagementApplication.class, args);

	}
}
