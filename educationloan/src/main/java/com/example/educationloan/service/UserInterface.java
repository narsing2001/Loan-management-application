package com.example.educationloan.service;

import com.example.educationloan.dto.RegisterDTO;
import com.example.educationloan.entity.Role;
import com.example.educationloan.entity.User;
import com.example.educationloan.enumconstant.RoleEnum;

import java.util.List;
import java.util.Optional;

public interface UserInterface {

    User createUser(String email, String password, String firstName, String lastName);
    User getUserById(Long id);
    Optional<User> getUserById1(Long userId);
    List<User> getAllUsers();
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    List<User> filterUsersByRoleAndStatus(RoleEnum roleName, Boolean isActive);
    List<Role> getRolesByUserId(Long userId);
    Role getRoleById(Long roleId);
    User updateUser(Long id, String username, String email, String password,String firstName, String lastName);
    User patchUser(Long id, String username, String email, String password,String firstName, String lastName);
    void activateUser(Long id);
    void deactivateUser(Long id);
    void verifyEmail(Long id, String email);
    void updatePassword(Long id, String newPassword);
    User assignRoleToUser(Long userId, RoleEnum roleName);
    User assignRolesUser1(Long userId, List<RoleEnum> roleNames);
    void removeRoleFromUser(Long userId, RoleEnum roleName);
    void removeUserRole(Long userRoleId);
    void removeUserRole1(Long userId, Long roleId);
    boolean isUserAdmin(Long userId);
    boolean isUserEmployee(Long userId);
    boolean doesUserHaveRole(Long id, RoleEnum roleName);
    boolean deleteUser(Long id);
    User registerUser(RegisterDTO request);
}