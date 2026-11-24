
package com.rahim.crm.complaint.entity;

import com.rahim.crm.entity.User;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "complaint_history",
        indexes = {
                @Index(name = "idx_history_complaint", columnList = "complaint_id"),
                @Index(name = "idx_history_created_at", columnList = "created_at")
        }
)
public class ComplaintHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id", nullable = false)
    private Complaint complaint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by", nullable = false)
    private User performedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ComplaintActionType actionType;

    @Column(nullable = false, length = 2000)
    private String reason;

    @Column(length = 2000)
    private String managerResponse;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public void setComplaint(Complaint complaint) {
        this.complaint = complaint;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(User performedBy) {
        this.performedBy = performedBy;
    }

    public ComplaintActionType getActionType() {
        return actionType;
    }

    public void setActionType(ComplaintActionType actionType) {
        this.actionType = actionType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getManagerResponse() {
        return managerResponse;
    }

    public void setManagerResponse(String managerResponse) {
        this.managerResponse = managerResponse;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
