package com.service.impl;

import com.model.Employee;
import com.enums.Role;
import com.repository.EmployeeRepository;
import com.service.base.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public boolean isAdmin(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .map(emp -> emp.getRole() == Role.ADMIN)
                .orElse(false);
    }

    @Override
    public boolean isPM(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .map(emp -> emp.getRole() == Role.PM)
                .orElse(false);
    }

    @Override
    public Employee getEmployeeById(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
    }
}

