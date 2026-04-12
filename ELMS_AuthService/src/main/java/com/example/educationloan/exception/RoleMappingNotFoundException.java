package com.example.educationloan.exception;

public class RoleMappingNotFoundException extends RuntimeException {
    public RoleMappingNotFoundException(String message) {
        super(message);
    }

    public RoleMappingNotFoundException(Long userId, Long roleId) {
        super(String.format("Role mapping not found for user ID: %d and role ID: %d", userId, roleId));
    }
}
