package com.model.dto;

import com.enums.Role;
import com.model.Employee;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EmployeeDto {

    private Long employeeId;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dob;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EmployeeDto(Employee employee) {
        this.employeeId = employee.getId();
        this.employeeCode = employee.getEmployeeCode();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.email = employee.getEmail();
        this.dob = employee.getDob();
        this.createdAt = employee.getCreatedAt();
        this.updatedAt = employee.getUpdatedAt();
    }
}