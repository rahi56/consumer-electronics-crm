
package com.rahim.crm.customer.controller;

import com.rahim.crm.Security.CustomUserPrincipal;
import com.rahim.crm.customer.dto.CustomerResponse;
import com.rahim.crm.customer.dto.CustomerUpdateRequest;
import com.rahim.crm.customer.service.CustomerPortalService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-portal")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerPortalController {

    private final CustomerPortalService customerPortalService;

    public CustomerPortalController(
            CustomerPortalService customerPortalService) {
        this.customerPortalService = customerPortalService;
    }

    @GetMapping("/profile")
    public CustomerResponse getMyProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        return customerPortalService.getMyProfile(principal.getId());
    }


    @PutMapping("/profile")
    public ResponseEntity<CustomerResponse> updateMyProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody CustomerUpdateRequest request) {

        CustomerResponse response =
                customerPortalService.updateMyProfile(
                        principal.getId(),
                        request
                );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/products")
    public List<?> getMyProducts(
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        return customerPortalService.getMyProducts(principal.getId());
    }



}
