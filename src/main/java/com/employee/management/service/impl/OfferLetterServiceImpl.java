package com.employee.management.service.impl;

import com.employee.management.DTO.OfferLetterDTO;
import com.employee.management.converters.Mapper;
import com.employee.management.models.OfferLetterEntity;
import com.employee.management.repository.OfferLetterRepository;
import com.employee.management.service.OfferLetterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OfferLetterServiceImpl implements OfferLetterService {
    @Autowired
    OfferLetterRepository offerLetterRepository;
    @Autowired
    Mapper mapper;

    @Override
    public OfferLetterDTO issueNewOfferLetter(OfferLetterDTO offerLetterDTO){
        OfferLetterEntity offerLetter=mapper.convertToOfferLetterEntity(offerLetterDTO);
        OfferLetterEntity savedOfferLetter = offerLetterRepository.save(offerLetter);
        return mapper.convertToOfferLetterDto(savedOfferLetter);
    }
}
