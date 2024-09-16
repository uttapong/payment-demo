package com.example.java.model;

import lombok.Data;

@Data
public class PaymentRequest {
    private String accountNumber;
    private double amount;
    private String currency;
}
