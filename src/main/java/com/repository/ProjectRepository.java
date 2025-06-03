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

    @Query("SELECT MAX(CAST(SUBSTRING(p.projectCode, 2) AS int)) FROM Project p")
    Integer findMaxProjectCodeNumber();

}
