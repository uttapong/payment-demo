// File: LoggingService.java
package com.example.java.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.stream.Collectors;

public class LoggingService {

    private static final Logger logger = LoggerFactory.getLogger(LoggingService.class);
    private final ObjectMapper objectMapper;

    public LoggingService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void logRequestResponse(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, long executionTime) {
        try {
            MDC.put("method", request.getMethod());
            MDC.put("uri", request.getRequestURI());
            MDC.put("requestHeaders", getHeadersAsString(request));
            MDC.put("requestBody", getContentAsString(request.getContentAsByteArray(), request.getCharacterEncoding()));
            MDC.put("responseStatus", String.valueOf(response.getStatus()));
            MDC.put("responseHeaders", getHeadersAsString(response));
            MDC.put("responseBody", getContentAsString(response.getContentAsByteArray(), response.getCharacterEncoding()));
            MDC.put("executionTime", String.valueOf(executionTime));
            MDC.put("requestType", request.getContentType() != null ? request.getContentType() : "");
            MDC.put("logType", "REQUEST_RESPONSE");

            logger.info("Request and Response logged");
        } catch (Exception e) {
            logger.error("Error logging request/response", e);
        } finally {
            MDC.clear();
        }
    }

    private String getHeadersAsString(ContentCachingRequestWrapper request) {
        return objectMapper.valueToTree(
            Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(
                    headerName -> headerName,
                    request::getHeader
                ))
        ).toString();
    }

    private String getHeadersAsString(ContentCachingResponseWrapper response) {
        return objectMapper.valueToTree(
            response.getHeaderNames().stream()
                .collect(Collectors.toMap(
                    headerName -> headerName,
                    response::getHeader
                ))
        ).toString();
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