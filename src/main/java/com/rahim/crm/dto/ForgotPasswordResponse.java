
package com.rahim.crm.dto;

public class ForgotPasswordResponse {

    private String message;
    private String otp;

    public ForgotPasswordResponse(String message, String otp) {
        this.message = message;
        this.otp = otp;
    }

    public String getMessage() {
        return message;
    }

    public String getOtp() {
        return otp;
    }
}
