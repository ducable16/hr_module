package com.repository;

import com.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("""
    SELECT DISTINCT p FROM Project p
    JOIN ProjectAssignment pa ON pa.projectId = p.id
    WHERE pa.employeeId = :employeeId
""")
    List<Project> findProjectsByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("""
    SELECT DISTINCT p FROM Project p
    JOIN ProjectAssignment pa ON pa.projectId = p.id
    JOIN Employee e ON pa.employeeId = e.id
    WHERE e.employeeCode = :employeeCode
""")
    List<Project> findProjectsByEmployeeCode(@Param("employeeCode") String employeeCode);

    @Query("SELECT MAX(CAST(SUBSTRING(p.projectCode, 2) AS int)) FROM Project p")
    Integer findMaxProjectCodeNumber();

}
