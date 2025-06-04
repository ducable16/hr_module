package com.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectMemberDto {
    private String employeeCode;
    private String fullName;
    private Integer workloadPercent;
    private LocalDate startDate;
    private LocalDate endDate;

    public ProjectMemberDto(String employeeCode, String fullName, Integer workload, LocalDate start, LocalDate end) {
        this.employeeCode = employeeCode;
        this.fullName = fullName;
        this.workloadPercent = workload;
        this.startDate = start;
        this.endDate = end;
    }
}
