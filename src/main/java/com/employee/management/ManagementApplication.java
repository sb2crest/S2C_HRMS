package com.employee.management;


import com.employee.management.converters.AmountToWordsConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@SpringBootApplication
@EnableScheduling
public class ManagementApplication {
	public static void main(String[] args) {
		SpringApplication.run(ManagementApplication.class, args);
		// Get the current date
		LocalDate currentDate = LocalDate.now();

		// Get the previous month
		LocalDate previousMonth = currentDate.minusMonths(1);

		// Format the previous month as "Month Year" (e.g., "January 2024")
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
		String previousMonthFormatted = previousMonth.format(formatter);

		System.out.println("Previous month: " + previousMonthFormatted);
	}
}
