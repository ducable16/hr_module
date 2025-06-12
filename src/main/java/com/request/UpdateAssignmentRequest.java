package com.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateAssignmentRequest {
    private Long assignmentId;
    private Integer workloadPercent;
    private LocalDate startDate;
    private LocalDate endDate;
}
