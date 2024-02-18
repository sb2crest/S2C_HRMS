package com.employee.management.exception;

import lombok.Getter;

@Getter
public class CompanyException extends RuntimeException{
    ResCodes resCodes;
    public CompanyException(ResCodes resCode){
        super(resCode.getErrorCode()+"-"+resCode.getErrorMsg());
        this.resCodes=resCode;
    }
}
