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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Role Management", description = "APIs for assigning, updating, removing and querying user roles")
public class RoleController {
    private final RoleService roleService;
    private final UserService userService;
    private final UserRoleService userRoleService;



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


    // 1. Get all data and roles assigned to a specific user
    @Operation(
            summary = "Get roles by user ID",
            description = "Fetches the user details along with all roles assigned to the specified user."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Roles fetched successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    //1.Get all data and roles assigned to a specific user--------------------------------------------------------------
    @GetMapping("/byUserId/{userId}")
    public ResponseEntity<ApiResponse<?>> getRolesByUserId(
            @Parameter(description = "ID of the user whose roles are to be fetched", required = true, example = "1")
            @PathVariable Long userId) {
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
//----------------------------------------------------------------------------------------------------------------------








    // 2. Get all users with their roles--------------------------------------------------------------------------------
    @Operation(
            summary = "Get all users with roles",
            description = "Returns a list of all users along with all roles assigned to each user."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "All users with roles fetched successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
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
//----------------------------------------------------------------------------------------------------------------------






    // 3. Assign a new role to an existing user-------------------------------------------------------------------------
    @Operation(
            summary = "Assign role to user",
            description = "Assigns the specified role to the user. Does nothing if the user already has this role."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Role assigned successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    // 3.add or assign  new role to the existing user,if same role is not exist-----------------------------------------
    @PostMapping("/assignRole/{userId}/{roleName}")
    public ResponseEntity<ApiResponse<UserDTO>> assignRoleUser(
            @Parameter(description = "ID of the user", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Role to assign (e.g. ADMIN, USER, MANAGER)", required = true, example = "ADMIN")
            @PathVariable RoleEnum roleName) {
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






    // 4. Update a role assigned to a specific user---------------------------------------------------------------------
    @Operation(
            summary = "Update user role",
            description = "Replaces the old role with a new role for the specified user."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Role updated successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    //4.update the role assign to the specific user---------------------------------------------------------------------
    @PutMapping("/updateUserRole/{userId}/{oldRole}/{newRole}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserRole(
            @Parameter(description = "ID of the user", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Existing role to be replaced", required = true, example = "USER")
            @PathVariable RoleEnum oldRole,
            @Parameter(description = "New role to assign", required = true, example = "ADMIN")
            @PathVariable RoleEnum newRole) {
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







    // 5. Remove a role from a specific user----------------------------------------------------------------------------
    @Operation(
            summary = "Remove role from user",
            description = "Removes the specified role from the user's assigned roles."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Role removed successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    //5.remove role from a specific user--------------------------------------------------------------------------------
    @DeleteMapping("/removeRole/{userId}/{roleName}")
    public ResponseEntity<ApiResponse<UserDTO>> removeRoleFromUser(
            @Parameter(description = "ID of the user", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Role to remove from the user", required = true, example = "ADMIN")
            @PathVariable RoleEnum roleName) {
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






    // 6. Get role ID and name by role name-----------------------------------------------------------------------------
    @Operation(
            summary = "Get role by name",
            description = "Returns the role ID and name for the given role enum value."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Role found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Role not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    //6.get role id and name by roleName--------------------------------------------------------------------------------
    @GetMapping("/{roleName}")
    public ResponseEntity<ApiResponse<Role>> getRoleByName(
            @Parameter(description = "Role name to look up", required = true, example = "ADMIN")
            @PathVariable RoleEnum roleName) {
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





    // 7. Get all available roles---------------------------------------------------------------------------------------
    @Operation(
            summary = "Get all roles",
            description = "Returns a list of all roles available in the system with their IDs."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "All roles fetched successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    //7.Get all available roles and its id at once----------------------------------------------------------------------
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





    // 8. Fetch all users based on a specific role (String)-------------------------------------------------------------
    @Operation(
            summary = "Get users with a role (String)",
            description = "Returns all users who have been assigned the specified role. Role name is accepted as a plain String."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users fetched successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    //8. fetch all user based on specific role assign to him------------------------------------------------------------
    @GetMapping("/allWithRole/{roleName}")
    public ResponseEntity<ApiResponse<?>> getUsersWithRole(
            @Parameter(description = "Role name as a plain string (e.g. ADMIN)", required = true, example = "ADMIN")
            @PathVariable String roleName) {
        log.info("REQUEST  : GET /api/v1/roles/allWithRole/{} | Fetching users with role={}", roleName, roleName);
        Object result = roleService.getUserWithRole(roleName);
        log.info("RESPONSE : 200 OK | role={} users fetched", roleName);
        return ResponseEntity.ok(new ApiResponse<>(true, "fetch all user with user role" + roleName, result));
    }





    // 9. Fetch all users with a specific role (Enum)-------------------------------------------------------------------
    @Operation(
            summary = "Get users with a role (Enum)",
            description = "Returns all users who have been assigned the specified role. Role name is accepted as a RoleEnum value."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users fetched successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    //9.fetch/get all user with specific role---------------------------------------------------------------------------
    @GetMapping("/usersWithRole/{roleName}")
    public ResponseEntity<ApiResponse<?>> getUsersWithSpecificRole(
            @Parameter(description = "Role name from RoleEnum", required = true, example = "ADMIN")
            @PathVariable RoleEnum roleName) {
        log.info("REQUEST  : GET /api/v1/roles/allWithRole/{} | Fetching users with role={}", roleName, roleName);
        Object result = roleService.getUserWithRole(String.valueOf(roleName));
        log.info("RESPONSE : 200 OK | role={} users fetched", roleName);
        return ResponseEntity.ok(new ApiResponse<>(true,"fetch all user with role: "+roleName,result));
    }





    // 10. Get all roles with IDs---------------------------------------------------------------------------------------
    @Operation(
            summary = "Get all roles with IDs",
            description = "Returns all roles in the system along with their database IDs."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "All roles fetched successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
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





    // 11. Get roles assigned to a specific user------------------------------------------------------------------------
    @Operation(
            summary = "Get roles for a user",
            description = "Returns the list of Role objects (with ID and name) currently assigned to the given user."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Roles fetched successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    //11.Get roles assigned to a specific user--------------------------------------------------------------------------
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Role>>> getRolesByUser(
            @Parameter(description = "ID of the user", required = true, example = "1")
            @PathVariable Long userId) {
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





    // 12. Add role to user (checks if already assigned)---------------------------------------------------------------
    @Operation(
            summary = "Add role to user",
            description = "Adds the given role to the user. Checks whether the role is already assigned before adding."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Role added successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
     //12.get the user that has the specific role assigned to him or not using the rollNo-------------------------------
    @PostMapping("/{userId}/role/{roleName}")
    public ResponseEntity<ApiResponse<UserDTO>> addRole(
            @Parameter(description = "ID of the user", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Role to add to the user", required = true, example = "MANAGER")
            @PathVariable RoleEnum roleName) {
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
