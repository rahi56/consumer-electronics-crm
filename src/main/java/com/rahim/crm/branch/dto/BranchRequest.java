
package com.rahim.crm.branch.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class BranchRequest {

    @NotBlank(message = "Branch name is required")
    @Size(max = 100, message = "Branch name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Branch code is required")
    @Size(min = 3, max = 20, message = "Branch code must be between 3 and 20 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9-]+$",
            message = "Branch code can contain only letters, numbers and hyphens"
    )
    private String code;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Invalid phone number"
    )
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    @Size(max = 254, message = "Email cannot exceed 254 characters")
    private String email;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100)
    private String state;

    @NotBlank(message = "Postal code is required")
    @Size(max = 15)
    private String postalCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
