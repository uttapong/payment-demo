package com.example.java.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.java.model.PaymentRequest;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class PaymentService {

    private static final String CORRELATION_ID = "correlationId";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    public void submitOrder(PaymentRequest paymentRequest) {

        try {
            // Log the request with correlationId
            log.info("Submitting order with correlationId: {}", MDC.get(CORRELATION_ID));
            log.info("url : {}", paymentServiceUrl);
            log.info("paymentRequest: {}", paymentRequest);
            // Call the payment microservice with POST method
            restTemplate.postForObject(paymentServiceUrl, paymentRequest, String.class);

            // Log the successful call
            log.info("Order submitted successfully.");
        } catch (Exception e) {
            // Log the error with correlationId
            log.error("Error while submitting order with correlationId: {}", MDC.get(CORRELATION_ID), e);
        } finally {
            // Clear the correlationId from MDC
            // MDC.clear();
        }
    }
}
