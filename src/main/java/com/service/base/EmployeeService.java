package com.service.base;


import com.model.dto.EmployeeDto;
import com.model.dto.EmployeeProjectHistoryDto;
import com.request.ChangeRoleRequest;
import com.request.EmployeeCreateRequest;
import com.request.EmployeeUpdateRequest;

import java.util.List;

public interface EmployeeService {

    EmployeeDto getEmployeeById(Long employeeId);

    EmployeeDto createEmployee(EmployeeCreateRequest request);

    EmployeeDto updateEmployee(Long employeeId, EmployeeUpdateRequest request);

    void deleteEmployee(Long employeeId);

    EmployeeDto changeRole(ChangeRoleRequest request);

    List<EmployeeProjectHistoryDto> getProjectHistoryForEmployee(Long employeeId);

}
