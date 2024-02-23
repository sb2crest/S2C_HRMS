package com.employee.management.DTO;

import lombok.Data;

@Data
public class PfNumberUpdateRequest {
    private String employeeId;
    private String pfNumber;
    private String uanNumber;
}
