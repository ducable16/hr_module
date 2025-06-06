package com.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class ParticipationPeriodDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer workloadPercent;
}
