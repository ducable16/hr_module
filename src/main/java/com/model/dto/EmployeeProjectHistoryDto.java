package com.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@AllArgsConstructor
@Data
public class EmployeeProjectHistoryDto {
    private String projectCode;
    private String projectName;
    private Integer workloadPercent;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
}