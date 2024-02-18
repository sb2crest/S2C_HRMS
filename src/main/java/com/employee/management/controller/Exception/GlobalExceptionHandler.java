package com.employee.management.controller.Exception;

import com.employee.management.exception.CompanyException;
import com.employee.management.exception.ErrorDetails;
import com.employee.management.exception.ResCodes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CompanyException.class)
    public ResponseEntity<ErrorDetails>companyExceptionHandler(CompanyException companyException){
        ResCodes resCodes=companyException.getResCodes();
        ErrorDetails errorDetails=new ErrorDetails();
        if(!companyException.getEmpId().isEmpty()){
            errorDetails.setErrorDesc(resCodes.getErrorMsg()+"--"+companyException.getEmpId());
        }else{
            errorDetails.setErrorDesc(resCodes.getErrorMsg());
        }
        errorDetails.setErrorCode(resCodes.getErrorCode());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
}
