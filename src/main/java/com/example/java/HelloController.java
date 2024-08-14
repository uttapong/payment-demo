package com.example.java;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.java.model.Order;

@RestController
public class HelloController {

    @GetMapping("/orders")
    public List<Order> getOrders() {
        return Arrays.asList(
                new Order(1L, "book", 1234.00),
                new Order(2L, "cars", 1224.00),
                new Order(3L, "disk", 1244.00));
    }
}
