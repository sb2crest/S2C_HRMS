package com.employee.management.controller;

import com.employee.management.DTO.OfferLetterDTO;
import com.employee.management.service.OfferLetterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/offer-letter")
public class OfferLetterController {
    @Autowired
    OfferLetterService offerLetterService;
    @PostMapping("/new")
    public ResponseEntity<OfferLetterDTO> issueOfferLetter(@RequestBody OfferLetterDTO offerLetterDTO){
        return new ResponseEntity<>(offerLetterService.issueNewOfferLetter(offerLetterDTO), HttpStatus.OK);
    }

}
