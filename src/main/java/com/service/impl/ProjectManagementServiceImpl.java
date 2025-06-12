package com.service.impl;

import com.enums.Role;
import com.exception.AccessDeniedException;
import com.exception.BadRequestException;
import com.model.dto.ProjectDto;
import com.model.dto.ProjectMemberDto;
import com.exception.EntityNotFoundException;
import com.model.Employee;
import com.model.Project;
import com.model.ProjectAssignment;
import com.model.dto.WorkloadBlockDto;
import com.model.dto.WorkloadRemainDto;
import com.repository.EmployeeRepository;
import com.repository.ProjectAssignmentRepository;
import com.repository.ProjectRepository;
import com.request.*;
import com.service.JwtService;
import com.service.base.ProjectManagementService;
import com.util.ProjectCodeGenerator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.exception.IllegalArgumentException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@AllArgsConstructor
public class ProjectManagementServiceImpl implements ProjectManagementService {

    private final ProjectRepository projectRepository;

    private final EmployeeRepository employeeRepository;

    private final ProjectAssignmentRepository projectAssignmentRepository;

    private final ProjectCodeGenerator projectCodeGenerator;

    private final JwtService jwtService;

    ProjectDto toDto(Project project) {
        return new ProjectDto(project);
    }

    private List<ProjectMemberDto> toProjectMemberDtoList(List<ProjectAssignment> assignments) {
        List<ProjectMemberDto> result = new ArrayList<>();
        for (ProjectAssignment a : assignments) {
            Employee employee = employeeRepository.findEmployeeById(a.getEmployeeId()).orElseThrow(() -> new EntityNotFoundException("Employee not found"));
            String fullName = employee.getFirstName() + " " + employee.getLastName();
            result.add(new ProjectMemberDto(
                    a.getAssignmentId(),
                    employee.getId(),
                    employee.getEmployeeCode(),
                    fullName,
                    employee.getEmail(),
                    employee.getRole(),
                    a.getWorkloadPercent(),
                    a.getStartDate(),
                    a.getEndDate()
            ));
        }
        return result;
    }

    @Override
    public ProjectDto createProject(ProjectCreateRequest request) {
        Project project = Project.builder()
                .projectCode(projectCodeGenerator.generateNextCode())
                .projectName(request.getProjectName())
                .pmEmail(request.getPmEmail())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .description(request.getDescription())
                .build();

        projectRepository.save(project);

        if(request.getPmEmail() != null) {
            Employee employee = employeeRepository.findEmployeeByEmail(request.getPmEmail()).orElseThrow(() -> new EntityNotFoundException("PM not found"));
            ProjectAssignment assignment = ProjectAssignment.builder()
                    .employeeId(employee.getId())
                    .projectId(project.getId())
                    .startDate(project.getStartDate())
                    .endDate(project.getEndDate())
                    .build();

            projectAssignmentRepository.save(assignment);
        }
        return toDto(project);
    }

    @Override
    public ProjectDto updateProject(ProjectUpdateRequest request) {
        Project project = projectRepository.findById(request.getProjectId()).orElseThrow(() -> new EntityNotFoundException("Project not found"));

        if (request.getProjectName() != null) project.setProjectName(request.getProjectName());
        if (!request.getPmEmail().equals(project.getPmEmail())) {
            Employee pm = employeeRepository.findEmployeeByEmail(project.getPmEmail()).orElseThrow(() -> new EntityNotFoundException("Cannot find current PM"));
            List<ProjectAssignment> list = projectAssignmentRepository.findByEmployeeIdAndProjectId(pm.getId(), project.getId());
            for (ProjectAssignment a : list) {
                if (a.getEndDate() == null || a.getEndDate().isAfter(LocalDate.now())) {
                    a.setEndDate(LocalDate.now());
                }
            }
            project.setPmEmail(request.getPmEmail());
        }
        if (request.getStartDate() != null) {
            if(project.getStartDate().isBefore(request.getStartDate())) {
                List<ProjectAssignment> assignments = projectAssignmentRepository.findByProjectId(request.getProjectId());
                for (ProjectAssignment a : assignments) {
                    if(a.getStartDate().isBefore(request.getStartDate())) {
                        a.setStartDate(request.getStartDate());
                        projectAssignmentRepository.save(a);
                    }
                }
            }
            project.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            if(project.getEndDate() == null) {
                List<ProjectAssignment> assignments = projectAssignmentRepository.findByProjectId(request.getProjectId());
                for (ProjectAssignment a : assignments) {
                    if(a.getEndDate() == null || a.getEndDate().isAfter(request.getEndDate())) {
                        a.setEndDate(request.getEndDate());
                    }
                    projectAssignmentRepository.save(a);
                }
            }
            project.setEndDate(request.getEndDate());
        }
        if (request.getDescription() != null) project.setDescription(request.getDescription());

        projectRepository.save(project);
        return toDto(project);
    }

    @Override
    public void deleteProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project not found");
        }
        projectRepository.deleteById(projectId);
    }

    @Override
    public Page<ProjectDto> getAllProjectsForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return projectRepository.findAll(pageable)
                .map(this::toDto);
    }

    @Override
    public List<ProjectDto> getCompletedProjectsForPM(String token) {
        String pmEmail = jwtService.extractUsername(token);
        return projectRepository.findCompletedProjectsByPmEmail(pmEmail).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<ProjectDto> getActiveProjectForPM(String token) {
        String pmEmail = jwtService.extractUsername(token);
        return projectRepository.findActiveProjectsByPmEmail(pmEmail).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<ProjectDto> getCurrentProjectsForEmployee(Long employeeId) {
        return projectRepository.findCurrentProjectsByEmployeeId(employeeId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<ProjectDto> getDistinctProjectsForEmployee(Long employeeId) {
        return projectRepository.findProjectsByEmployeeId(employeeId).stream()
                .map(this::toDto)
                .toList();
    }
    public static boolean isOverlapping(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        if(end1 == null && end2 == null) return true;
        else {
            if(end1 == null) {
                if(!end2.isBefore(start1)) return true;
                else return false;
            }
            if(end2 == null) {
                if(!end1.isBefore(start2)) return true;
                else return false;
            }
            return !(end1.isBefore(start2) || end2.isBefore(start1));
        }

    }

    @Override
    public void assignEmployeeToProject(AssignEmployeeRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        if (request.getWorkloadPercent() < 0 || request.getWorkloadPercent() > 100) {
            throw new IllegalArgumentException("Workload must be between 0 and 100");
        }
        if (request.getStartDate().isBefore(project.getStartDate())) {
            throw new IllegalArgumentException("Start date must not before project start date");
        }

        if (project.getEndDate() != null && request.getEndDate() != null && request.getEndDate().isAfter(project.getEndDate())) {
            throw new IllegalArgumentException("End date must not exceed project end date");
        }
        if(request.getEndDate() == null && project.getEndDate() != null) {
            request.setEndDate(project.getEndDate());
        }

        List<ProjectAssignment> activeAssignments = projectAssignmentRepository
                .findByEmployeeIdAndDateRangeOverlap(
                        request.getEmployeeId(),
                        request.getStartDate(),
                        request.getEndDate() == null ? LocalDate.now() : request.getEndDate()
                );

        int totalExistingWorkload = activeAssignments.stream()
                .mapToInt(ProjectAssignment::getWorkloadPercent)
                .sum();

        int totalAfterAssign = totalExistingWorkload + request.getWorkloadPercent();

        if (totalAfterAssign > 100) {
            throw new IllegalArgumentException("Total workload exceeds 100% for the given period");
        }
        List<ProjectAssignment> list =  projectAssignmentRepository.findByEmployeeIdAndProjectId(request.getEmployeeId(), request.getProjectId());
        for (ProjectAssignment a : list) {
            if(a.getEndDate() == null) {
                if(request.getStartDate().isAfter(a.getStartDate()) || (request.getEndDate() != null && request.getEndDate().isAfter(a.getStartDate()))) {
                    throw new IllegalArgumentException("Participation periods must not overlap");
                }
            }
            else {
                if(isOverlapping(a.getStartDate(), a.getEndDate(), request.getStartDate(), request.getEndDate())) {
                    throw new IllegalArgumentException("Participation periods must not overlap");
                }
            }
        }


        ProjectAssignment assignment = ProjectAssignment.builder()
                .employeeId(employee.getId())
                .projectId(project.getId())
                .workloadPercent(request.getWorkloadPercent())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        projectAssignmentRepository.save(assignment);
    }

    @Override
    public WorkloadRemainDto workloadPercentRemaining(WorkloadRemainCheckRequest request) {
        List<ProjectAssignment> activeAssignments;

        if (request.getEndDate() == null) {
            // Không có endDate: tìm tất cả assignment chưa kết thúc từ startDate trở đi
            activeAssignments = projectAssignmentRepository.findActiveOrFutureAssignments(
                    request.getEmployeeId(),
                    request.getStartDate()
            );
        } else {
            // Có endDate: dùng điều kiện overlap đầy đủ
            activeAssignments = projectAssignmentRepository.findByEmployeeIdAndDateRangeOverlap(
                    request.getEmployeeId(),
                    request.getStartDate(),
                    request.getEndDate()
            );
        }
//        System.out.println(request.getAssignmentId());
        int totalExistingWorkload = 0;
        for (ProjectAssignment assignment : activeAssignments) {
            if(request.getAssignmentId() != null && assignment.getAssignmentId().equals(request.getAssignmentId())) continue;
            totalExistingWorkload += (assignment.getWorkloadPercent() == null) ? 0 : assignment.getWorkloadPercent();
        }


        return new WorkloadRemainDto(100 - totalExistingWorkload);
    }

    @Override
    public void deleteAssignmentFromProject(Long assignmentId) {
        if (!projectAssignmentRepository.existsById(assignmentId)) {
            throw new EntityNotFoundException("Assignment not found");
        }
        projectAssignmentRepository.deleteById(assignmentId);
    }

    @Override
    public void updateAssignment(UpdateAssignmentRequest request) {
        ProjectAssignment projectAssignment = projectAssignmentRepository.findByAssignmentId(request.getAssignmentId()).orElseThrow(() -> new EntityNotFoundException("Assignment not found"));
        WorkloadRemainCheckRequest checkRequest = new WorkloadRemainCheckRequest(projectAssignment.getAssignmentId(), projectAssignment.getEmployeeId(), request.getStartDate(), request.getEndDate());
        WorkloadRemainDto workloadRemainDto = workloadPercentRemaining(checkRequest);

        List<ProjectAssignment> list =  projectAssignmentRepository.findByEmployeeIdAndProjectId(projectAssignment.getEmployeeId(), projectAssignment.getProjectId());
        for (ProjectAssignment a : list) {
            if(a.getAssignmentId().equals(request.getAssignmentId())) {continue;}
            if(a.getEndDate() == null) {
                if(request.getStartDate().isAfter(a.getStartDate()) || (request.getEndDate() != null && request.getEndDate().isAfter(a.getStartDate()))) {
                    System.out.println(a.getStartDate());
                    throw new IllegalArgumentException("Participation periods must not overlap");
                }
            }
            else {
                System.out.println(a.getStartDate() + " " + a.getEndDate());
                if(isOverlapping(a.getStartDate(), a.getEndDate(), request.getStartDate(), request.getEndDate())) {
                    throw new IllegalArgumentException("Participation periods must not overlap");
                }
            }
        }


        if(workloadRemainDto.getWorkloadPercent() < request.getWorkloadPercent()) {
            throw new BadRequestException("Total workload exceeds 100% for the given period");
        }
        projectAssignment.setWorkloadPercent(request.getWorkloadPercent());
        projectAssignment.setStartDate(request.getStartDate());
        projectAssignment.setEndDate(request.getEndDate());
        projectAssignmentRepository.save(projectAssignment);
    }

    @Override
    public List<ProjectMemberDto> getMembersOfProject(String token, Long projectId) {

        Long employeeId = jwtService.extractEmployeeId(token);

        if(!projectAssignmentRepository.existsByProjectIdAndEmployeeId(projectId, employeeId)) {
            Project project = projectRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException("Project not found"));
            String email = jwtService.extractUsername(token);
            if(!project.getPmEmail().equals(email)) {
                throw new AccessDeniedException("You do not have permission to access this project");
            }
        }

        List<ProjectAssignment> assignments = projectAssignmentRepository.findByProjectId(projectId);
        return toProjectMemberDtoList(assignments);
    }

    @Override
    public List<ProjectMemberDto> getCurrentMembersOfProject(Long projectId) {
        List<ProjectAssignment> assignments = projectAssignmentRepository.findCurrentMembersOfProject(projectId, LocalDate.now());
        return toProjectMemberDtoList(assignments);
    }

    @Override
    public List<ProjectMemberDto> getPastMembersOfProject(Long projectId) {
        List<ProjectAssignment> assignments = projectAssignmentRepository.findPastMembersOfProject(projectId, LocalDate.now());
        return toProjectMemberDtoList(assignments);
    }

    @Override
    public List<WorkloadBlockDto> getWorkloadBlocks(Long employeeId, LocalDate from, LocalDate to) {
        // Step 1: Get raw assignments
        List<ProjectAssignment> assignments = projectAssignmentRepository.findOverlappingAssignments(employeeId, from, to);

        // Step 2: Expand to daily workload map
        Map<LocalDate, Integer> dailyWorkload = new TreeMap<>();
        for (ProjectAssignment a : assignments) {
            if(a.getWorkloadPercent() == null) continue;
            LocalDate curr = a.getStartDate().isAfter(from) ? a.getStartDate() : from;
            while (!curr.isAfter(to) && (a.getEndDate() == null || !curr.isAfter(a.getEndDate()))) {
                int currentEffort = dailyWorkload.getOrDefault(curr, 0);
                dailyWorkload.put(curr, currentEffort + a.getWorkloadPercent());
                curr = curr.plusDays(1);
            }
        }

        // Step 3: Group into blocks
        List<WorkloadBlockDto> blocks = new ArrayList<>();
        LocalDate prevDate = null;
        Integer prevValue = null;
        LocalDate blockStart = null;

        for (Map.Entry<LocalDate, Integer> entry : dailyWorkload.entrySet()) {
            LocalDate date = entry.getKey();
            int value = entry.getValue();

            if (prevValue == null || !prevValue.equals(value) || !date.minusDays(1).equals(prevDate)) {
                // Save previous block
                if (prevValue != null) {
                    blocks.add(new WorkloadBlockDto(employeeId, blockStart, prevDate, prevValue));
                }
                // Start new block
                blockStart = date;
            }

            prevDate = date;
            prevValue = value;
        }

        // Save last block
        if (prevValue != null && blockStart != null && prevDate != null) {
            blocks.add(new WorkloadBlockDto(employeeId, blockStart, prevDate, prevValue));
        }
        return blocks;
    }


}
