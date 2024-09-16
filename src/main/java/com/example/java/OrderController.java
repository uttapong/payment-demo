package com.example.java;


import com.example.java.model.PaymentRequest;
import com.example.java.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/submit")
    public String submitOrder(@RequestBody PaymentRequest paymentRequest) {
        paymentService.submitOrder(paymentRequest);
        return "Order submitted successfully.";
    }
}
