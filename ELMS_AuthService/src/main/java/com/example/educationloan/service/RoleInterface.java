package com.example.educationloan.service;

import com.example.educationloan.entity.Role;
import com.example.educationloan.entity.User;
import com.example.educationloan.enumconstant.RoleEnum;
import java.util.List;
import java.util.Optional;

public interface RoleInterface {

    Optional<Role> getByRoleName(RoleEnum roleName);
    List<Role> getAllRoles();
    List<Role> getRolesByUserId(Long userId);
    List<User> getUserWithRole(String roleName);
    List<User> getUsersByRoleName(RoleEnum roleName);
    Role createOrGetRole(RoleEnum name);
    User updateUserRole(Long userId, RoleEnum oldRole, RoleEnum newRole);
    User removeRoleFromUser(Long userId, RoleEnum roleName);
}