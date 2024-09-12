package com.example.java;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;


@Configuration
public class LoggingConfiguration {

    @Value("${application.name:MySpringApp}")
    private String appName;
    
    @PostConstruct
    public void init() {
        MDC.put("application_name", appName);
    }
}
