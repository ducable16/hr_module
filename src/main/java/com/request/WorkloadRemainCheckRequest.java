package com.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkloadRemainCheckRequest {
    private Long assignmentId;

    private Long employeeId;

    private LocalDate startDate;

    private LocalDate endDate;
}
