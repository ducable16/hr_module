package com.service.base;


import com.model.dto.ProjectDto;
import com.model.dto.ProjectMemberDto;
import com.request.AssignEmployeeRequest;
import com.request.ProjectCreateRequest;
import com.request.ProjectUpdateRequest;

import java.util.List;

public interface ProjectManagementService {

    ProjectDto createProject(ProjectCreateRequest request);

    ProjectDto updateProject(ProjectUpdateRequest request);

    void deleteProject(Long projectId);

    List<ProjectDto> getAllProjectsForAdmin();

    List<ProjectDto> getDistinctProjectsForEmployee(Long employeeId);

    void assignEmployeeToProject(AssignEmployeeRequest request);

    List<ProjectMemberDto> getMembersOfProject(Long projectId);

    List<ProjectMemberDto> getCurrentMembersOfProject(Long projectId);

    List<ProjectMemberDto> getPastMembersOfProject(Long projectId);

}
