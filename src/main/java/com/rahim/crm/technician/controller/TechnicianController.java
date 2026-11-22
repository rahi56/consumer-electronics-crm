
package com.rahim.crm.technician.controller;

import com.rahim.crm.dto.ApiResponse;
import com.rahim.crm.technician.dto.TechnicianCreateResponse;
import com.rahim.crm.technician.dto.TechnicianRequest;
import com.rahim.crm.technician.dto.TechnicianResponse;
import com.rahim.crm.technician.service.TechnicianService;
import com.rahim.crm.technician.dto.TechnicianUpdateRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/technicians")
public class TechnicianController {

    private final TechnicianService technicianService;

    public TechnicianController(TechnicianService technicianService) {
        this.technicianService = technicianService;
    }

    // Create technician
    @PostMapping
    public ResponseEntity<ApiResponse<TechnicianCreateResponse>> createTechnician(
            @Valid @RequestBody TechnicianRequest request
    ) {
        TechnicianCreateResponse response =
                technicianService.createTechnician(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Technician account created successfully.",
                        response
                ));
    }

    // Get technicians by branch
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<ApiResponse<List<TechnicianResponse>>> getTechniciansByBranch(
            @PathVariable Long branchId
    ) {
        List<TechnicianResponse> response =
                technicianService.getTechniciansByBranch(branchId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Technicians retrieved successfully.",
                        response
                )
        );
    }



    @GetMapping("/{technicianId}")
    public ResponseEntity<ApiResponse<TechnicianResponse>> getTechnicianById(
            @PathVariable Long technicianId
    ) {
        TechnicianResponse response =
                technicianService.getTechnicianById(technicianId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Technician retrieved successfully.",
                        response
                )
        );
    }



    @PutMapping("/{technicianId}")
    public ResponseEntity<ApiResponse<TechnicianResponse>> updateTechnician(
            @PathVariable Long technicianId,
            @Valid @RequestBody TechnicianUpdateRequest request
    ) {
        TechnicianResponse response =
                technicianService.updateTechnician(technicianId, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Technician updated successfully.",
                        response
                )
        );
    }


    @PatchMapping("/{technicianId}/deactivate")
    public ResponseEntity<ApiResponse<TechnicianResponse>> deactivateTechnician(
            @PathVariable Long technicianId
    ) {
        TechnicianResponse response =
                technicianService.deactivateTechnician(technicianId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Technician account deactivated successfully.",
                        response
                )
        );
    }




    @PatchMapping("/{technicianId}/activate")
    public ResponseEntity<ApiResponse<TechnicianResponse>> activateTechnician(
            @PathVariable Long technicianId
    ) {
        TechnicianResponse response =
                technicianService.activateTechnician(technicianId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Technician account activated successfully.",
                        response
                )
        );
    }


}
