
package com.rahim.crm.technician.dto;

public class TechnicianCreateResponse {

    private Long id;
    private String technicianId;
    private String name;
    private String username;
    private String email;
    private String temporaryPassword;
    private Long branchId;
    private String branchName;

    public TechnicianCreateResponse(
            Long id,
            String technicianId,
            String name,
            String username,
            String email,
            String temporaryPassword,
            Long branchId,
            String branchName
    ) {
        this.id = id;
        this.technicianId = technicianId;
        this.name = name;
        this.username = username;
        this.email = email;
        this.temporaryPassword = temporaryPassword;
        this.branchId = branchId;
        this.branchName = branchName;
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

    public String getTemporaryPassword() {
        return temporaryPassword;
    }

    public Long getBranchId() {
        return branchId;
    }

    public String getBranchName() {
        return branchName;
    }
}
