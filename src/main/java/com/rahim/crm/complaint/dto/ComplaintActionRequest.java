
package com.rahim.crm.complaint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ComplaintActionRequest {

    @NotBlank(message = "Reason is required")
    @Size(
            min = 10,
            max = 2000,
            message = "Reason must be between 10 and 2000 characters"
    )
    private String reason;

    public ComplaintActionRequest() {
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
