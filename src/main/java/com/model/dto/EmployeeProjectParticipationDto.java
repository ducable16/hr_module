package com.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class EmployeeProjectParticipationDto {
    private Long projectId;
    private String projectCode;
    private  String projectName;
    private LocalDate projectStartDate;
    private LocalDate projectEndDate;
    private List<ParticipationPeriodDto> participations;
}
