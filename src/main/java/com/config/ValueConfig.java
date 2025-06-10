package com.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ValueConfig {
    @Value("${spring.application.employee-default-password}")
    private String defaultRawPassword;
}
