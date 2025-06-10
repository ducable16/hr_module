package com.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class EmployeeSearchDto {
    private Long employeeId;
    private String fullName;
    private String email;
}
