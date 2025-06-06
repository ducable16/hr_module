package com.service.impl;

import com.model.LoginAccount;
import com.model.Project;
import com.model.ProjectAssignment;
import com.model.dto.EmployeeDto;
import com.exception.EntityNotFoundException;
import com.model.Employee;
import com.model.dto.EmployeeProjectParticipationDto;
import com.model.dto.ParticipationPeriodDto;
import com.repository.EmployeeRepository;
import com.repository.LoginAccountRepository;
import com.repository.ProjectAssignmentRepository;
import com.repository.ProjectRepository;
import com.request.ChangeRoleRequest;
import com.request.EmployeeCreateRequest;
import com.request.EmployeeUpdateRequest;
import com.service.base.EmployeeService;
import com.util.EmployeeCodeGenerator;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeCodeGenerator employeeCodeGenerator;
    private final ProjectAssignmentRepository projectAssignmentRepository;
    private final ProjectRepository projectRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAccountRepository loginAccountRepository;


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
                .dob(request.getDob())
                .build();
        employeeRepository.save(employee);

        LoginAccount loginAccount = LoginAccount.builder()
                .employeeId(employee.getId())
                .email(employee.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        loginAccountRepository.save(loginAccount);
        return toDto(employee);
    }

    @Override
    public EmployeeDto updateEmployee(EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId()).orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        if(request.getFirstName() != null) {employee.setFirstName(request.getFirstName());};
        if(request.getLastName() != null) {employee.setLastName(request.getLastName());};
        if(request.getEmail() != null) {employee.setEmail(request.getEmail());};
        if(request.getDob() != null) {employee.setDob(request.getDob());};

        LoginAccount account = loginAccountRepository.findLoginAccountByEmployeeId(employee.getId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        account.setEmail(employee.getEmail());
        loginAccountRepository.save(account);

        employeeRepository.save(employee);

        return toDto(employee);
    }

    @Override
    public void deleteEmployee(Long employeeId) {
        if(employeeRepository.findById(employeeId).isEmpty()) throw new EntityNotFoundException("Employee not found");
        employeeRepository.deleteById(employeeId);
    }

    @Override
    public void changeRole(ChangeRoleRequest request) {
        LoginAccount account = loginAccountRepository.findLoginAccountByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        account.setRole(request.getRole());
        loginAccountRepository.save(account);
    }

    @Override
    public List<EmployeeProjectParticipationDto> getProjectParticipationHistory(Long employeeId) {
        List<ProjectAssignment> assignments = projectAssignmentRepository.findByEmployeeId(employeeId);

        Set<Long> projectIds = new HashSet<>();
        for (ProjectAssignment assignment : assignments) {
            projectIds.add(assignment.getProjectId());
        }

        List<Project> projectList = projectRepository.findAllById(projectIds);
        Map<Long, Project> projectMap = new HashMap<>();
        for (Project project : projectList) {
            projectMap.put(project.getId(), project);
        }

        Map<Long, EmployeeProjectParticipationDto> dtoMap = new LinkedHashMap<>();

        for (ProjectAssignment assignment : assignments) {
            Long projectId = assignment.getProjectId();
            Project project = projectMap.get(projectId);

            if (!dtoMap.containsKey(projectId)) {
                dtoMap.put(projectId, EmployeeProjectParticipationDto.builder()
                        .projectId(projectId)
                        .projectName(project.getProjectName())
                        .projectCode(project.getProjectCode())
                        .projectStartDate(project.getStartDate())
                        .projectEndDate(project.getEndDate())
                        .participations(new ArrayList<>())
                        .build()
                );
            }

            EmployeeProjectParticipationDto dto = dtoMap.get(projectId);
            dto.getParticipations().add(ParticipationPeriodDto.builder()
                        .startDate(assignment.getStartDate())
                        .endDate(assignment.getEndDate())
                        .workloadPercent(assignment.getWorkloadPercent())
                        .build()
            );
        }
        return new ArrayList<>(dtoMap.values());
    }



}

