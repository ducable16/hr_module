package com.request;

import com.enums.Role;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeCreateRequest {
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private LocalDate dob;
}