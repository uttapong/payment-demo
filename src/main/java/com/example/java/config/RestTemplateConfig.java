package com.example.java.config;

import org.slf4j.MDC;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;


@Configuration
public class RestTemplateConfig {

    private static final String CORRELATION_ID = "correlationId";

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.interceptors(loggingInterceptor()).build();
    }

    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            String correlationId = MDC.get(CORRELATION_ID);
            if (correlationId != null) {
                request.getHeaders().add(CORRELATION_ID, correlationId);
            }
            return execution.execute(request, body);
        };
    }
}
