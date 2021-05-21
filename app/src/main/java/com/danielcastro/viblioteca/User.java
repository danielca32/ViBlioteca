package com.danielcastro.viblioteca;

import java.io.Serializable;

public class User implements Serializable {
    private String UID;
    private boolean active;
    private String role;

    public User() {
    }

    public User(String UID, boolean active, String role) {
        this.UID = UID;
        this.active = active;
        this.role = role;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
