package com.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class ParticipationPeriodDto {
    private Long assignmentId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer workloadPercent;
}
