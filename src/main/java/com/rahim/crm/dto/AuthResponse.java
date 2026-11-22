
package com.rahim.crm.dto;

public class AuthResponse {

    private String token;
    private String tokenType;
    private boolean mustChangePassword;

    public AuthResponse() {
    }

    public AuthResponse(
            String token,
            String tokenType,
            boolean mustChangePassword
    ) {
        this.token = token;
        this.tokenType = tokenType;
        this.mustChangePassword = mustChangePassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public boolean isMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }
}
