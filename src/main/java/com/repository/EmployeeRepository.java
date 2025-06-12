package com.repository;

import com.enums.Role;
import com.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findEmployeeByEmail(String email);

    Optional<Employee> findEmployeeById(Long id);

    @Query("""
    SELECT u FROM Employee u 
    WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :emailFragment, '%')) 
    AND u.role <> 'ADMIN'
""")
    List<Employee> findTop10ByEmailLike(@Param("emailFragment") String emailFragment, Pageable pageable);

    @Query("""
    SELECT u FROM Employee u 
    WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :emailFragment, '%')) 
    AND u.role = :role
""")
    List<Employee> findTop10ByEmailAndRole(
            @Param("emailFragment") String emailFragment,
            @Param("role") Role role,
            Pageable pageable
    );

    @Query("SELECT MAX(CAST(SUBSTRING(e.employeeCode, 5) AS int)) FROM Employee e")
    Integer findMaxEmployeeCodeNumber();

    Page<Employee> findAllByRole(Role role, Pageable pageable);
}
