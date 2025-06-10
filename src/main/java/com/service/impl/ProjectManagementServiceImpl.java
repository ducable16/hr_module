package com.service.impl;

import com.enums.Role;
import com.exception.AccessDeniedException;
import com.model.dto.ProjectDto;
import com.model.dto.ProjectMemberDto;
import com.exception.EntityNotFoundException;
import com.model.Employee;
import com.model.Project;
import com.model.ProjectAssignment;
import com.repository.EmployeeRepository;
import com.repository.ProjectAssignmentRepository;
import com.repository.ProjectRepository;
import com.request.AssignEmployeeRequest;
import com.request.ProjectCreateRequest;
import com.request.ProjectUpdateRequest;
import com.service.JwtService;
import com.service.base.ProjectManagementService;
import com.util.ProjectCodeGenerator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.exception.IllegalArgumentException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ProjectManagementServiceImpl implements ProjectManagementService {

    private final ProjectRepository projectRepository;

    private final EmployeeRepository employeeRepository;

    private final ProjectAssignmentRepository projectAssignmentRepository;

    private final ProjectCodeGenerator projectCodeGenerator;

    private final JwtService jwtService;

    ProjectDto toDto(Project project) {
        return new ProjectDto(project);
    }

    private List<ProjectMemberDto> toProjectMemberDtoList(List<ProjectAssignment> assignments) {
        List<ProjectMemberDto> result = new ArrayList<>();
        for (ProjectAssignment a : assignments) {
            Employee employee = employeeRepository.findEmployeeById(a.getEmployeeId()).orElseThrow(() -> new EntityNotFoundException("Employee not found"));
            String fullName = employee.getFirstName() + " " + employee.getLastName();
            result.add(new ProjectMemberDto(
                    employee.getEmployeeCode(),
                    fullName,
                    employee.getEmail(),
                    employee.getRole(),
                    a.getWorkloadPercent(),
                    a.getStartDate(),
                    a.getEndDate()
            ));
        }
        return result;
    }

    @Override
    public ProjectDto createProject(ProjectCreateRequest request) {
        Project project = Project.builder()
                .projectCode(projectCodeGenerator.generateNextCode())
                .projectName(request.getProjectName())
                .pmEmail(request.getPmEmail())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .description(request.getDescription())
                .build();

        projectRepository.save(project);
        return toDto(project);
    }

    @Override
    public ProjectDto updateProject(ProjectUpdateRequest request) {
        Project project = projectRepository.findById(request.getProjectId()).orElseThrow(() -> new EntityNotFoundException("Project not found"));

        if (request.getProjectName() != null) project.setProjectName(request.getProjectName());
        if (request.getPmEmail() != null) project.setPmEmail(request.getPmEmail());
        if (request.getStartDate() != null) {
            if(project.getStartDate().isBefore(request.getEndDate())) {
                List<ProjectAssignment> assignments = projectAssignmentRepository.findByProjectId(request.getProjectId());
                for (ProjectAssignment a : assignments) {
                    if(a.getStartDate().isBefore(request.getStartDate())) {
                        a.setStartDate(request.getStartDate());
                        projectAssignmentRepository.save(a);
                    }
                }
            }
            project.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            if(project.getEndDate() == null) {
                List<ProjectAssignment> assignments = projectAssignmentRepository.findByProjectId(request.getProjectId());
                for (ProjectAssignment a : assignments) {
                    if(a.getEndDate() == null || a.getEndDate().isAfter(request.getEndDate())) {
                        a.setEndDate(request.getEndDate());
                    }
                    projectAssignmentRepository.save(a);
                }
            }
            project.setEndDate(request.getEndDate());
        }
        if (request.getDescription() != null) project.setDescription(request.getDescription());

        projectRepository.save(project);
        return toDto(project);
    }

    @Override
    public void deleteProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project not found");
        }
        projectRepository.deleteById(projectId);
    }

    @Override
    public Page<ProjectDto> getAllProjectsForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return projectRepository.findAll(pageable)
                .map(this::toDto);
    }

    @Override
    public List<ProjectDto> getProjectsForPM(String token) {
        String pmEmail = jwtService.extractUsername(token);
        return projectRepository.findProjectByPmEmail(pmEmail).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<ProjectDto> getDistinctProjectsForEmployee(Long employeeId) {
        return projectRepository.findProjectsByEmployeeId(employeeId).stream()
                .map(this::toDto)
                .toList();
    }
    public static boolean isOverlapping(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !(end1.isBefore(start2) || end2.isBefore(start1));
    }

    @Override
    public void assignEmployeeToProject(AssignEmployeeRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        if (request.getWorkloadPercent() < 0 || request.getWorkloadPercent() > 100) {
            throw new IllegalArgumentException("Workload must be between 0 and 100");
        }
        if (request.getStartDate().isBefore(project.getStartDate())) {
            throw new IllegalArgumentException("Start date must not before project start date");
        }

        if (project.getEndDate() != null && request.getEndDate() != null && request.getEndDate().isAfter(project.getEndDate())) {
            throw new IllegalArgumentException("End date must not exceed project end date");
        }
        if(request.getEndDate() == null && project.getEndDate() != null) {
            request.setEndDate(project.getEndDate());
        }

        List<ProjectAssignment> activeAssignments = projectAssignmentRepository
                .findByEmployeeIdAndDateRangeOverlap(
                        request.getEmployeeId(),
                        request.getStartDate(),
                        request.getEndDate() == null ? LocalDate.now() : request.getEndDate()
                );

        int totalExistingWorkload = activeAssignments.stream()
                .mapToInt(ProjectAssignment::getWorkloadPercent)
                .sum();

        int totalAfterAssign = totalExistingWorkload + request.getWorkloadPercent();

        if (totalAfterAssign > 100) {
            throw new IllegalArgumentException("Total workload exceeds 100% for the given period");
        }
        List<ProjectAssignment> list =  projectAssignmentRepository.findByEmployeeIdAndProjectId(request.getEmployeeId(), request.getProjectId());
        for (ProjectAssignment a : list) {
            if(a.getEndDate() == null) {
                if(request.getStartDate().isAfter(a.getStartDate()) || (request.getEndDate() != null && request.getEndDate().isAfter(a.getStartDate()))) {
                    throw new IllegalArgumentException("Participation periods must not overlap");
                }
            }
            else {
                if(isOverlapping(a.getStartDate(), a.getEndDate(), request.getStartDate(), request.getEndDate())) {
                    throw new IllegalArgumentException("Participation periods must not overlap");
                }
            }
        }


        ProjectAssignment assignment = ProjectAssignment.builder()
                .employeeId(employee.getId())
                .projectId(project.getId())
                .workloadPercent(request.getWorkloadPercent())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        projectAssignmentRepository.save(assignment);
    }

    @Override
    public List<ProjectMemberDto> getMembersOfProject(String token, Long projectId) {
        Role role = jwtService.extractRole(token);
        if(!role.equals(Role.ADMIN)) {
            Long employeeId = jwtService.extractEmployeeId(token);
            String pmEmail = jwtService.extractUsername(token);
            if(!projectAssignmentRepository.existsByProjectIdAndEmployeeId(projectId, employeeId) && !projectRepository.existsByIdAndPmEmail(projectId, pmEmail)) {
                throw new AccessDeniedException("You do not have permission to access this project");
            }
        }
        List<ProjectAssignment> assignments = projectAssignmentRepository.findByProjectId(projectId);
        return toProjectMemberDtoList(assignments);
    }

    @Override
    public List<ProjectMemberDto> getCurrentMembersOfProject(Long projectId) {
        List<ProjectAssignment> assignments = projectAssignmentRepository.findCurrentMembersOfProject(projectId, LocalDate.now());
        return toProjectMemberDtoList(assignments);
    }

    @Override
    public List<ProjectMemberDto> getPastMembersOfProject(Long projectId) {
        List<ProjectAssignment> assignments = projectAssignmentRepository.findPastMembersOfProject(projectId, LocalDate.now());
        return toProjectMemberDtoList(assignments);
    }


}
