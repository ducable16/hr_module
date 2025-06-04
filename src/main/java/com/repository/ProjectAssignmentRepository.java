package com.repository;

import com.model.ProjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProjectAssignmentRepository extends JpaRepository<ProjectAssignment, Long> {

    List<ProjectAssignment> findByEmployee_Id(Long employeeId);

    List<ProjectAssignment> findByProject_Id(Long projectId);

    @Query("""
    SELECT a FROM ProjectAssignment a 
    WHERE a.employee.id = :employeeId 
      AND a.startDate <= :endDate 
      AND a.endDate >= :startDate
""")
    List<ProjectAssignment> findByEmployeeIdAndDateRangeOverlap(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT a FROM ProjectAssignment a
    WHERE a.project.id = :projectId
      AND :today BETWEEN a.startDate AND a.endDate
""")
    List<ProjectAssignment> findCurrentMembersOfProject(@Param("projectId") Long projectId,
                                                        @Param("today") LocalDate today);

    @Query("""
    SELECT a FROM ProjectAssignment a
    WHERE a.project.id = :projectId
      AND a.endDate < :today
""")
    List<ProjectAssignment> findPastMembersOfProject(@Param("projectId") Long projectId,
                                                     @Param("today") LocalDate today);

}
