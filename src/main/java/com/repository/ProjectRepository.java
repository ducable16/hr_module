package com.repository;

import com.model.Project;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    WHERE pa.employeeId = :employeeId
    AND (p.endDate IS NULL OR p.endDate > CURRENT_TIMESTAMP)
""")
    List<Project> findCurrentProjectsByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("""
    SELECT p FROM Project p
    WHERE p.pmEmail = :pmEmail
    AND p.endDate < CURRENT_TIMESTAMP
""")
    List<Project> findCompletedProjectsByPmEmail(@Param("pmEmail") String pmEmail);

    @Query("""
    SELECT p FROM Project p
    WHERE p.pmEmail = :pmEmail
    AND (p.endDate IS NULL OR p.endDate > CURRENT_TIMESTAMP)
""")
    List<Project> findActiveProjectsByPmEmail(@Param("pmEmail") String pmEmail);

    @Query("SELECT MAX(CAST(SUBSTRING(p.projectCode, 2) AS int)) FROM Project p")
    Integer findMaxProjectCodeNumber();

    boolean existsByPmEmail(String pmEmail);

}
