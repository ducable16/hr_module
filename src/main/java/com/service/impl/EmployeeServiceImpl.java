package com.service.impl;

import com.config.ValueConfig;
import com.enums.Role;
import com.exception.AccessDeniedException;
import com.exception.BadRequestException;
import com.model.LoginAccount;
import com.model.Project;
import com.model.ProjectAssignment;
import com.model.dto.*;
import com.exception.EntityNotFoundException;
import com.model.Employee;
import com.repository.EmployeeRepository;
import com.repository.LoginAccountRepository;
import com.repository.ProjectAssignmentRepository;
import com.repository.ProjectRepository;
import com.request.ChangeRoleRequest;
import com.request.EmployeeCreateRequest;
import com.request.EmployeeUpdateRequest;
import com.service.JwtService;
import com.service.base.EmployeeService;
import com.util.EmployeeCodeGenerator;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final JwtService jwtService;
    private final ValueConfig valueConfig;

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
    public Page<EmployeeDto> getAllEmployeesForAdmin(Role role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("role").ascending());
        Page<Employee> employees;

        if (role == null) {
            employees = employeeRepository.findAll(pageable);
        } else {
            employees = employeeRepository.findAllByRole(role, pageable);
        }
        return employees.map(this::toDto);
    }

    @Override
    public EmployeeDto getEmployeeByEmail(String token) {
        String email = jwtService.extractUsername(token);
        Employee employee = employeeRepository.findEmployeeByEmail(email).orElseThrow(() -> new EntityNotFoundException("Employee not found"));
        return toDto(employee);
    }

    @Override
    public EmployeeOnCreateDto createEmployee(EmployeeCreateRequest request) {
        Employee employee = Employee.builder()
                .employeeCode(employeeCodeGenerator.generateNextCode())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .role(request.getRole())
                .dob(request.getDob())
                .build();
        employeeRepository.save(employee);

        LoginAccount loginAccount = LoginAccount.builder()
                .employeeId(employee.getId())
                .email(employee.getEmail())
                .password(passwordEncoder.encode(valueConfig.getDefaultRawPassword()))
                .role(request.getRole())
                .build();

        loginAccountRepository.save(loginAccount);

        return new EmployeeOnCreateDto(employee, valueConfig.getDefaultRawPassword());
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
    @Transactional
    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new EntityNotFoundException("PM not found"));
        if(employee.getRole() == Role.PM && projectRepository.existsByPmEmail(employee.getEmail())) {
            throw new BadRequestException("PM still have been assigned to project");
        }
        employeeRepository.deleteById(employeeId);
    }

    @Override
    public void changeRole(ChangeRoleRequest request) {
        LoginAccount account = loginAccountRepository.findLoginAccountByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        account.setRole(request.getRole());
        loginAccountRepository.save(account);
        Employee employee = employeeRepository.findById(request.getEmployeeId()).orElseThrow(() -> new EntityNotFoundException("Employee not found"));
        employee.setRole(request.getRole());
        employeeRepository.save(employee);
    }

    @Override
    public List<EmployeeProjectParticipationDto> getProjectParticipationHistory(String token, Long employeeId) {
        if(!jwtService.extractEmployeeId(token).equals(employeeId) && !jwtService.extractRole(token).equals(Role.ADMIN)) {
            throw new AccessDeniedException("Access denied");
        };
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

    @Override
    public List<EmployeeSearchDto> searchByEmail(Role role, String emailFragment) {
        if (emailFragment == null || emailFragment.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Employee> employees = new ArrayList<>();
        emailFragment = emailFragment.trim();
        if(role != null) {
            employees = employeeRepository.findTop10ByEmailAndRole(emailFragment, role, PageRequest.of(0, 10));
        }
        else employees = employeeRepository.findTop10ByEmailLike(emailFragment, PageRequest.of(0, 10));


        List<EmployeeSearchDto> dtos = new ArrayList<>();
        for(Employee employee : employees) {
            EmployeeSearchDto e = EmployeeSearchDto.builder()
                    .employeeId(employee.getId())
                    .email(employee.getEmail())
                    .fullName(employee.getFirstName() + " " + employee.getLastName())
                    .role(employee.getRole())
                    .build();
            dtos.add(e);
        }
        return dtos;
    }
    @Override
    public List<ParticipationPeriodDto> getParticipationPeriod(Long projectId, Long employeeId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException("Project not found"));
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new EntityNotFoundException("Employee not found"));
        List<ProjectAssignment> assignments = projectAssignmentRepository.findByEmployeeIdAndProjectId(employeeId, projectId);
        List<ParticipationPeriodDto> dtos = new ArrayList<>();
        for (ProjectAssignment assignment : assignments) {

            ParticipationPeriodDto dto = ParticipationPeriodDto.builder()
                    .assignmentId(assignment.getAssignmentId())
                    .startDate(assignment.getStartDate())
                    .endDate(assignment.getEndDate())
                    .workloadPercent(assignment.getWorkloadPercent())
                    .build();

            dtos.add(dto);
        }
        return dtos;
    }


}

