
package com.rahim.crm.controller;

import com.rahim.crm.dto.*;
import com.rahim.crm.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(
            @Valid @RequestBody RegisterRequest request) {

        authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful"));
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        String token = authService.login(
                request.getEmail(),
                request.getPassword()
        );

        AuthResponse authResponse = new AuthResponse(token, "Bearer");

        return ResponseEntity.ok(
                ApiResponse.success("Login successful", authResponse)
        );
    }

    // FORGOT PASSWORD - GENERATE OTP
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ForgotPasswordResponse>> forgetPassword(
            @Valid @RequestBody ForgetPasswordRequest request) {

        ForgotPasswordResponse response =
                authService.forgetPassword(request.getEmail());

        return ResponseEntity.ok(
                ApiResponse.success(response.getMessage(), response)
        );
    }

    // VERIFY OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        String result = authService.verifyOtp(
                request.getEmail(),
                request.getOtp()
        );

        return ResponseEntity.ok(
                ApiResponse.success(result)
        );
    }

    // RESET PASSWORD
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        String result = authService.resetPassword(
                request.getEmail(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                ApiResponse.success(result)
        );
    }
}
