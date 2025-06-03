package com.util;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.repository.EmployeeRepository;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@AllArgsConstructor
public class EmployeeCodeGenerator {

    private static final String PREFIX = "2025";

    private final EmployeeRepository employeeRepository;
    private static AtomicInteger counter;

    @PostConstruct
    public void init() {
        Integer max = employeeRepository.findMaxEmployeeCodeNumber();
        counter = new AtomicInteger(max != null ? max : 0);
    }

    public String generateNextCode() {
        int next = counter.incrementAndGet();
        return PREFIX + String.format("%04d", next);
    }
}
