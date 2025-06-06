package com.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeUpdateRequest {
    private Long employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dob;
}
