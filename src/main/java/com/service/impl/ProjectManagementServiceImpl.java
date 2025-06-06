package com.service.impl;

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
import com.service.base.ProjectManagementService;
import com.util.ProjectCodeGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
        if (request.getStartDate() != null) project.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) project.setEndDate(request.getEndDate());
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
    public List<ProjectDto> getAllProjectsForAdmin() {
        return projectRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<ProjectDto> getDistinctProjectsForEmployee(Long employeeId) {
        return projectRepository.findProjectsByEmployeeId(employeeId).stream()
                .map(this::toDto)
                .toList();
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

        if (request.getEndDate().isAfter(project.getEndDate())) {
            throw new IllegalArgumentException("End date must not exceed project end date");
        }

        List<ProjectAssignment> activeAssignments = projectAssignmentRepository
                .findByEmployeeIdAndDateRangeOverlap(
                        request.getEmployeeId(),
                        request.getStartDate(),
                        request.getEndDate()
                );

        int totalExistingWorkload = activeAssignments.stream()
                .mapToInt(ProjectAssignment::getWorkloadPercent)
                .sum();

        int totalAfterAssign = totalExistingWorkload + request.getWorkloadPercent();

        if (totalAfterAssign > 100) {
            throw new IllegalArgumentException("Total workload exceeds 100% for the given period");
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
    public List<ProjectMemberDto> getMembersOfProject(Long projectId) {
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
