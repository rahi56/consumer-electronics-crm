package com.rahim.crm.branch.controller;

import com.rahim.crm.branch.dto.BranchManagerRequest;
import com.rahim.crm.branch.dto.BranchManagerResponse;
import com.rahim.crm.branch.service.BranchManagerService;
import com.rahim.crm.dto.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches/managers")
public class BranchManagerController {

    private final BranchManagerService branchManagerService;

    public BranchManagerController(
            BranchManagerService branchManagerService) {
        this.branchManagerService = branchManagerService;
    }

    // ADMIN creates a Branch Manager
    @PostMapping
    public ResponseEntity<ApiResponse<BranchManagerResponse>> createManager(
            @Valid @RequestBody BranchManagerRequest request) {

        BranchManagerResponse response =
                branchManagerService.createManager(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Branch manager created successfully",
                        response
                ));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<ApiResponse<List<BranchManagerResponse>>> getManagersByBranch(
            @PathVariable Long branchId) {

        List<BranchManagerResponse> managers =
                branchManagerService.getManagersByBranch(branchId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Branch managers retrieved successfully",
                        managers
                )
        );
    }

    @GetMapping("/{managerId}")
    public ResponseEntity<ApiResponse<BranchManagerResponse>> getManagerById(
            @PathVariable Long managerId) {

        BranchManagerResponse manager =
                branchManagerService.getManagerById(managerId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Branch manager retrieved successfully",
                        manager
                )
        );
    }

    @PatchMapping("/{managerId}/deactivate")
    public ResponseEntity<ApiResponse<BranchManagerResponse>> deactivateManager(
            @PathVariable Long managerId) {

        BranchManagerResponse manager =
                branchManagerService.deactivateManager(managerId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Branch manager deactivated successfully",
                        manager
                )
        );
    }

    @PatchMapping("/{managerId}/activate")
    public ResponseEntity<ApiResponse<BranchManagerResponse>> activateManager(
            @PathVariable Long managerId) {

        BranchManagerResponse manager =
                branchManagerService.activateManager(managerId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Branch manager activated successfully",
                        manager
                )
        );
    }


}