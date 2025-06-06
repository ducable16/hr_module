package com.controller;

import com.model.dto.EmployeeDto;
import com.model.dto.EmployeeProjectParticipationDto;
import com.request.ChangeRoleRequest;
import com.request.EmployeeCreateRequest;
import com.request.EmployeeUpdateRequest;
import com.service.base.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping()
    public EmployeeDto create(@RequestBody EmployeeCreateRequest request) {
        return employeeService.createEmployee(request);
    }

    @PutMapping()
    public EmployeeDto update(@RequestBody EmployeeUpdateRequest request) {
        return employeeService.updateEmployee(request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }

    @PutMapping("/change-role")
    public void changeEmployeeRole(@RequestBody ChangeRoleRequest request) {
        employeeService.changeRole(request);
    }

    @GetMapping("/project-history/{employeeId}")
    public List<EmployeeProjectParticipationDto> getEmployeeProjectHistory(@PathVariable Long employeeId) {
        return employeeService.getProjectParticipationHistory(employeeId);
    }
}
