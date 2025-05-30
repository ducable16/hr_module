package com.service.impl;

import com.exception.AccessDeniedException;
import com.model.Project;
import com.repository.ProjectRepository;
import com.service.base.EmployeeService;
import com.service.base.ProjectManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectManagementServiceImpl implements ProjectManagementService {

    private final ProjectRepository projectRepository;
    private final EmployeeService employeeService;

    @Override
    public Project createProject(Project project, Long creatorId) {
        if (!employeeService.isAdmin(creatorId)) {
            throw new AccessDeniedException("Only admin can create project");
        }
        return projectRepository.save(project);
    }

    @Override
    public Project updateProject(Long projectId, Project updatedProject, Long updaterId) {
        if (!employeeService.isAdmin(updaterId)) {
            throw new AccessDeniedException("Only admin can update project");
        }
        updatedProject.setId(projectId);
        return projectRepository.save(updatedProject);
    }

    @Override
    public void deleteProject(Long projectId, Long requesterId) {
        if (!employeeService.isAdmin(requesterId)) {
            throw new AccessDeniedException("Only admin can delete project");
        }
        projectRepository.deleteById(projectId);
    }

    @Override
    public List<Project> getAllProjectsForAdmin() {
        return projectRepository.findAll();
    }

    @Override
    public List<Project> getProjectsForUser(Long userId) {
        return projectRepository.findProjectsByEmployeeId(userId);
    }
}
