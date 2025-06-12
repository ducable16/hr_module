package com.model.dto;

import com.enums.Role;
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
    private Role role;
}
