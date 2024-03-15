package com.employee.management.service;

import com.employee.management.DTO.CtcData;
import com.employee.management.DTO.EmployeeDTO;
import com.employee.management.DTO.OfferLetterDTO;
import com.employee.management.DTO.PaySlip;
import com.employee.management.converters.AmountToWordsConverter;
import com.employee.management.converters.Mapper;
import com.employee.management.models.HikeEntity;
import com.employee.management.util.CtcCalculator;
import com.employee.management.util.Formatters;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.type.PdfVersionEnum;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PDFService {
   private final Mapper mapper;
   private final CtcCalculator calculator;
   private final Formatters formatters;
   private final AmountToWordsConverter converter;
   PDFService(Mapper mapper,CtcCalculator calculator,Formatters formatters,AmountToWordsConverter converter){
       this.mapper=mapper;
       this.calculator=calculator;
       this.formatters=formatters;
       this.converter=converter;
   }

    public byte[] generatePaySlipPdf(PaySlip paySlip) throws JRException {
        InputStream template = getClass().getResourceAsStream("/templates/pay-slip.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(template);
        String amount= converter.convertToIndianCurrency(paySlip.getPayrollDTO().getTotalNetPayable());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("payroll", paySlip.getPayrollDTO());
        parameters.put("employee", paySlip.getEmployeeDTO());
        parameters.put("amount", amount);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
    public byte[] generateHikeLetter(EmployeeDTO employee, HikeEntity hike,String issuedDate) throws JRException, IOException {

        JasperReport template1 =hike.getIsPromoted()? JasperCompileManager.
                compileReport(new ClassPathResource("templates/hikeLetterPages/hike-letter-with-promotion.jrxml").
                        getInputStream())
                :
                JasperCompileManager.
                        compileReport(new ClassPathResource("templates/hikeLetterPages/hike-letter.jrxml").
                                getInputStream())
                ;

        JasperReport template2 = JasperCompileManager.compileReport(new ClassPathResource("templates/hikeLetterPages/hike-letter-page-two.jrxml").getInputStream());

        System.err.println("compiled ");


        Map<String, Object> parameters1 = new HashMap<>();
        parameters1.put("employee", employee);
        parameters1.put("hikeDetails", mapper.convertToHikeEntityDto(hike));
        parameters1.put("hikeAmount", formatters.formatAmountWithCommas((hike.getNewSalary() - hike.getPrevSalary())));
        parameters1.put("currentDate", issuedDate);


        CtcCalculator calculator = new CtcCalculator();
        Map<String, Object> parameters2 = new HashMap<>();
        parameters2.put("employee", employee);
        parameters2.put("prevSalaryDetails", calculator.compensationDetails(hike.getPrevSalary()));
        parameters2.put("newSalaryDetails", calculator.compensationDetails(hike.getNewSalary()));
        parameters2.put("updatedDesignation",hike.getIsPromoted()? mapper.convertToHikeEntityDto(hike).getNewPosition():employee.getDesignation());

        JasperPrint jasperPrint1 = JasperFillManager.fillReport(template1, parameters1, new JREmptyDataSource());
        JasperPrint jasperPrint2 = JasperFillManager.fillReport(template2, parameters2, new JREmptyDataSource());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();

        List<JasperPrint> jasperPrints = new ArrayList<>();
        jasperPrints.add(jasperPrint1);
        jasperPrints.add(jasperPrint2);

        exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrints));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

        SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
        configuration.setPdfVersion(PdfVersionEnum.VERSION_1_7);
        configuration.setCreatingBatchModeBookmarks(true);
        configuration.setOverrideHints(true);
        exporter.setConfiguration(configuration);

        exporter.exportReport();

        return outputStream.toByteArray();
    }

    public byte[] generateMergedOfferReport(OfferLetterDTO offerLetterDTO) throws IOException, JRException {
        CtcData data=calculator.compensationDetails(formatters.convertStringToDoubleAmount(offerLetterDTO.getCtc()));

        JasperReport report1 = JasperCompileManager.compileReport(new ClassPathResource("/templates/offerLetterPages/pageone.jrxml").getInputStream());
        JasperReport report2 = JasperCompileManager.compileReport(new ClassPathResource("/templates/offerLetterPages/pagetwo.jrxml").getInputStream());
        JasperReport report3 = JasperCompileManager.compileReport(new ClassPathResource("/templates/offerLetterPages/pagethree.jrxml").getInputStream());
        JasperReport report4 = JasperCompileManager.compileReport(new ClassPathResource("/templates/offerLetterPages/pagefour.jrxml").getInputStream());
        Map<String, Object> paramsForReport= new HashMap<>();
        paramsForReport.put("offer",offerLetterDTO);
        System.out.println(offerLetterDTO.getIssuedDate());
        paramsForReport.put("ctc",data);


        JasperPrint jasperPrint1 = JasperFillManager.fillReport(report1, paramsForReport, new JREmptyDataSource());
        JasperPrint jasperPrint2 = JasperFillManager.fillReport(report2, null, new JREmptyDataSource());
        JasperPrint jasperPrint3 = JasperFillManager.fillReport(report3, null, new JREmptyDataSource());
        JasperPrint jasperPrint4 = JasperFillManager.fillReport(report4, paramsForReport, new JREmptyDataSource());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        List<JasperPrint> jasperPrints = new ArrayList<>();
        jasperPrints.add(jasperPrint1);
        jasperPrints.add(jasperPrint2);
        jasperPrints.add(jasperPrint3);
        jasperPrints.add(jasperPrint4);
        exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrints));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();

        return outputStream.toByteArray();
    }
    public ResponseEntity<byte[]> generatePdfPreviewResponse(byte[] pdfBytes) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("inline").build());
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
