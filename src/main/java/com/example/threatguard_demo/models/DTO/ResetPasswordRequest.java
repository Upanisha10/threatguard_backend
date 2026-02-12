package com.example.threatguard_demo.models.DTO;

public class ResetPasswordRequest {
    private String token;
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public String getToken() {
        return token;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

