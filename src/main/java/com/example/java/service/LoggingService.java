package com.example.java.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LoggingService {

    private static final Logger logger = LoggerFactory.getLogger(LoggingService.class);
    private final ObjectMapper objectMapper;

    public LoggingService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void logRequestResponse(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response,
            long executionTime) {
        try {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("method", request.getMethod());
            logMap.put("uri", request.getRequestURI());
            logMap.put("requestHeaders", getHeadersAsString(request));
            logMap.put("requestBody",
                    getContentAsString(request.getContentAsByteArray(), request.getCharacterEncoding()));
            logMap.put("responseStatus", response.getStatus());
            logMap.put("responseHeaders", getHeadersAsString(response));
            logMap.put("responseBody",
                    getContentAsString(response.getContentAsByteArray(), response.getCharacterEncoding()));
            logMap.put("executionTime", executionTime);
            logMap.put("requestType", request.getContentType());

            MDC.put("logType", "REQUEST_RESPONSE");
            logger.info(objectMapper.writeValueAsString(logMap));
        } catch (Exception e) {
            logger.error("Error logging request/response", e);
        } finally {
            MDC.remove("logType");
        }
    }

    private String getHeadersAsString(ContentCachingRequestWrapper request) {
        return Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(
                        headerName -> headerName,
                        request::getHeader))
                .toString();
    }

    private String getHeadersAsString(ContentCachingResponseWrapper response) {
        return response.getHeaderNames().stream()
                .collect(Collectors.toMap(
                        headerName -> headerName,
                        response::getHeader))
                .toString();
    }

    private String getContentAsString(byte[] content, String encoding) {
        try {
            return new String(content, encoding != null ? encoding : StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            logger.error("Failed to parse content", e);
            return "[Error reading content]";
        }
    }
}