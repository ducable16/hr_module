package com.controller;

import com.model.dto.ProjectDto;
import com.model.dto.ProjectMemberDto;
import com.request.AssignEmployeeRequest;
import com.request.ProjectCreateRequest;
import com.request.ProjectUpdateRequest;
import com.service.base.ProjectManagementService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
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

    @GetMapping("/project-manager")
    @PreAuthorize("hasRole('PM')")
    public List<ProjectDto> getProjectsForPM(@RequestHeader("Authorization") String token) {
        return projectService.getProjectsForPM(token);
    }



    @GetMapping("/employee/{employeeId}")
    public List<ProjectDto> getProjectsForEmployee(@PathVariable Long employeeId) {
        return projectService.getDistinctProjectsForEmployee(employeeId);
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('PM')")
    public void assignEmployeeToProject(@RequestBody AssignEmployeeRequest request) {
        projectService.assignEmployeeToProject(request);
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

