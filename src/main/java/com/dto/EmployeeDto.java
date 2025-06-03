package com.dto;

import com.enums.Role;
import com.model.Employee;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EmployeeDto {

    private Long id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private LocalDate dob;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EmployeeDto(Employee employee) {
        this.id = employee.getId();
        this.employeeCode = employee.getEmployeeCode();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.email = employee.getEmail();
        this.role = employee.getRole();
        this.dob = employee.getDob();
        this.createdAt = employee.getCreatedAt();
        this.updatedAt = employee.getUpdatedAt();
    }
}