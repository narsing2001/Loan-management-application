package com.example.educationloan.controller;

import com.example.educationloan.dto.AssignRoleDTO;
import com.example.educationloan.dto.UserRoleDTO;
import com.example.educationloan.entity.Role;
import com.example.educationloan.entity.User;
import com.example.educationloan.entity.UserRole;
import com.example.educationloan.response.ApiResponse;
import com.example.educationloan.service.UserRoleService;
import com.example.educationloan.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/v1/user_roles")
@RequiredArgsConstructor
public class UserRolesController {

    private final UserService userService;
    private final UserRoleService userRoleService;

    private static final String ROW  = "+----------------+------------+----------------------+----------------------+";
    private static final String HEAD = "| %-14s | %-10s | %-20s | %-20s |";
    private static final String DATA = "| %-14s | %-10s | %-20s | %-20s |";

    private void logUserRoleTable(String operation, List<UserRoleDTO> list) {
        log.info("Operation : {}", operation);
        log.info(ROW);
        log.info(String.format(HEAD, "UserRole ID", "User ID", "Role Name", "Assigned By"));
        log.info(ROW);
        list.forEach(userRole -> log.info(String.format(DATA, userRole.getRoleId(), userRole.getUserId(), userRole.getRoleName(), userRole.getAssignedBy())));
        log.info(ROW);
        log.info("Total records: {}", list.size());
    }

    // Fetch all UserRole mappings for a given user id--------------------------------------------------
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<UserRoleDTO>>> getUserRolesByUserId(@PathVariable Long userId) {
        log.info("REQUEST  : GET /api/v1/user_roles/user/{} | Fetching all roles for userId={}", userId, userId);
        List<UserRole> userRoles = userRoleService.getUserRolesByUserId(userId);
        List<UserRoleDTO> response = userRoles.stream().map(UserRoleDTO::fromEntity).toList();
        logUserRoleTable("GET_ROLES_BY_USER_ID", response);
        log.info("RESPONSE : 200 OK | userId={} | rolesCount={}", userId, response.size());
        return ResponseEntity.ok(new ApiResponse<>(true, "User roles fetched successfully", response));
    }

    // Fetch all UserRole mappings for a given role-------------------------------------------------
    @GetMapping("/role/{roleId}")
    public ResponseEntity<ApiResponse<List<UserRoleDTO>>> getUserRolesByRoleId(@PathVariable Long roleId) {
        log.info("REQUEST  : GET /api/v1/user_roles/role/{} | Fetching all users for roleId={}", roleId, roleId);
        List<UserRole> userRoles = userRoleService.getUserRolesByRoleId(roleId);
        List<UserRoleDTO> response = userRoles.stream().map(UserRoleDTO::fromEntity).toList();
        logUserRoleTable("GET_USERS_BY_ROLE_ID", response);
        log.info("RESPONSE : 200 OK | roleId={} | usersCount={}", roleId, response.size());
        return ResponseEntity.ok(new ApiResponse<>(true, "Role mappings fetched successfully", response));
    }

    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<UserRoleDTO>> assignRoleToUser(@RequestBody AssignRoleDTO request) {
        log.info("REQUEST  : POST /api/v1/user_roles/assign | userId={} roleId={} assignedBy={}", request.getUserId(), request.getRoleId(), request.getAssignedBy());
        User user = userService.getUserById(request.getUserId());
        Role role = userService.getRoleById(request.getRoleId());
        UserRole userRole = userRoleService.giveRoleToUser(user, role, request.getAssignedBy());
        UserRoleDTO response = UserRoleDTO.fromEntity(userRole);
        log.info("Operation : ASSIGN_ROLE_TO_USER");
        log.info(ROW);
        log.info(String.format(HEAD, "UserRole ID", "User ID", "Role Name", "Assigned By"));
        log.info(ROW);
        log.info(String.format(DATA, response.getRoleId(),response.getUserId(), response.getRoleName(), response.getAssignedBy()));
        log.info(ROW);
        log.info("RESPONSE : 200 OK | role assigned | userId={} roleId={} assignedBy={}", request.getUserId(), request.getRoleId(), request.getAssignedBy());
        return ResponseEntity.ok(new ApiResponse<>(true, "Role assigned successfully", UserRoleDTO.fromEntity(userRole)));
    }

    //remove the particular role from the user using the roleId---------------------------------------------------
    @DeleteMapping("/{userId}/role/{roleId}")
    public ResponseEntity<ApiResponse<Void>> removeUserRole(@PathVariable Long userId, @PathVariable Long roleId) {
        log.info("REQUEST  : DELETE /api/v1/user_roles/{}/role/{} | Removing roleId={} from userId={}", userId, roleId, roleId, userId);
        userService.removeUserRole1(userId, roleId);
        log.info("Operation : REMOVE_ROLE_FROM_USER");
        log.info("+------------+------------+");
        log.info(String.format("| %-10s | %-10s |", "User ID", "Role ID"));
        log.info("+------------+------------+");
        log.info(String.format("| %-10s | %-10s |", userId, roleId));
        log.info("+------------+------------+");
        log.info("RESPONSE : 200 OK | roleId={} removed from userId={}", roleId, userId);

        return ResponseEntity.ok(new ApiResponse<>(true, "User role removed successfully", null));
    }

}
