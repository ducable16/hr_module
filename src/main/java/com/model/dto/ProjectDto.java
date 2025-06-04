package com.model.dto;

import com.model.Project;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProjectDto {

    private String projectCode;
    private String projectName;
    private String pmEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProjectDto(Project project) {
        this.projectCode = project.getProjectCode();
        this.projectName = project.getProjectName();
        this.pmEmail = project.getPmEmail();
        this.startDate = project.getStartDate();
        this.endDate = project.getEndDate();
        this.description = project.getDescription();
        this.createdAt = project.getCreatedAt();
        this.updatedAt = project.getUpdatedAt();
    }
}
