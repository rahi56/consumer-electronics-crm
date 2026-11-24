
package com.rahim.crm.complaint.entity;

import com.rahim.crm.branch.entity.Branch;
import com.rahim.crm.customer.entity.Customer;
import com.rahim.crm.entity.User;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "complaints",
        indexes = {
                @Index(name = "idx_complaint_customer", columnList = "customer_id"),
                @Index(name = "idx_complaint_status", columnList = "status"),
                @Index(name = "idx_complaint_branch", columnList = "branch_id")
        }
)
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "complaint_number", nullable = false, unique = true, length = 30)
    private String complaintNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "subject", nullable = false, length = 150)
    private String subject;

    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    // Customer's service location
    @Column(name = "service_latitude", nullable = false)
    private Double serviceLatitude;

    @Column(name = "service_longitude", nullable = false)
    private Double serviceLongitude;

    // Automatically selected nearest branch
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    // Branch manager responsible for this complaint
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_manager_id")
    private User assignedManager;

    // Technician assigned manually by the branch manager
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_technician_id")
    private User assignedTechnician;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ComplaintStatus status;

    @Column(name = "resolution_notes", length = 2000)
    private String resolutionNotes;

    private LocalDateTime resolvedAt;

    private LocalDateTime closedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Complaint() {
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = ComplaintStatus.OPEN;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getComplaintNumber() {
        return complaintNumber;
    }

    public void setComplaintNumber(String complaintNumber) {
        this.complaintNumber = complaintNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getServiceLatitude() {
        return serviceLatitude;
    }

    public void setServiceLatitude(Double serviceLatitude) {
        this.serviceLatitude = serviceLatitude;
    }

    public Double getServiceLongitude() {
        return serviceLongitude;
    }

    public void setServiceLongitude(Double serviceLongitude) {
        this.serviceLongitude = serviceLongitude;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public User getAssignedManager() {
        return assignedManager;
    }

    public void setAssignedManager(User assignedManager) {
        this.assignedManager = assignedManager;
    }

    public User getAssignedTechnician() {
        return assignedTechnician;
    }

    public void setAssignedTechnician(User assignedTechnician) {
        this.assignedTechnician = assignedTechnician;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
