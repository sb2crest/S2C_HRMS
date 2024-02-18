package com.employee.management.exception;

import lombok.Getter;

@Getter
public enum ResCodes {

    EMPLOYEE_NOT_FOUND("1000","Employee not found"),
    SALARY_DETAILS_NOT_FOUND("1001","Salary details not found"),
    PAYSLIP_NOT_AVAILABLE("1002","PaySlip not available for this month"),
    INVALID_ID_AND_PASSWORD("1003","Incorrect user id or password"),
    INVALID_EMPLOYEE_DETAILS("1004","Employee data is not complete"),
    INVALID_ROLE("1005","Role details not found"),
    INACTIVE_EMPLOYEE("1006","Inactive Employee"),
    INVALID_STATUS("1007","Status not found"),
    NOT_AUTHORIZED("1008","Employee not permitted")
    ;
    private final String errorCode;
    private final String errorMsg;

    ResCodes(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
