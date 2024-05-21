package com.employee.management.controller;

import com.employee.management.DTO.CtcData;
import com.employee.management.DTO.OfferLetterDTO;
import com.employee.management.service.PDFService;
import com.employee.management.service.EmailSenderService;
import com.employee.management.service.OfferLetterService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/offer-letter")
@CrossOrigin(origins = "https://hrm-service-fe-16185511.ap-south-1.elb.amazonaws.com/")
public class OfferLetterController {
    @Autowired
    OfferLetterService offerLetterService;
    @Autowired
    EmailSenderService emailSenderService;
    @Autowired
    PDFService pdfService;

    @PostMapping("/send")
    public ResponseEntity<String> issueOfferLetter(@RequestBody OfferLetterDTO offerLetterDTO) {
        try {
            OfferLetterDTO letterDTO = offerLetterService.issueNewOfferLetter(offerLetterDTO);
            byte[] pdfBytes = pdfService.generateMergedOfferReport(letterDTO);
            emailSenderService.sendEmailWithAttachment(letterDTO.getEmail(), "Offer and Appointment Letter ", "Congratulations", pdfBytes);
            return ResponseEntity.status(HttpStatus.CREATED).body("Email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
        }
    }


    @PostMapping("/preview-compensation-details")
    public ResponseEntity<CtcData> preview(@RequestBody OfferLetterDTO offerLetterDTO){
        return new ResponseEntity<>(offerLetterService.preview(offerLetterDTO.getCtc()),HttpStatus.OK);
    }

    @PostMapping("/preview-letter")
    public ResponseEntity<byte[]> previewLetter(@RequestBody OfferLetterDTO offerLetterDTO) throws JRException, IOException {
        byte[] pdfBytes = pdfService.generateMergedOfferReport(offerLetterDTO);
        return pdfService.generatePdfPreviewResponse(pdfBytes);
    }
    @GetMapping("/preview-letter-by-id/{id}")
    public ResponseEntity<byte[]> previewLetterById(@PathVariable("id") Long id) throws JRException, IOException {
        OfferLetterDTO offerLetterDTO=offerLetterService.get(id);
        byte[] pdfBytes = pdfService.generateMergedOfferReport(offerLetterDTO);
          return pdfService.generatePdfPreviewResponse(pdfBytes);
    }


}
