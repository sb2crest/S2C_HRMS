package com.employee.management.DTO;

import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link com.employee.management.models.Leaves}
 */
@Value
@Data
public class LeavesDTO implements Serializable {
    String leaveType;
    Date leaveStartDate;
    Date leaveEndDate;
    String status;
    String reason;
    String employeeId;
}