package com.service.base;


import com.model.Employee;

public interface EmployeeService {

    boolean isAdmin(Long employeeId);

    boolean isPM(Long employeeId);

    Employee getEmployeeById(Long employeeId);
}
