package com.model.dto;

import com.enums.Role;
import com.model.Employee;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EmployeeOnCreateDto {

    private Long employeeId;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private LocalDate dob;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EmployeeOnCreateDto(Employee employee, String rawPassword) {
        this.employeeId = employee.getId();
        this.employeeCode = employee.getEmployeeCode();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.email = employee.getEmail();
        this.password = rawPassword;
        this.dob = employee.getDob();
        this.role = employee.getRole();
        this.createdAt = employee.getCreatedAt();
        this.updatedAt = employee.getUpdatedAt();
    }
}