package com.hairstudio.api.model.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {
    ADMINISTRATOR((short) 1, "Administrator"),
    EMPLOYEE((short) 2, "Employee"),
    USER((short) 3, "User");

    private final short id;
    private final String roleName;

    RoleEnum(short id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    public static RoleEnum fromId(short id) {
        for (RoleEnum role : values()) {
            if (role.getId() == id) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role ID: " + id);
    }

    public static RoleEnum fromName(String name) {
        for (RoleEnum role : values()) {
            if (role.getRoleName().equalsIgnoreCase(name)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role name: " + name);
    }
}