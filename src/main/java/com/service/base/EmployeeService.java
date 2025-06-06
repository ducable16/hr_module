package com.service.base;


import com.model.dto.EmployeeDto;
import com.model.dto.EmployeeProjectParticipationDto;
import com.request.ChangeRoleRequest;
import com.request.EmployeeCreateRequest;
import com.request.EmployeeUpdateRequest;

import java.util.List;

public interface EmployeeService {

    EmployeeDto getEmployeeById(Long employeeId);

    EmployeeDto createEmployee(EmployeeCreateRequest request);

    EmployeeDto updateEmployee(EmployeeUpdateRequest request);

    void deleteEmployee(Long employeeId);

    void changeRole(ChangeRoleRequest request);

    List<EmployeeProjectParticipationDto> getProjectParticipationHistory(Long employeeId);

}
