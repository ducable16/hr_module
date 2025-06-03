package com.request;

import com.enums.Role;
import lombok.Data;

@Data
public class ChangeRoleRequest {
    private Long employeeId;
    private Role role;
}
