package com.model.dto;

import com.enums.Role;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectMemberDto {
    private String employeeCode;
    private String fullName;
    private String email;
    private Role role;
    private Integer workloadPercent;
    private LocalDate startDate;
    private LocalDate endDate;

    public ProjectMemberDto(String employeeCode, String fullName, String email, Role role, Integer workload, LocalDate start, LocalDate end) {
        this.employeeCode = employeeCode;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.workloadPercent = workload;
        this.startDate = start;
        this.endDate = end;
    }
}
