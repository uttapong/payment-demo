package com.example.java.config;

import com.example.java.filter.RequestResponseLoggingFilter;
import com.example.java.service.LoggingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @ConditionalOnMissingBean
    public LoggingService loggingService(ObjectMapper objectMapper) {
        return new LoggingService(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestResponseLoggingFilter requestResponseLoggingFilter(LoggingService loggingService) {
        return new RequestResponseLoggingFilter(loggingService);
    }
}