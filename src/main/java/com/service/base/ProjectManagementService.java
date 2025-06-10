package com.service.base;


import com.model.dto.ProjectDto;
import com.model.dto.ProjectMemberDto;
import com.request.AssignEmployeeRequest;
import com.request.ProjectCreateRequest;
import com.request.ProjectUpdateRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProjectManagementService {

    ProjectDto createProject(ProjectCreateRequest request);

    ProjectDto updateProject(ProjectUpdateRequest request);

    void deleteProject(Long projectId);

    Page<ProjectDto> getAllProjectsForAdmin(int page, int size);

    List<ProjectDto> getProjectsForPM(String token);

    List<ProjectDto> getDistinctProjectsForEmployee(Long employeeId);

    void assignEmployeeToProject(AssignEmployeeRequest request);

    List<ProjectMemberDto> getMembersOfProject(String token, Long projectId);

    List<ProjectMemberDto> getCurrentMembersOfProject(Long projectId);

    List<ProjectMemberDto> getPastMembersOfProject(Long projectId);

}
