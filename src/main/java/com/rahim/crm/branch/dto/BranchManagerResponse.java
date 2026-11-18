package com.rahim.crm.branch.dto;

import com.rahim.crm.entity.Role;

public class BranchManagerResponse {

    private Long id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private Role role;
    private Long branchId;
    private String branchName;
    private boolean enabled;

    public BranchManagerResponse() {
    }

    public BranchManagerResponse(
            Long id,
            String name,
            String username,
            String email,
            String phone,
            Role role,
            Long branchId,
            String branchName,
            boolean enabled) {

        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.branchId = branchId;
        this.branchName = branchName;
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
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

    public Role getRole() {
        return role;
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