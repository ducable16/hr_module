package com.controller;

import com.model.dto.ProjectDto;
import com.model.dto.ProjectMemberDto;
import com.model.dto.WorkloadRemainDto;
import com.request.*;
import com.service.base.ProjectManagementService;
import com.service.impl.ProjectManagementServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
@AllArgsConstructor
public class ProjectManagementController {

    private final ProjectManagementService projectService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProjectDto createProject(@RequestBody ProjectCreateRequest request) {
        return projectService.createProject(request);
    }

    @PutMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ProjectDto updateProject(@RequestBody ProjectUpdateRequest request) {
        return projectService.updateProject(request);
    }

    @DeleteMapping("/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<ProjectDto> getAllProjectsForAdmin(@RequestParam int page, @RequestParam int size) {
        return projectService.getAllProjectsForAdmin(page, size);
    }

    @GetMapping("/project-manager/completed")
    @PreAuthorize("hasRole('PM')")
    public List<ProjectDto> getCompletedProjectsForPM(@RequestHeader("Authorization") String token) {
        return projectService.getCompletedProjectsForPM(token);
    }

    @GetMapping("/project-manager/current")
    @PreAuthorize("hasRole('PM')")
    public List<ProjectDto> getActiveProjectsForPM(@RequestHeader("Authorization") String token) {
        return projectService.getActiveProjectForPM(token);
    }

    @GetMapping("/current/{employeeId}")
    public List<ProjectDto> getCurrentProjectsForEmployee(@PathVariable Long employeeId) {
        return projectService.getCurrentProjectsForEmployee(employeeId);
    }
    @GetMapping("/{employeeId}")
    public List<ProjectDto> getAllProjectsAssignForEmployee(@PathVariable Long employeeId) {
        return projectService.getDistinctProjectsForEmployee(employeeId);
    }


    @PostMapping("/assign")
    @PreAuthorize("hasRole('PM')")
    public void assignEmployeeToProject(@RequestBody AssignEmployeeRequest request) {
        projectService.assignEmployeeToProject(request);
    }

    @PostMapping("/workload-check")
    public WorkloadRemainDto workloadPercentRemaining(@RequestBody WorkloadRemainCheckRequest request) {
        return projectService.workloadPercentRemaining(request);
    }

    @DeleteMapping("/assignment/{assignmentId}")
    @PreAuthorize("hasRole('PM')")
    public void deleteAssignmentFromProject(@PathVariable Long assignmentId) {
        projectService.deleteAssignmentFromProject(assignmentId);
    }

    @PutMapping("/update-assignment")
    public void updateAssignment(@RequestBody UpdateAssignmentRequest request) {
        projectService.updateAssignment(request);
    }


    @GetMapping("/{projectId}/members")
    public List<ProjectMemberDto> getProjectMembers(@RequestHeader("Authorization") String token, @PathVariable Long projectId) {
        return projectService.getMembersOfProject(token, projectId);
    }

    @PreAuthorize("hasRole('PM')")
    @GetMapping("/{projectId}/members/current")
    public List<ProjectMemberDto> getCurrentProjectMembers(@PathVariable Long projectId) {
        return projectService.getCurrentMembersOfProject(projectId);
    }

    @PreAuthorize("hasRole('PM')")
    @GetMapping("/{projectId}/members/completed")
    public List<ProjectMemberDto> getPastProjectMembers(@PathVariable Long projectId) {
        return projectService.getPastMembersOfProject(projectId);
    }



}

