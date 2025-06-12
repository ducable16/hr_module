package com.service.base;


import com.enums.Role;
import com.model.dto.*;
import com.request.ChangeRoleRequest;
import com.request.EmployeeCreateRequest;
import com.request.EmployeeUpdateRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EmployeeService {

    EmployeeDto getEmployeeById(Long employeeId);

    Page<EmployeeDto> getAllEmployeesForAdmin(Role role, int page, int size);

    EmployeeDto getEmployeeByEmail(String email);

    EmployeeOnCreateDto createEmployee(EmployeeCreateRequest request);

    EmployeeDto updateEmployee(EmployeeUpdateRequest request);

    void deleteEmployee(Long employeeId);

    void changeRole(ChangeRoleRequest request);

    List<EmployeeProjectParticipationDto> getProjectParticipationHistory(String token, Long employeeId);

    List<EmployeeSearchDto> searchByEmail(Role role, String emailFragment);

    List<ParticipationPeriodDto> getParticipationPeriod(Long projectId, Long employeeId);

}
