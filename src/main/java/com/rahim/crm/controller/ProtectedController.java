package com.rahim.crm.controller;

import com.rahim.crm.dto.ApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class ProtectedController {

    @GetMapping("/protected")
    public ResponseEntity<ApiResponse<String>> protectedEndpoint(
            Authentication authentication) {

        String message = "Welcome, " + authentication.getName()
                + "! You are authenticated.";

        return ResponseEntity.ok(ApiResponse.success(message, authentication.getName()));
    }
}
