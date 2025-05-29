package com.repository;

import com.model.ProjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectAssignmentRepository extends JpaRepository<ProjectAssignment, Long> {
    List<ProjectAssignment> findByEmployee_Id(Long employeeId);
    List<ProjectAssignment> findByProject_Id(Long projectId);
}
