package com.campus.marketplace.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class HealthController {

    /**
     * Public health check endpoints returning HTTP 200 with {"status":"UP"}
     * Accessible without authentication.
     */
    @GetMapping({"/api/health", "/health", "/"})
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Collections.singletonMap("status", "UP"));
    }
}
