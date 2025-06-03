package com.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeUpdateRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private LocalDate dob;
}
