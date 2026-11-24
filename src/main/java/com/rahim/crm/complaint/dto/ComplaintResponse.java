
package com.rahim.crm.complaint.dto;

import com.rahim.crm.complaint.entity.ComplaintStatus;

import java.time.LocalDateTime;

public class ComplaintResponse {

    private Long id;
    private String complaintNumber;
    private String subject;
    private String description;
    private ComplaintStatus status;

    private String resolutionNotes;

    // Assigned branch information
    private Long branchId;
    private String branchName;

    // Assigned manager information
    private Long assignedManagerId;
    private String assignedManagerName;

    // Assigned technician information
    private Long assignedTechnicianId;
    private String assignedTechnicianName;

    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ComplaintResponse() {
    }

    public ComplaintResponse(
            Long id,
            String complaintNumber,
            String subject,
            String description,
            ComplaintStatus status,
            String resolutionNotes,
            Long branchId,
            String branchName,
            Long assignedManagerId,
            String assignedManagerName,
            Long assignedTechnicianId,
            String assignedTechnicianName,
            LocalDateTime resolvedAt,
            LocalDateTime closedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.complaintNumber = complaintNumber;
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.resolutionNotes = resolutionNotes;
        this.branchId = branchId;
        this.branchName = branchName;
        this.assignedManagerId = assignedManagerId;
        this.assignedManagerName = assignedManagerName;
        this.assignedTechnicianId = assignedTechnicianId;
        this.assignedTechnicianName = assignedTechnicianName;
        this.resolvedAt = resolvedAt;
        this.closedAt = closedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getComplaintNumber() {
        return complaintNumber;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public Long getBranchId() {
        return branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public Long getAssignedManagerId() {
        return assignedManagerId;
    }

    public String getAssignedManagerName() {
        return assignedManagerName;
    }

    public Long getAssignedTechnicianId() {
        return assignedTechnicianId;
    }

    public String getAssignedTechnicianName() {
        return assignedTechnicianName;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
