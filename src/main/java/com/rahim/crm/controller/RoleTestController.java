package com.rahim.crm.controller;

import com.rahim.crm.dto.ApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test/roles")
public class RoleTestController {

    @GetMapping("/test-denied")
    public ResponseEntity<ApiResponse<Void>> testDenied() {
        throw new AccessDeniedException("Testing access denied");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ApiResponse<String>> adminAccess() {
        return ResponseEntity.ok(
                ApiResponse.success("Admin access granted",
                        "Welcome Admin! You have ADMIN access."));
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<String>> managerAccess() {
        return ResponseEntity.ok(
                ApiResponse.success("Manager access granted",
                        "Welcome Manager! You have MANAGER access."));
    }

    @GetMapping("/employee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<String>> employeeAccess() {
        return ResponseEntity.ok(
                ApiResponse.success("Employee access granted",
                        "Welcome Employee! You have EMPLOYEE access."));
    }

    @GetMapping("/technician")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<ApiResponse<String>> technicianAccess() {
        return ResponseEntity.ok(
                ApiResponse.success("Technician access granted",
                        "Welcome Technician! You have TECHNICIAN access."));
    }

    @GetMapping("/whoami")
    public ResponseEntity<ApiResponse<String>> whoAmI(Authentication authentication) {
        String info = "Username: " + authentication.getName()
                + " | Authorities: " + authentication.getAuthorities();
        return ResponseEntity.ok(ApiResponse.success("User info retrieved", info));
    }
}
