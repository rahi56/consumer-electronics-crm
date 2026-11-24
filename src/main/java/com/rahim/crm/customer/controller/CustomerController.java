
package com.rahim.crm.customer.controller;

import com.rahim.crm.customer.dto.CustomerRequest;
import com.rahim.crm.customer.dto.CustomerResponse;
import com.rahim.crm.customer.service.CustomerService;
import com.rahim.crm.dto.ApiResponse;
import com.rahim.crm.customer.dto.CustomerUpdateRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // CREATE CUSTOMER
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BRANCH_MANAGER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CustomerRequest request) {

        System.out.println("NOt in controller");

        CustomerResponse response =
                customerService.createCustomer(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Customer created successfully",
                        response
                ));
    }

    // GET ALL CUSTOMERS
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BRANCH_MANAGER')")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllCustomers() {

        List<CustomerResponse> customers =
                customerService.getAllCustomers();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Customers retrieved successfully",
                        customers
                )
        );
    }

    // GET CUSTOMER BY ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BRANCH_MANAGER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(
            @PathVariable Long id) {

        CustomerResponse response =
                customerService.getCustomerById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Customer retrieved successfully",
                        response
                )
        );
    }


    // UPDATE CUSTOMER
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BRANCH_MANAGER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerUpdateRequest request) {

        CustomerResponse response =
                customerService.updateCustomer(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Customer updated successfully",
                        response
                )
        );
    }


    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'BRANCH_MANAGER')")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> searchCustomers(
            @RequestParam String keyword) {

        List<CustomerResponse> customers =
                customerService.searchCustomers(keyword);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Customer search completed",
                        customers
                )
        );
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'BRANCH_MANAGER')")
    public ResponseEntity<ApiResponse<String>> deactivateCustomer(
            @PathVariable Long id) {

        String message = customerService.deactivateCustomer(id);

        return ResponseEntity.ok(
                ApiResponse.success(message)
        );
    }


    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'BRANCH_MANAGER')")
    public ResponseEntity<ApiResponse<String>> activateCustomer(
            @PathVariable Long id) {

        String message = customerService.activateCustomer(id);

        return ResponseEntity.ok(
                ApiResponse.success(message)
        );
    }


}
