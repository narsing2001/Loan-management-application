package com.example.educationloan.controller;

import com.example.educationloan.dto.RoleDTO;
import com.example.educationloan.dto.UserDTO;
import com.example.educationloan.entity.Role;
import com.example.educationloan.entity.User;
import com.example.educationloan.entity.UserRole;
import com.example.educationloan.enumconstant.RoleEnum;
import com.example.educationloan.response.ApiResponse;
import com.example.educationloan.service.RoleService;
import com.example.educationloan.service.UserRoleService;
import com.example.educationloan.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import static com.example.educationloan.dto.UserDTO.toUserDTO;
@Slf4j
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;
    private final UserService userService;
    private final UserRoleService userRoleService;

    // ── log helpers ──────────────────────────────────────────────────────────

    private static final String ROW  = "+------------+----------------------+----------------------+----------------------------------+";
    private static final String HEAD = "| %-10s | %-20s | %-20s | %-32s |";
    private static final String DATA = "| %-10s | %-20s | %-20s | %-32s |";

    private void logUserTable(String operation, UserDTO u, List<RoleDTO> roles) {
        log.info("Operation  : {}", operation);
        log.info("User Details:");
        log.info(ROW);
        log.info(String.format("| %-10s | %-20s | %-20s | %-20s |", "User ID", "First Name", "Last Name", "Email"));
        log.info(ROW);
        log.info(String.format(DATA, u.getId(), u.getFirstName(), u.getLastName(), u.getEmail()));
        log.info(ROW);

        if (roles != null && !roles.isEmpty()) {
            log.info("Assigned Roles:");
            log.info("+------------+----------------------+");
            log.info(String.format("| %-10s | %-20s |", "Role ID", "Role Name"));
            log.info("+------------+----------------------+");
            roles.forEach(r -> log.info(String.format("| %-10s | %-20s |", r.getId(), r.getName())));
            log.info("+------------+----------------------+");
        }
    }




    //1.Get all data and roles assigned to a specific user--------------------------------------------------------------
    @GetMapping("/byUserId/{userId}")
    public ResponseEntity<ApiResponse<?>> getRolesByUserId(@PathVariable Long userId) {
        log.info("REQUEST  : GET /api/v1/roles/byUserId/{} | Fetching roles for userId={}", userId, userId);
        Optional<User> userOptional = userService.getUserById1(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserDTO userDTO = toUserDTO(user);
            // Fetch UserRole mappings for this user
            List<UserRole> userRoles = userRoleService.getUserRolesByUserId(userId);
            // Extract Role objects from UserRole
            List<RoleDTO> roleDTOs = userRoles.stream().map(UserRole::getRole)
                                              .map(role -> new RoleDTO(role.getRoleId(), role.getName())).toList();
            // Attach roles to the UserDTO
            userDTO.setRoles(new HashSet<>(roleDTOs));
            logUserTable("GET_ROLES_BY_USER_ID", userDTO, roleDTOs);
            log.info("RESPONSE : 200 OK | userId={} | rolesCount={}", userId, roleDTOs.size());
            return ResponseEntity.ok(new ApiResponse<>(true, "Roles for user with id " + userId + " fetched successfully", userDTO));
        } else {
            log.warn("RESPONSE : 404 NOT FOUND | userId={} not found", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "User with id " + userId + " not found", null));
        }
    }

    //2.get all data and roles assigned to all user data at once--------------------------------------------------------
    @GetMapping("/getAllData")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsersWithRoles() {
        log.info("REQUEST  : GET /api/v1/roles/getAllData | Fetching all users with roles");
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = users.stream().map(user -> {
            UserDTO userDTO = toUserDTO(user);
            // Fetch roles for each user
            List<UserRole> userRoles = userRoleService.getUserRolesByUserId(user.getId());
            // Map to RoleDTO and attach to UserDTO
            List<RoleDTO> roleDTOs = userRoles.stream()
                    .map(UserRole::getRole).map(role -> new RoleDTO(role.getRoleId(), role.getName())).toList();
            userDTO.setRoles(new HashSet<>(roleDTOs));
            return userDTO;
        }).toList();
        log.info("All Users With Roles:");
        log.info(ROW);
        log.info(String.format(HEAD, "User ID", "First Name", "Last Name", "Email"));
        log.info(ROW);
        userDTOs.forEach(u -> log.info(String.format(DATA, u.getId(), u.getFirstName(), u.getLastName(), u.getEmail())));
        log.info(ROW);
        log.info("RESPONSE : 200 OK | totalUsers={}", userDTOs.size());
        return ResponseEntity.ok(new ApiResponse<>(true, "All users with roles fetched successfully", userDTOs));
    }


    // 3.add or assign  new role to the existing user,if same role is not exist-----------------------------------------
    @PostMapping("/assignRole/{userId}/{roleName}")
    public ResponseEntity<ApiResponse<UserDTO>> assignRoleUser(@PathVariable Long userId, @PathVariable RoleEnum roleName) {
        log.info("REQUEST  : POST /api/v1/roles/assignRole/{}/{} | Assigning role={} to userId={}", userId, roleName, roleName, userId);
        User updatedUserRole=userService.assignRoleToUser(userId, roleName);
        UserDTO userDTO = toUserDTO(updatedUserRole);
        logUserTable("ASSIGN_ROLE", userDTO, null);
        log.info("+------------+----------------------+");
        log.info(String.format("| %-10s | %-20s |", "Assigned", roleName));
        log.info("+------------+----------------------+");
        log.info("RESPONSE : 200 OK | role={} assigned to userId={}", roleName, userId);
        return ResponseEntity.ok(new ApiResponse<>(true,"Role "+ roleName+" assigned to user with id "+userId+" successfully",userDTO));
    }

    //4.update the role assign to the specific user---------------------------------------------------------------------
    @PutMapping("/updateUserRole/{userId}/{oldRole}/{newRole}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserRole(@PathVariable Long userId, @PathVariable RoleEnum oldRole,@PathVariable RoleEnum newRole) {
        log.info("REQUEST  : PUT /api/v1/roles/updateUserRole/{}/{}/{} | userId={} oldRole={} newRole={}", userId, oldRole, newRole, userId, oldRole, newRole);
        User updatedUserRole=roleService.updateUserRole(userId, oldRole,newRole);
        UserDTO userDTO = toUserDTO(updatedUserRole);
        logUserTable("UPDATE_USER_ROLE", userDTO, null);
        log.info("+----------------------+----------------------+");
        log.info(String.format("| %-20s | %-20s |", "Old Role", "New Role"));
        log.info("+----------------------+----------------------+");
        log.info(String.format("| %-20s | %-20s |", oldRole, newRole));
        log.info("+----------------------+----------------------+");
        log.info("RESPONSE : 200 OK | userId={} role updated {} -> {}", userId, oldRole, newRole);
        return ResponseEntity.ok(new ApiResponse<>(true,"User role updated successfully for user with id:"+ userId,userDTO));
    }

    //5.remove role from a specific user--------------------------------------------------------------------------------
    @DeleteMapping("/removeRole/{userId}/{roleName}")
    public ResponseEntity<ApiResponse<UserDTO>> removeRoleFromUser(@PathVariable Long userId, @PathVariable RoleEnum roleName) {
        log.info("REQUEST  : DELETE /api/v1/roles/removeRole/{}/{} | Removing role={} from userId={}", userId, roleName, roleName, userId);
            User updatedUser=roleService.removeRoleFromUser(userId, roleName);
            UserDTO userDTO=toUserDTO(updatedUser);

        logUserTable("REMOVE_ROLE", userDTO, null);
        log.info("+------------+----------------------+");
        log.info(String.format("| %-10s | %-20s |", "Removed", roleName));
        log.info("+------------+----------------------+");
        log.info("RESPONSE : 200 OK | role={} removed from userId={}", roleName, userId);
            return ResponseEntity.ok(new ApiResponse<>(true,"Role " + roleName + " removed from user with id " + userId,userDTO));
    }

    //6.get role id and name by roleName------------------------------------------------------------------------------------------------
    @GetMapping("/{roleName}")
    public ResponseEntity<ApiResponse<Role>> getRoleByName(@PathVariable RoleEnum roleName) {
        log.info("REQUEST  : GET /api/v1/roles/{} | Fetching role by name={}", roleName, roleName);
        return roleService.getByRoleName(roleName).map(roleObj->{
                    log.info("+------------+----------------------+");
                    log.info(String.format("| %-10s | %-20s |", "Role ID", "Role Name"));
                    log.info("+------------+----------------------+");
                    log.info(String.format("| %-10s | %-20s |", roleObj.getRoleId(), roleObj.getName()));
                    log.info("+------------+----------------------+");
                    log.info("RESPONSE : 200 OK | role={} found", roleName);
               return ResponseEntity.ok(new ApiResponse<>(true,"User Role Found",roleObj));
                }).
                orElseGet(()->{
                    log.warn("RESPONSE : 404 NOT FOUND | role={} not found", roleName);
                    return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false,"User Role Not Found",null));
                });
    }

    //7.Get all available roles and its id at once--------------------------------------------------------------------------------
    @GetMapping("/getAllRoles")
    public ResponseEntity<ApiResponse<?>> getAllRoles() {
        log.info("REQUEST  : GET /api/v1/roles/getAllRoles | Fetching all roles");
        List<Role> roles=roleService.getAllRoles();
        log.info("+------------+----------------------+");
        log.info(String.format("| %-10s | %-20s |", "Role ID", "Role Name"));
        log.info("+------------+----------------------+");
        roles.forEach(r -> log.info(String.format("| %-10s | %-20s |", r.getRoleId(), r.getName())));
        log.info("+------------+----------------------+");
        log.info("RESPONSE : 200 OK | totalRoles={}", roles.size());
        return ResponseEntity.ok(new ApiResponse<>(true,"All roles fetched successfully",roles));
    }


    //8. fetch all user based on specific role assign to him------------------------------------------------------------------------------
    @GetMapping("/allWithRole/{roleName}")
    public ResponseEntity<ApiResponse<?>> getUsersWithRole(@PathVariable String roleName) {
        log.info("REQUEST  : GET /api/v1/roles/allWithRole/{} | Fetching users with role={}", roleName, roleName);
        Object result = roleService.getUserWithRole(roleName);
        log.info("RESPONSE : 200 OK | role={} users fetched", roleName);
        return ResponseEntity.ok(new ApiResponse<>(true, "fetch all user with user role" + roleName, result));
    }

    //9.fetch/get all user with specific role---------------------------------------------------------------------------
    @GetMapping("/usersWithRole/{roleName}")
    public ResponseEntity<ApiResponse<?>> getUsersWithSpecificRole(@PathVariable RoleEnum roleName) {
        log.info("REQUEST  : GET /api/v1/roles/allWithRole/{} | Fetching users with role={}", roleName, roleName);
        Object result = roleService.getUserWithRole(String.valueOf(roleName));
        log.info("RESPONSE : 200 OK | role={} users fetched", roleName);
        return ResponseEntity.ok(new ApiResponse<>(true,"fetch all user with role: "+roleName,result));
    }

    //10.Get all roles--------------------------------------------------------------------------------------------------
    @GetMapping("/getAllRoleWithId")
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles1() {
        log.info("REQUEST  : GET /api/v1/roles/getAllRoleWithId | Fetching all roles with IDs");
        List<Role> roles = roleService.getAllRoles();
        log.info("+------------+----------------------+");
        log.info(String.format("| %-10s | %-20s |", "Role ID", "Role Name"));
        log.info("+------------+----------------------+");
        roles.forEach(r -> log.info(String.format("| %-10s | %-20s |", r.getRoleId(), r.getName())));
        log.info("+------------+----------------------+");
        log.info("RESPONSE : 200 OK | totalRoles={}", roles.size());
        return ResponseEntity.ok(new ApiResponse<>(true, "All roles fetched successfully", roles));
    }

    //11.Get roles assigned to a specific user--------------------------------------------------------------------------
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Role>>> getRolesByUser(@PathVariable Long userId) {
        log.info("REQUEST  : GET /api/v1/roles/user/{} | Fetching roles for userId={}", userId, userId);
        List<Role> roles = roleService.getRolesByUserId(userId);
        log.info("+-----------------------------------+");
        log.info(String.format("| %-10s | %-20s |", "Role ID", "Role Name"));
        log.info("+-----------------------------------+");
        roles.forEach(r -> log.info(String.format("| %-10s | %-20s |", r.getRoleId(), r.getName())));
        log.info("+------------+----------------------+");
        log.info("RESPONSE : 200 OK | userId={} | rolesCount={}", userId, roles.size());
        return ResponseEntity.ok(new ApiResponse<>(true, "Roles for user " + userId + " fetched successfully", roles));
    }

     //12.get the user that has the specific role assigned to him or not using the rollNo-------------------------------
    @PostMapping("/{userId}/role/{roleName}")
    public ResponseEntity<ApiResponse<UserDTO>> addRole(@PathVariable Long userId, @PathVariable RoleEnum roleName) {
        log.info("REQUEST  : POST /api/v1/roles/{}/role/{} | Adding role={} to userId={}", userId, roleName, roleName, userId);
        User updatedUser = userService.assignRolesUser1(userId, List.of(roleName));
        UserDTO response = toUserDTO(updatedUser);
        logUserTable("ADD_ROLE", response, null);
        log.info("+--------------------------------------+");
        log.info(String.format("| %-10s | %-20s |", "Added Role", roleName));
        log.info("+--------------------------------------+");
        log.info("RESPONSE : 200 OK | role={} added to userId={}", roleName, userId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "Role added successfully", response));
    }



}
