package com.employee.management.controller;

import com.employee.management.DTO.CtcData;
import com.employee.management.DTO.OfferLetterDTO;
import com.employee.management.service.EmailSenderService;
import com.employee.management.service.OfferLetterService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/offer-letter")
public class OfferLetterController {
    @Autowired
    OfferLetterService offerLetterService;
    @Autowired
    EmailSenderService emailSenderService;
    @PostMapping("/send")
    public ResponseEntity<String> issueOfferLetter(@RequestBody OfferLetterDTO offerLetterDTO) throws JRException, IOException {
        OfferLetterDTO letterDTO=offerLetterService.issueNewOfferLetter(offerLetterDTO);
        try {
            byte[] pdfBytes = offerLetterService.getMergedOfferReport(letterDTO);
            emailSenderService.sendEmailWithAttachment(letterDTO.getEmail(),"Offer and Appointment Letter ","Congratulations",pdfBytes);
            return new ResponseEntity<>("Email sent successfully",HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/preview-compensation-details")
    public ResponseEntity<CtcData> preview(@RequestBody OfferLetterDTO offerLetterDTO){
        return new ResponseEntity<>(offerLetterService.preview(offerLetterDTO.getCtc()),HttpStatus.OK);
    }

    @PostMapping("/preview-letter")
    public ResponseEntity<byte[]> previewLetter(@RequestBody OfferLetterDTO offerLetterDTO) throws JRException, IOException {
        try {
            byte[] pdfBytes = offerLetterService.getMergedOfferReport(offerLetterDTO);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "offerLetter.pdf");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
