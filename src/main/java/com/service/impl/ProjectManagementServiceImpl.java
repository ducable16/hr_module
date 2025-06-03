package com.service.impl;

import com.dto.ProjectDto;
import com.exception.AccessDeniedException;
import com.exception.EntityNotFoundException;
import com.model.Project;
import com.repository.ProjectRepository;
import com.request.ProjectCreateRequest;
import com.request.ProjectUpdateRequest;
import com.service.base.EmployeeService;
import com.service.base.ProjectManagementService;
import com.util.ProjectCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectManagementServiceImpl implements ProjectManagementService {

    private final ProjectRepository projectRepository;
    private final EmployeeService employeeService;
    private final ProjectCodeGenerator projectCodeGenerator;

    ProjectDto toDto(Project project) {
        return new ProjectDto(project);
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
    public ProjectDto updateProject(Long projectId, ProjectUpdateRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

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
    public List<ProjectDto> getProjectsForEmployee(Long employeeId) {
        return projectRepository.findProjectsByEmployeeId(employeeId).stream()
                .map(this::toDto)
                .toList();
    }

}
