package com.service.impl;

import com.model.Project;
import com.model.ProjectAssignment;
import com.model.dto.EmployeeDto;
import com.exception.EntityNotFoundException;
import com.model.Employee;
import com.enums.Role;
import com.model.dto.EmployeeProjectHistoryDto;
import com.repository.EmployeeRepository;
import com.repository.ProjectAssignmentRepository;
import com.request.ChangeRoleRequest;
import com.request.EmployeeCreateRequest;
import com.request.EmployeeUpdateRequest;
import com.service.base.EmployeeService;
import com.util.EmployeeCodeGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeCodeGenerator employeeCodeGenerator;
    private final ProjectAssignmentRepository projectAssignmentRepository;


    private EmployeeDto toDto(Employee employee) {
        return new EmployeeDto(employee);
    }

    @Override
    public EmployeeDto getEmployeeById(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
        return toDto(employee);
    }

    @Override
    public EmployeeDto createEmployee(EmployeeCreateRequest request) {
        Employee employee = Employee.builder()
                .employeeCode(employeeCodeGenerator.generateNextCode())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(Role.valueOf(request.getRole()))
                .dob(request.getDob())
                .build();
        employeeRepository.save(employee);
        return toDto(employee);
    }

    @Override
    public EmployeeDto updateEmployee(Long employeeId, EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        if(request.getFirstName() != null) {employee.setFirstName(request.getFirstName());};
        if(request.getLastName() != null) {employee.setLastName(request.getLastName());};
        if(request.getEmail() != null) {employee.setEmail(request.getEmail());};
        if(request.getRole() != null) {employee.setRole(Role.valueOf(request.getRole()));}
        if(request.getDob() != null) {employee.setDob(request.getDob());};


        employeeRepository.save(employee);

        return toDto(employee);
    }

    @Override
    public void deleteEmployee(Long employeeId) {
        if(employeeRepository.findById(employeeId).isEmpty()) throw new EntityNotFoundException("Employee not found");
        employeeRepository.deleteById(employeeId);
    }

    @Override
    public EmployeeDto changeRole(ChangeRoleRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        employee.setRole(request.getRole());
        employeeRepository.save(employee);
        return toDto(employee);
    }

    @Override
    public List<EmployeeProjectHistoryDto> getProjectHistoryForEmployee(Long employeeId) {
        List<ProjectAssignment> assignments = projectAssignmentRepository.findByEmployee_Id(employeeId);
        List<EmployeeProjectHistoryDto> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (ProjectAssignment a : assignments) {
            boolean isActive = !today.isBefore(a.getStartDate()) && !today.isAfter(a.getEndDate());
            Project p = a.getProject();
            result.add(new EmployeeProjectHistoryDto(
                    p.getProjectCode(),
                    p.getProjectName(),
                    a.getWorkloadPercent(),
                    a.getStartDate(),
                    a.getEndDate(),
                    isActive
            ));
        }

        return result;
    }

}

