package com.util;

import com.repository.ProjectRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@AllArgsConstructor
public class ProjectCodeGenerator {

    private static final String PREFIX = "P";

    private final ProjectRepository projectRepository;
    private static AtomicInteger counter;

    @PostConstruct
    public void init() {
        Integer max = projectRepository.findMaxProjectCodeNumber();
        this.counter = new AtomicInteger(max != null ? max : 0);
    }

    public String generateNextCode() {
        int next = counter.incrementAndGet();
        return PREFIX + String.format("%05d", next); // 5 chữ số, ví dụ: P00001
    }
}

