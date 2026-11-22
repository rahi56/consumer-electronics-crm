
package com.rahim.crm.technician.dto;

public class TechnicianResponse {

    private Long id;
    private String technicianId;
    private String name;
    private String username;
    private String email;
    private String phone;
    private Long branchId;
    private String branchName;
    private boolean enabled;

    public TechnicianResponse() {
    }

    public TechnicianResponse(
            Long id,
            String technicianId,
            String name,
            String username,
            String email,
            String phone,
            Long branchId,
            String branchName,
            boolean enabled
    ) {
        this.id = id;
        this.technicianId = technicianId;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.branchId = branchId;
        this.branchName = branchName;
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }

    public String getTechnicianId() {
        return technicianId;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Long getBranchId() {
        return branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
