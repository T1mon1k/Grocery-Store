package com.example.onlinestore.entity;

public enum Role {
    ROLE_USER("Користувач"),
    ROLE_ADMIN("Адміністратор");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
