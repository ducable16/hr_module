package com.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class WorkloadBlockDto {
    private Long employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int workloadPercent;
}