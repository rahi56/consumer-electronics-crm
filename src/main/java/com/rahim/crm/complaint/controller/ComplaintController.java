
package com.rahim.crm.complaint.controller;

import com.rahim.crm.Security.CustomUserPrincipal;
import com.rahim.crm.complaint.dto.ComplaintActionRequest;
import com.rahim.crm.complaint.dto.ComplaintHistoryResponse;
import com.rahim.crm.complaint.dto.ComplaintRequest;
import com.rahim.crm.complaint.dto.ComplaintResponse;
import com.rahim.crm.complaint.service.ComplaintService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-portal/complaints")
@PreAuthorize("hasRole('CUSTOMER')")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    // Submit a new complaint
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComplaintResponse createComplaint(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody ComplaintRequest request
    ) {
        return complaintService.createComplaint(
                principal.getId(),
                request
        );
    }

    // View all complaints belonging to logged-in customer
    @GetMapping
    public List<ComplaintResponse> getMyComplaints(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return complaintService.getMyComplaints(principal.getId());
    }

    // Reopen a resolved or closed complaint
    @PostMapping("/{complaintId}/reopen")
    public ComplaintResponse reopenComplaint(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long complaintId,
            @Valid @RequestBody ComplaintActionRequest request
    ) {
        return complaintService.reopenComplaint(
                principal.getId(),
                complaintId,
                request
        );
    }

    // Appeal a resolved or closed complaint
    @PostMapping("/{complaintId}/appeal")
    public ComplaintResponse appealComplaint(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long complaintId,
            @Valid @RequestBody ComplaintActionRequest request
    ) {
        return complaintService.appealComplaint(
                principal.getId(),
                complaintId,
                request
        );
    }

    // View complaint history
    @GetMapping("/{complaintId}/history")
    public List<ComplaintHistoryResponse> getComplaintHistory(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long complaintId
    ) {
        return complaintService.getComplaintHistory(
                principal.getId(),
                complaintId
        );
    }
}
