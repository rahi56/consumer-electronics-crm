
package com.rahim.crm.complaint.dto;

import com.rahim.crm.complaint.entity.ComplaintActionType;

import java.time.LocalDateTime;

public class ComplaintHistoryResponse {

    private Long id;
    private ComplaintActionType actionType;
    private String reason;
    private String managerResponse;
    private String performedBy;
    private LocalDateTime createdAt;

    public ComplaintHistoryResponse() {
    }

    public ComplaintHistoryResponse(
            Long id,
            ComplaintActionType actionType,
            String reason,
            String managerResponse,
            String performedBy,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.actionType = actionType;
        this.reason = reason;
        this.managerResponse = managerResponse;
        this.performedBy = performedBy;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public ComplaintActionType getActionType() {
        return actionType;
    }

    public String getReason() {
        return reason;
    }

    public String getManagerResponse() {
        return managerResponse;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
