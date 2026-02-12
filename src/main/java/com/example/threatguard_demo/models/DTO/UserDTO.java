package com.example.threatguard_demo.models.DTO;

import com.example.threatguard_demo.constants.Role;

public class UserDTO {
    private String name;
    private String email;
    private Role role;

    public void setRole(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
