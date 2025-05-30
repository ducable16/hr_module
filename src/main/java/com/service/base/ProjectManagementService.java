package com.service.base;


import com.model.Project;
import java.util.List;

public interface ProjectManagementService {

    Project createProject(Project project, Long creatorId);

    Project updateProject(Long projectId, Project updatedProject, Long updaterId);

    void deleteProject(Long projectId, Long requesterId);

    List<Project> getAllProjectsForAdmin();

    List<Project> getProjectsForUser(Long userId);
}
