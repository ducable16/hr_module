package com.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectUpdateRequest {
    private String projectName;
    private String pmEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}
