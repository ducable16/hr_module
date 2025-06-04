package com.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AssignEmployeeRequest {
    private Long employeeId;
    private Long projectId;
    private Integer workloadPercent;
    private LocalDate startDate;
    private LocalDate endDate;
}