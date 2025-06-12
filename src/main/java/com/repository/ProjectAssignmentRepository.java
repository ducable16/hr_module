package com.repository;

import com.model.ProjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProjectAssignmentRepository extends JpaRepository<ProjectAssignment, Long> {

    Optional<ProjectAssignment> findByAssignmentId(Long assignmentId);

    List<ProjectAssignment> findByEmployeeId(Long employeeId);

    List<ProjectAssignment> findByProjectId(Long projectId);

    boolean existsByProjectIdAndEmployeeId(Long projectId, Long employeeId);

    List<ProjectAssignment> findByEmployeeIdAndProjectId(Long employeeId, Long projectId);

    @Query("""
    SELECT a FROM ProjectAssignment a
    WHERE a.employeeId = :employeeId
      AND a.startDate <= :endDate
      AND (a.endDate IS NULL OR a.endDate >= :startDate)
""")
    List<ProjectAssignment> findByEmployeeIdAndDateRangeOverlap(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT a FROM ProjectAssignment a
    WHERE a.projectId = :projectId
      AND :today BETWEEN a.startDate AND a.endDate
""")
    List<ProjectAssignment> findCurrentMembersOfProject(@Param("projectId") Long projectId,
                                                        @Param("today") LocalDate today);

    @Query("""
    SELECT a FROM ProjectAssignment a
    WHERE a.projectId = :projectId
      AND a.endDate < :today
""")
    List<ProjectAssignment> findPastMembersOfProject(@Param("projectId") Long projectId,
                                                     @Param("today") LocalDate today);

    @Query("""
        SELECT a FROM ProjectAssignment a
        WHERE a.employeeId = :employeeId
          AND a.startDate <= :rangeEnd
          AND (a.endDate IS NULL OR a.endDate >= :rangeStart)
    """)
    List<ProjectAssignment> findOverlappingAssignments(
            @Param("employeeId") Long employeeId,
            @Param("rangeStart") LocalDate rangeStart,
            @Param("rangeEnd") LocalDate rangeEnd
    );

    @Query("""
    SELECT a FROM ProjectAssignment a
    WHERE a.employeeId = :employeeId
      AND (a.endDate IS NULL OR a.endDate >= :startDate)
""")
    List<ProjectAssignment> findActiveOrFutureAssignments(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate
    );

}
