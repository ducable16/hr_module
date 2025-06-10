package com.controller;

import com.enums.Role;
import com.exception.EntityNotFoundException;
import com.model.Employee;
import com.model.LoginAccount;
import com.model.dto.*;
import com.request.ChangeRoleRequest;
import com.request.EmployeeCreateRequest;
import com.request.EmployeeUpdateRequest;
import com.service.JwtService;
import com.service.base.EmployeeService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/employee")
@AllArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    private final JwtService jwtService;

    @GetMapping("/info")
    public EmployeeDto getEmployeeInfo(@RequestHeader("Authorization") String token) {
        return employeeService.getEmployeeByEmail(token);
    }

    @GetMapping("/role-list")
    public List<String> getListRole() {
        return Arrays.stream(Role.values())
                .map(Enum::name)
                .toList();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<EmployeeDto> getAllEmployeesForAdmin(@RequestParam(required = false) Role role, @RequestParam int page, @RequestParam int size) {
        return employeeService.getAllEmployeesForAdmin(role, page, size);
    }


    @PostMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public EmployeeOnCreateDto create(@RequestBody EmployeeCreateRequest request) {
        return employeeService.createEmployee(request);
    }

    @PutMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeDto update(@RequestBody EmployeeUpdateRequest request) {
        return employeeService.updateEmployee(request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }

    @PutMapping("/change-role")
    @PreAuthorize("hasRole('ADMIN')")
    public void changeEmployeeRole(@RequestBody ChangeRoleRequest request) {
        employeeService.changeRole(request);
    }

    @GetMapping("/project-history/{employeeId}")
    public List<EmployeeProjectParticipationDto> getEmployeeProjectHistory(@RequestHeader("Authorization") String token, @PathVariable Long employeeId) {
        return employeeService.getProjectParticipationHistory(token, employeeId);
    }


    @GetMapping("/{projectId}/{employeeId}")
    public List<ParticipationPeriodDto> getEmployeeProjectHistory(@PathVariable Long projectId, @PathVariable Long employeeId) {
        return employeeService.getParticipationPeriod(projectId, employeeId);
    }


    @GetMapping("/search")
    public List<EmployeeSearchDto> searchByEmail(@RequestParam String email) {
        return employeeService.searchByEmail(email);
    }


}
