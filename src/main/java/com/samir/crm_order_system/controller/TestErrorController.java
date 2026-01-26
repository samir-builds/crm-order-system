package com.samir.crm_order_system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestErrorController {

    @GetMapping("/test/error")
    public void throwError() {
        throw new RuntimeException("Forced test exception");
    }
}
