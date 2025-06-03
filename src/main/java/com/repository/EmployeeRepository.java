package com.repository;

import com.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    @Query("SELECT MAX(CAST(SUBSTRING(e.employeeCode, 5) AS int)) FROM Employee e")
    Integer findMaxEmployeeCodeNumber();
}
