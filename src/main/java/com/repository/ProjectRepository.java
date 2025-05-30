package com.repository;

import com.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT pa.project FROM ProjectAssignment pa WHERE pa.employee.id = :employeeId")
    List<Project> findProjectsByEmployeeId(@Param("employeeId") Long employeeId);

}
