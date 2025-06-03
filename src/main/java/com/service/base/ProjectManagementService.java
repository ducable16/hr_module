package com.service.base;


import com.dto.ProjectDto;
import com.model.Project;
import com.request.ProjectCreateRequest;
import com.request.ProjectUpdateRequest;

import java.util.List;

public interface ProjectManagementService {

    ProjectDto createProject(ProjectCreateRequest request);

    ProjectDto updateProject(Long projectId, ProjectUpdateRequest request);

    void deleteProject(Long projectId);

    List<ProjectDto> getAllProjectsForAdmin();

    List<ProjectDto> getProjectsForEmployee(Long employeeId);

}
