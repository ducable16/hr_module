package com.controller;

import com.model.dto.ProjectDto;
import com.model.dto.ProjectMemberDto;
import com.request.AssignEmployeeRequest;
import com.request.ProjectCreateRequest;
import com.request.ProjectUpdateRequest;
import com.service.base.ProjectManagementService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/project")
@AllArgsConstructor
public class ProjectManagementController {

    private final ProjectManagementService projectService;

    @PostMapping
    public ProjectDto createProject(@RequestBody ProjectCreateRequest request) {
        return projectService.createProject(request);
    }

    @PutMapping("/{projectId}")
    public ProjectDto updateProject(@PathVariable Long projectId,
                                    @RequestBody ProjectUpdateRequest request) {
        return projectService.updateProject(projectId, request);
    }

    @DeleteMapping("/{projectId}")
    public void deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
    }

    @GetMapping("/admin")
    public List<ProjectDto> getAllProjectsForAdmin() {
        return projectService.getAllProjectsForAdmin();
    }

    @GetMapping("/employee/{employeeId}")
    public List<ProjectDto> getProjectsForEmployee(@PathVariable Long employeeId) {
        return projectService.getProjectsForEmployee(employeeId);
    }

    @PostMapping("/assign")
    public void assignEmployeeToProject(@RequestBody AssignEmployeeRequest request) {
        projectService.assignEmployeeToProject(request);
    }
    @GetMapping("/{projectId}/members")
    public List<ProjectMemberDto> getProjectMembers(@PathVariable Long projectId) {
        return projectService.getMembersOfProject(projectId);
    }

    @GetMapping("/{projectId}/members/current")
    public List<ProjectMemberDto> getCurrentProjectMembers(@PathVariable Long projectId) {
        return projectService.getCurrentMembersOfProject(projectId);
    }

    @GetMapping("/{projectId}/members/completed")
    public List<ProjectMemberDto> getPastProjectMembers(@PathVariable Long projectId) {
        return projectService.getPastMembersOfProject(projectId);
    }
}

