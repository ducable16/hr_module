package com.service.base;


import com.model.dto.ProjectDto;
import com.model.dto.ProjectMemberDto;
import com.model.dto.WorkloadBlockDto;
import com.model.dto.WorkloadRemainDto;
import com.request.*;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface ProjectManagementService {

    ProjectDto createProject(ProjectCreateRequest request);

    ProjectDto updateProject(ProjectUpdateRequest request);

    void deleteProject(Long projectId);

    Page<ProjectDto> getAllProjectsForAdmin(int page, int size);

    List<ProjectDto> getDistinctProjectsForEmployee(Long employeeId);

    void assignEmployeeToProject(AssignEmployeeRequest request);

    List<ProjectDto> getCompletedProjectsForPM(String token);

    List<ProjectDto> getActiveProjectForPM(String token);

    List<ProjectDto> getCurrentProjectsForEmployee(Long employeeId);

    WorkloadRemainDto workloadPercentRemaining(WorkloadRemainCheckRequest request);

    void deleteAssignmentFromProject(Long assignmentId);

    List<ProjectMemberDto> getMembersOfProject(String token, Long projectId);

    void updateAssignment(UpdateAssignmentRequest request);

    List<ProjectMemberDto> getCurrentMembersOfProject(Long projectId);

    List<WorkloadBlockDto> getWorkloadBlocks(Long employeeId, LocalDate from, LocalDate to);

    List<ProjectMemberDto> getPastMembersOfProject(Long projectId);

}
