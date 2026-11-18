
package com.rahim.crm.branch.controller;

import com.rahim.crm.branch.dto.BranchRequest;
import com.rahim.crm.branch.dto.BranchResponse;
import com.rahim.crm.branch.service.BranchService;
import com.rahim.crm.dto.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    // Create branch
    @PostMapping
    public ResponseEntity<ApiResponse<BranchResponse>> createBranch(
            @Valid @RequestBody BranchRequest request
    ) {
        BranchResponse response = branchService.createBranch(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Branch created successfully", response));
    }

    // Get all branches
    @GetMapping
    public ResponseEntity<ApiResponse<List<BranchResponse>>> getAllBranches() {

        List<BranchResponse> branches = branchService.getAllBranches();

        return ResponseEntity.ok(
                ApiResponse.success("Branches retrieved successfully", branches)
        );
    }

    // Get branch by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BranchResponse>> getBranchById(
            @PathVariable Long id
    ) {
        BranchResponse response = branchService.getBranchById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Branch retrieved successfully", response)
        );
    }

    // Update branch
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BranchResponse>> updateBranch(
            @PathVariable Long id,
            @Valid @RequestBody BranchRequest request
    ) {
        BranchResponse response = branchService.updateBranch(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Branch updated successfully", response)
        );
    }

    // Deactivate branch
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<BranchResponse>> deactivateBranch(
            @PathVariable Long id
    ) {
        BranchResponse response = branchService.deactivateBranch(id);

        return ResponseEntity.ok(
                ApiResponse.success("Branch deactivated successfully", response)
        );
    }

    // Activate branch
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<BranchResponse>> activateBranch(
            @PathVariable Long id
    ) {
        BranchResponse response = branchService.activateBranch(id);

        return ResponseEntity.ok(
                ApiResponse.success("Branch activated successfully", response)
        );
    }
}
