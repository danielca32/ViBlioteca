package com.danielcastro.viblioteca;

import java.io.Serializable;

public class User implements Serializable {
    private String UID;
    private boolean active;
    private String role;
    private String name;

    public User() {
    }

    public User(String UID, boolean active, String role, String name) {
        this.UID = UID;
        this.active = active;
        this.role = role;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
