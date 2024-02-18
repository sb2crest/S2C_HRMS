package com.employee.management.controller;

import com.employee.management.DTO.EmployeeDTO;
import com.employee.management.DTO.PaySlip;
import com.employee.management.DTO.PayrollDTO;
import com.employee.management.DTO.PayrollRequest;
import com.employee.management.converters.AmountToWordsConverter;
import com.employee.management.converters.PDFGeneratorForPaySlip;
import com.employee.management.exception.CompanyException;
import com.employee.management.exception.ResCodes;
import com.employee.management.models.Employee;
import com.employee.management.models.Payroll;
import com.employee.management.service.EmployeeService;
import com.employee.management.service.PayRollService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/salary")
@CrossOrigin(origins = "http://localhost:3000")
public class PayRollController {
    @Autowired
    PayRollService payRollService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    AmountToWordsConverter amountToWordsConverter;
    @Autowired
    PDFGeneratorForPaySlip pdfGeneratorForPaySlip;
    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<PaySlip> getPaySlip(@RequestParam("employeeId")String empId,@RequestParam("payPeriod")String payPeriod,@RequestParam("password")String password){
        return new ResponseEntity<>(payRollService.getPaySlip(empId,payPeriod), HttpStatus.OK);
    }
    @GetMapping("/download")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<byte[]> getPaySlipDownload(@RequestParam("employeeId") String empId, @RequestParam("payPeriod") String payPeriod) {
        PaySlip paySlip = payRollService.getPaySlip(empId, payPeriod);
        String amountInWords = amountToWordsConverter.convertToIndianCurrency(paySlip.getPayrollDTO().getTotalNetPayable());
        try {
            byte[] pdfBytes = pdfGeneratorForPaySlip.generatePaySlipPdf(paySlip, amountInWords);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "pay_slip(" + paySlip.getEmployeeDTO().getEmployeeName()+ "_" + paySlip.getPayrollDTO().getPayPeriod() + ").pdf");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
