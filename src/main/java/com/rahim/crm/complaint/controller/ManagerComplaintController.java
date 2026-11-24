
package com.rahim.crm.complaint.controller;

import com.rahim.crm.complaint.dto.ComplaintResponse;
import com.rahim.crm.complaint.dto.TechnicianAssignmentRequest;
import com.rahim.crm.complaint.service.ComplaintService;
import com.rahim.crm.Security.CustomUserPrincipal;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager/complaints")
@PreAuthorize("hasRole('BRANCH_MANAGER')")
public class ManagerComplaintController {

    private final ComplaintService complaintService;

    public ManagerComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    // View complaints assigned to the authenticated manager
    @GetMapping
    public List<ComplaintResponse> getMyComplaints(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return complaintService.getManagerComplaints(principal.getId());
    }


    // Assign a technician to a complaint
    @PostMapping("/{complaintId}/assign-technician")
    public ComplaintResponse assignTechnician(
            @PathVariable Long complaintId,
            @Valid @RequestBody TechnicianAssignmentRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return complaintService.assignTechnician(
                principal.getId(),
                complaintId,
                request.getTechnicianId()
        );
    }

}
