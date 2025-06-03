package com.service.base;


import com.dto.EmployeeDto;
import com.model.Employee;
import com.request.ChangeRoleRequest;
import com.request.EmployeeCreateRequest;
import com.request.EmployeeUpdateRequest;

public interface EmployeeService {

    EmployeeDto getEmployeeById(Long employeeId);

    EmployeeDto createEmployee(EmployeeCreateRequest request);

    EmployeeDto updateEmployee(Long employeeId, EmployeeUpdateRequest request);

    void deleteEmployee(Long employeeId);

    EmployeeDto changeRole(ChangeRoleRequest request);
}
