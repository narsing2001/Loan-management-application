package com.example.educationloan.controller;

import com.example.educationloan.dto.*;
import com.example.educationloan.entity.Role;
import com.example.educationloan.entity.User;
import com.example.educationloan.enumconstant.RoleEnum;
import com.example.educationloan.response.ApiResponse;
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

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.educationloan.dto.UpdateUserDTO.toUpdateUserDTO;
import static com.example.educationloan.dto.UserDTO.toUserDTO;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users in the Education Loan system")
public class UserController {
    private final UserService userService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy hh:mm:ss a").withZone(ZoneId.of("Asia/Kolkata"));
    private static final String ROW  = "+------------+----------------------+----------------------+------------------------------+------------+-----------------+-----------------------------------+";
    private static final String HEAD = "| %-10s | %-20s | %-20s | %-28s | %-10s | %-15s | %-33s |";
    private static final String DATA = "| %-10s | %-20s | %-20s | %-28s | %-10s | %-15s | %-31s |";

    private void logUserTable(String operation, UserDTO u) {
        log.info("Operation : {}", operation);
        log.info(ROW);
        log.info(String.format(HEAD, "User ID", "First Name", "Last Name", "Email", "Active", "Email Verified", "Created At(DD-MM-YY Hour-Min-Sec)"));
        log.info(ROW);
        log.info(String.format(DATA,u.getId(),u.getFirstName(),u.getLastName(),u.getEmail(),u.getIsActive(),u.getIsEmailVerified(),u.getCreatedAt().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("Asia/Kolkata")).format(formatter)));
        log.info(ROW);
    }
    private void logUserListTable(String operation, List<UserDTO> users) {
        log.info("Operation : {}", operation);
        log.info(ROW);
        log.info(String.format(HEAD, "User ID", "First Name", "Last Name", "Email", "Active", "Email Verified", "Created At(DD-MM-YY Hour-Min-Sec)"));
        log.info(ROW);
        users.forEach(u -> log.info(String.format(DATA,u.getId(),u.getFirstName(),u.getLastName(),u.getEmail(),u.getIsActive(),u.getIsEmailVerified(),u.getCreatedAt().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("Asia/Kolkata")).format(formatter))));
        log.info(ROW);
        log.info("Total records: {}", users.size());
    }



    // 1. Create / Register new user ------------------------------------------------------------------------------------
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with email, password, first name, and last name"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input / validation error",
                    content = @Content(schema = @Schema(implementation = com.example.educationloan.response.ApiResponse.class)))
    })
    //1.controller to create/register the new  user---------------------------------------------------------------------
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserDTO>> createUser1(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration details",
                    required = true, content = @Content(schema = @Schema(implementation = User.class)))
            @RequestBody User user) {
        log.info("REQUEST  : POST /api/v1/users/create | email={}", user.getEmail());
        User createdUser = userService.createUser(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName());
        UserDTO response = toUserDTO(createdUser);
        logUserTable("CREATE_USER", response);
        log.info("RESPONSE : 201 CREATED | userId={} created successfully", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true,"User created successfully",response));
    }





    // 2. Get user by ID ------------------------------------------------------------------------------------------------
    @Operation(
            summary = "Get user by ID",
            description = "Fetches a single user's details using their unique ID"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User fetched successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = com.example.educationloan.response.ApiResponse.class)))
    })
  // 2.read the  user by id---------------------------------------------------------------------------------------------
    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(
            @Parameter(description = "Unique ID of the user to fetch", required = true, example = "1")
            @PathVariable Long id) {
        log.info("REQUEST  : GET /api/v1/users/get/{} | Fetching userId={}", id, id);
        User user = userService.getUserById(id);
        UserDTO response = toUserDTO(user);
        logUserTable("GET_USER_BY_ID", response);
        log.info("RESPONSE : 200 OK | userId={} fetched successfully", id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true,"User Read successfully",response));
    }





    // 3. Assign roles to user -----------------------------------------------------------------------------------------
    @Operation(
            summary = "Assign multiple roles to a user",
            description = "Assigns one or more roles to an existing user. Skips roles the user already has"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Roles assigned successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = com.example.educationloan.response.ApiResponse.class)))
    })
    //3.assign multiple roles to the existing user if same role is not exist--------------------------------------------
    @PostMapping("assign/{userId}/roles")
    public ResponseEntity<ApiResponse<UserDTO>> assignRoles(
            @Parameter(description = "ID of the user to assign roles to", required = true, example = "1")
            @PathVariable Long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "List of role names to assign (e.g. ADMIN, EMPLOYEE, USER)",
                    required = true,
                    content = @Content(schema = @Schema(example = "[\"ADMIN\", \"EMPLOYEE\"]"))
            )
            @RequestBody List<RoleEnum> roleNames) {
        log.info("REQUEST  : POST /api/v1/users/assign/{}/roles | roles={}", userId, roleNames);
        User updatedUser = userService.assignRolesUser1(userId, roleNames);
        UserDTO response = toUserDTO(updatedUser);
        logUserTable("ASSIGN_ROLES", response);
        log.info("+----------------------+");
        log.info(String.format("| %-20s |", "Roles Assigned"));
        log.info("+----------------------+");
        roleNames.forEach(r -> log.info(String.format("| %-20s |", r)));
        log.info("+----------------------+");
        log.info("RESPONSE : 200 OK | {} roles assigned to userId={}", roleNames.size(), userId);
        return ResponseEntity.ok(new ApiResponse<>(true,"Roles assigned to user successfully",response));
    }






    // 4. Get all users ------------------------------------------------------------------------------------------------
    @Operation(
            summary = "Get all users",
            description = "Returns a list of all registered users. Role details are excluded from this response"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "All users fetched successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class)))
    })
    //4.Get all users---------------------------------------------------------------------------------------------------
    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        //this logic can be used to show a which roles is assigned to particular user in api response
        //List<UserDTO> response = userService.getAllUsers() .stream() .map(UserDTO::toUserDTO) .toList();
        //this custom user to dto mapping is used for i don't want to show that which roles is assigned to the specific user
        log.info("REQUEST  : GET /api/v1/users/getAll | Fetching all users");
        List<User> users = userService.getAllUsers();
        List<UserDTO> response = users.stream().map(UserDTO::toUserDTO1).collect(Collectors.toList());
        logUserListTable("GET_ALL_USERS", response);
        log.info("RESPONSE : 200 OK | totalUsers={}", response.size());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true,"All User Read Successfully",response));
    }






    // 5. Full update user ---------------------------------------------------------------------------------------------
    @Operation(
            summary = "Update user (full update)",
            description = "Updates firstName, lastName, email, and password for a user. Provide a new email to change it"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UpdateUserDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = com.example.educationloan.response.ApiResponse.class)))
    })
    //5.UPDATE--firstname--lastname--password-for-email-update-enter-different-email------------------------------------
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<UpdateUserDTO>> updateUser(
            @Parameter(description = "ID of the user to update", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated user fields",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateUserDTO.class))
            )
            @RequestBody UpdateUserDTO user) {
        log.info("REQUEST  : PUT /api/v1/users/update/{} | email={} firstName={} lastName={}",id, user.getEmail(), user.getFirstName(), user.getLastName());
        User updatedUser = userService.updateUser(id, null, user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName());
        UpdateUserDTO response = toUpdateUserDTO(updatedUser);
        log.info("Operation : UPDATE_USER");
        log.info("+------------+----------------------+----------------------+------------------------------+");
        log.info(String.format("| %-10s | %-20s | %-20s | %-28s |", "User ID", "First Name", "Last Name", "Email"));
        log.info("+------------+----------------------+----------------------+------------------------------+");
        log.info(String.format("| %-10s | %-20s | %-20s | %-28s |",
                id, response.getFirstName(), response.getLastName(), response.getEmail()));
        log.info("+------------+----------------------+----------------------+------------------------------+");
        log.info("RESPONSE : 200 OK | userId={} updated successfully", id);
        return ResponseEntity.ok(new ApiResponse<>(true,"User Updated Successfully ",response));
    }




    // 6. Delete user --------------------------------------------------------------------------------------------------
    @Operation(
            summary = "Delete user by ID",
            description = "Permanently deletes a user from the system by their ID"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = com.example.educationloan.response.ApiResponse.class)))
    })
    //6.DELETE-----------------------------------------------------------------------------------------------------------
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Long>> deleteUser(
            @Parameter(description = "ID of the user to delete", required = true, example = "1")
            @PathVariable Long id) {
        log.info("REQUEST  : DELETE /api/v1/users/delete/{} | Deleting userId={}", id, id);
        boolean deleted=  userService.deleteUser(id);
        if (!deleted) {
            log.warn("RESPONSE : 404 NOT FOUND | userId={} not found for deletion", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "User not found", id));
        }
        log.info("+------------+----------------------------+");
        log.info(String.format("| %-10s | %-26s |", "User ID", "Status"));
        log.info("+------------+----------------------------+");
        log.info(String.format("| %-10s | %-26s |", id, "DELETED"));
        log.info("+------------+----------------------------+");
        log.info("RESPONSE : 200 OK | userId={} deleted successfully", id);
        return ResponseEntity.ok(new ApiResponse<>(true,"User deleted successfully",id));
    }





    // 7. Patch user ---------------------------------------------------------------------------------------------------
    @Operation(
            summary = "Partial update user (PATCH)",
            description = "Updates only the provided fields of a user. Null fields are ignored"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User patched successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = com.example.educationloan.response.ApiResponse.class)))
    })
    //7.PATCH - Partial Update ------------------------------------------------------------------------------------------
    @PatchMapping("/patch/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> patchUser(
            @Parameter(description = "ID of the user to partially update", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Fields to update (any subset of user fields)",
                    required = true,
                    content = @Content(schema = @Schema(example = "{\"firstName\": \"UpdatedName\"}"))
            )
            @RequestBody User user) {
        log.info("REQUEST  : PATCH /api/v1/users/patch/{} | Partial update for userId={}", id, id);
        User patchedUser = userService.patchUser(id, user.getUsername(), user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName());
        user.setIsActive(true);
        UserDTO response = toUserDTO(patchedUser);
        logUserTable("PATCH_USER", response);
        log.info("RESPONSE : 200 OK | userId={} patched successfully", id);
        return ResponseEntity.ok(new ApiResponse<>(true, "User patched successfully", response));
    }





    // 8. Get user by username -----------------------------------------------------------------------------------------
    @Operation(
            summary = "Get user by username",
            description = "Fetches user details using the username"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = com.example.educationloan.response.ApiResponse.class)))
    })
    //8.get the user by username----------------------------------------------------------------------------------------
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByUsername(
            @Parameter(description = "Username of the user to fetch", required = true, example = "john_doe")
            @PathVariable String username) {
        log.info("REQUEST  : GET /api/v1/users/username/{} | Fetching user by username={}", username, username);
        User user = userService.getUserByUsername(username);
        UserDTO response = UserDTO.toUserDTO(user);
        logUserTable("GET_USER_BY_USERNAME", response);
        log.info("RESPONSE : 200 OK | username={} found userId={}", username, response.getId());
        return ResponseEntity.ok( new ApiResponse<>(true, "User fetched successfully", response) );
    }




    // 9. Get user by email --------------------------------------------------------------------------------------------
    @Operation(
            summary = "Get user by email",
            description = "Fetches user details using the email address"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = com.example.educationloan.response.ApiResponse.class)))
    })
    //9.get the user detail by email-----------------------------------------------
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByEmail(
            @Parameter(description = "Email address of the user to fetch", required = true, example = "john@example.com")
            @PathVariable String email) {
        log.info("REQUEST  : GET /api/v1/users/email/{} | Fetching user by email={}", email, email);
        User user = userService.getUserByEmail(email);
        UserDTO response = UserDTO.toUserDTO(user);
        logUserTable("GET_USER_BY_EMAIL", response);
        log.info("RESPONSE : 200 OK | email={} found userId={}", email, response.getId());
        return ResponseEntity.ok( new ApiResponse<>(true, "User fetched successfully", response) );
    }







    // 10. Filter users by role and status -----------------------------------------------------------------------------
    @Operation(
            summary = "Filter users by role and active status",
            description = "Returns a list of users that match the given role and active/inactive status"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Filtered users fetched successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class)))
    })

  //10.get the users by roles and status--------------------------------------------------------------------------------
  @GetMapping("/getByRoleAndStatus/{roleName}/{isActive}")
    public ResponseEntity<ApiResponse<List<UserDTO>>> filterUsersByRoleAndStatus(
            @Parameter(description = "Role to filter by (e.g. ADMIN, EMPLOYEE, USER)", required = true, example = "ADMIN")
            @PathVariable RoleEnum roleName,
            @Parameter(description = "Active status filter: true for active users, false for inactive", required = true, example = "true")
            @PathVariable Boolean isActive) {
      log.info("REQUEST  : GET /api/v1/users/getByRoleAndStatus/{}/{} | role={} isActive={}", roleName, isActive, roleName, isActive);
        List<User> users = userService.filterUsersByRoleAndStatus(roleName, isActive);
        List<UserDTO> response = users.stream().map(UserDTO::toUserDTO).toList();
      logUserListTable("FILTER_BY_ROLE_AND_STATUS", response);
      log.info("RESPONSE : 200 OK | role={} isActive={} | matchedUsers={}", roleName, isActive, response.size());
        return ResponseEntity.ok(new ApiResponse<>(true, String.format("Filtered users fetched successfully for role %s and status %s", roleName, isActive), response));
        }






    // 11. Check if user is admin --------------------------------------------------------------------------------------
    @Operation(
            summary = "Check if user is an admin",
            description = "Returns whether the user with the given ID has the ADMIN role"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Admin check completed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = com.example.educationloan.response.ApiResponse.class)))
    })
  //11.whether the user with the given is admin or not------------------------------------------------------------------
    @GetMapping("/{id}/isAdmin")
    public ResponseEntity<ApiResponse<String>> isUserAdmin(
            @Parameter(description = "ID of the user to check", required = true, example = "1")
            @PathVariable Long id) {
        log.info("REQUEST  : GET /api/v1/users/{}/isAdmin | Checking admin status for userId={}", id, id);
        boolean isAdmin = userService.isUserAdmin(id);
        log.info("+------------+------------+");
        log.info(String.format("| %-10s | %-10s |", "User ID", "Is Admin"));
        log.info("+------------+------------+");
        log.info(String.format("| %-10s | %-10s |", id, isAdmin));
        log.info("+------------+------------+");
        log.info("RESPONSE : 200 OK | userId={} isAdmin={}", id, isAdmin);
        return ResponseEntity.ok( new ApiResponse<>(true, "Admin check completed", "User With id:"+id+", isadmin:"+isAdmin) );
    }







    // 12. Check if user is employee -----------------------------------------------------------------------------------
    @Operation(
            summary = "Check if user is an employee",
            description = "Returns whether the user with the given ID has the EMPLOYEE role"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Employee check completed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = com.example.educationloan.response.ApiResponse.class)))
    })
//12.check whether user with the given id is employee or not-------------------
    @GetMapping("/{id}/isEmployee")
    public ResponseEntity<ApiResponse<String>> isUserEmployee(
            @Parameter(description = "ID of the user to check", required = true, example = "1")
            @PathVariable Long id) {
        log.info("REQUEST  : GET /api/v1/users/{}/isEmployee | Checking employee status for userId={}", id, id);
        boolean isEmployee = userService.isUserEmployee(id);
        log.info("+------------+--------------+");
        log.info(String.format("| %-10s | %-12s |", "User ID", "Is Employee"));
        log.info("+------------+--------------+");
        log.info(String.format("| %-10s | %-12s |", id, isEmployee));
        log.info("+------------+--------------+");
        log.info("RESPONSE : 200 OK | userId={} isEmployee={}", id, isEmployee);
        return ResponseEntity.ok( new ApiResponse<>(true, "Employee check completed", "User with id:"+id+ ", isEmployee:"+isEmployee) );
    }






    // 13. Check if user has a specific role ----------------------------------------------------------------------------
    @Operation(
            summary = "Check if user has a specific role",
            description = "Returns true if the specified user has the given role, false otherwise"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Role check completed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = com.example.educationloan.response.ApiResponse.class)))
    })
    //13.get a data of particular user id to check if the user has given role provided in the url path--------------------
    @GetMapping("/{id}/hasRole/{roleName}")
    public ResponseEntity<ApiResponse<Boolean>> doesUserHaveRole(
            @Parameter(description = "ID of the user to check", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Role name to check (e.g. ADMIN, EMPLOYEE, USER)", required = true, example = "ADMIN")
            @PathVariable RoleEnum roleName) {
        log.info("REQUEST  : GET /api/v1/users/{}/hasRole/{} | Checking role={} for userId={}", id, roleName, roleName, id);
        boolean hasRole = userService.doesUserHaveRole(id, roleName);
        log.info("+------------+----------------------+------------+");
        log.info(String.format("| %-10s | %-20s | %-10s |", "User ID", "Role", "Has Role"));
        log.info("+------------+----------------------+------------+");
        log.info(String.format("| %-10s | %-20s | %-10s |", id, roleName, hasRole));
        log.info("+------------+----------------------+------------+");
        log.info("RESPONSE : 200 OK | userId={} hasRole={} result={}", id, roleName, hasRole);
        return ResponseEntity.ok( new ApiResponse<>(true, "User with id:"+id+", hasRole"+":"+roleName+"=", hasRole) );
    }







    // 14. Activate user -----------------------------------------------------------------------------------------------
    @Operation(
            summary = "Activate a user",
            description = "Sets the user's active status to true, allowing them to use the system"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User activated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = com.example.educationloan.response.ApiResponse.class)))
    })
    //14.activate the user of the  given id------------------------------
    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Long>> activateUser(
            @Parameter(description = "ID of the user to activate", required = true, example = "1")
            @PathVariable Long id) {
        log.info("REQUEST  : PUT /api/v1/users/{}/activate | Activating userId={}", id, id);
        userService.activateUser(id);
        log.info("+------------+------------+");
        log.info(String.format("| %-10s | %-10s |", "User ID", "Status"));
        log.info("+------------+------------+");
        log.info(String.format("| %-10s | %-10s |", id, "ACTIVATED"));
        log.info("+------------+------------+");
        log.info("RESPONSE : 200 OK | userId={} activated", id);
        return ResponseEntity.ok( new ApiResponse<>(true, "User activated successfully", id) );
    }






    // 15. Deactivate user ---------------------------------------------------------------------------------------------
    @Operation(
            summary = "Deactivate a user",
            description = "Sets the user's active status to false, blocking them from using the system"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deactivated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = com.example.educationloan.response.ApiResponse.class)))
    })
    //15.deactivate the user based on the id
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Long>> deactivateUser(
            @Parameter(description = "ID of the user to deactivate", required = true, example = "1")
            @PathVariable Long id) {
        log.info("REQUEST  : PUT /api/v1/users/{}/deactivate | Deactivating userId={}", id, id);
        userService.deactivateUser(id);
        log.info("+------------+--------------+");
        log.info(String.format("| %-10s | %-12s |", "User ID", "Status"));
        log.info("+------------+--------------+");
        log.info(String.format("| %-10s | %-12s |", id, "DEACTIVATED"));
        log.info("+------------+--------------+");
        log.info("RESPONSE : 200 OK | userId={} deactivated", id);
        return ResponseEntity.ok( new ApiResponse<>(true, "User deactivated successfully", id) );
    }







    // 16. Verify email ------------------------------------------------------------------------------------------------
    @Operation(
            summary = "Verify user email",
            description = "Marks the user's email as verified. Typically called by an admin after confirming the email"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Email verified successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Email mismatch or invalid",
                    content = @Content(schema = @Schema(implementation = com.example.educationloan.response.ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = com.example.educationloan.response.ApiResponse.class)))
    })
    //16.verify email flag to true by the admin is email is verified-------------------------------------------------------
    @PutMapping("/{id}/verifyEmail")
    public ResponseEntity<ApiResponse<String>> verifyEmail(
            @Parameter(description = "ID of the user whose email is to be verified", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Email address to verify",
                    required = true,
                    content = @Content(schema = @Schema(example = "{\"email\": \"user@example.com\"}"))
            )
            @RequestBody EmailDTO email) {

        log.info("REQUEST  : PUT /api/v1/users/{}/verifyEmail | userId={} email={}", id, id, email.getEmail());
        userService.verifyEmail(id,email.getEmail());

        log.info("+------------+------------------------------+-----------------+");
        log.info(String.format("| %-10s | %-28s | %-15s |", "User ID", "Email", "Verified"));
        log.info("+------------+------------------------------+-----------------+");
        log.info(String.format("| %-10s | %-28s | %-15s |", id, email.getEmail(), true));
        log.info("+------------+------------------------------+-----------------+");
        log.info("RESPONSE : 200 OK | userId={} email verified", id);
        return ResponseEntity.ok( new ApiResponse<>(true, "User email verified successfully", "id:"+id+" ,"+"email: "+email.getEmail()) );
    }






    // 17. Update password ---------------------------------------------------------------------------------------------
    @Operation(
            summary = "Update user password",
            description = "Allows a user to update their password by providing a new one"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = com.example.educationloan.response.ApiResponse.class)))
    })
    //17.update the password by the user
    @PutMapping("/{id}/updatePassword")
    public ResponseEntity<ApiResponse<Long>> updatePassword(
            @Parameter(description = "ID of the user whose password is to be updated", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New password payload",
                    required = true,
                    content = @Content(schema = @Schema(example = "{\"newPassword\": \"newSecret456\"}"))
            )
            @RequestBody PasswordUpdateDTO request) {
        log.info("REQUEST  : PUT /api/v1/users/{}/updatePassword | userId={}", id, id);
        userService.updatePassword(id, request.getNewPassword());
        log.info("+------------+------------------------------+");
        log.info(String.format("| %-10s | %-28s |", "User ID", "Status"));
        log.info("+------------+------------------------------+");
        log.info(String.format("| %-10s | %-28s |", id, "PASSWORD UPDATED"));
        log.info("+------------+------------------------------+");
        log.info("RESPONSE : 200 OK | userId={} password updated", id);
        return ResponseEntity.ok( new ApiResponse<>(true, "Password updated successfully", id) );
    }






    // 18. Remove role from user ---------------------------------------------------------------------------------------
    @Operation(
            summary = "Remove a role from a user",
            description = "Removes the specified role from the user. The user must already have that role"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Role removed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found or role not assigned",
                    content = @Content(schema = @Schema(implementation = com.example.educationloan.response.ApiResponse.class)))
    })
    // 18.Remove a role from a user based on the id-----------------
    @DeleteMapping("/{id}/roles/{roleName}")
    public ResponseEntity<ApiResponse<Long>> removeRoleFromUser(
            @Parameter(description = "ID of the user from whom the role will be removed", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Role to remove (e.g. ADMIN, EMPLOYEE, USER)", required = true, example = "ADMIN")
            @PathVariable RoleEnum roleName) {
        log.info("REQUEST  : DELETE /api/v1/users/{}/roles/{} | Removing role={} from userId={}", id, roleName, roleName, id);
        userService.removeRoleFromUser(id, roleName);
        log.info("+------------+----------------------+");
        log.info(String.format("| %-10s | %-20s |", "User ID", "Role Removed"));
        log.info("+------------+----------------------+");
        log.info(String.format("| %-10s | %-20s |", id, roleName));
        log.info("+------------+----------------------+");
        log.info("RESPONSE : 200 OK | role={} removed from userId={}", roleName, id);
        return ResponseEntity.ok( new ApiResponse<>(true, roleName+", Role removed successfully with user having ", id));
    }






    // 19. Get all roles for a user ------------------------------------------------------------------------------------
    @Operation(
            summary = "Get all roles for a user",
            description = "Returns the complete list of roles assigned to the user with the given ID"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Roles fetched successfully",
                    content = @Content(schema = @Schema(implementation = RoleDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = com.example.educationloan.response.ApiResponse.class)))
    })
    // 19.Get all roles for a user
    @GetMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<List<RoleDTO>>> getRolesByUserId(
            @Parameter(description = "ID of the user whose roles are to be fetched", required = true, example = "1")
            @PathVariable Long id) {
        log.info("REQUEST  : GET /api/v1/users/{}/roles | Fetching roles for userId={}", id, id);
        List<Role> roles = userService.getRolesByUserId(id);
        List<RoleDTO> response = roles.stream().map(role -> new RoleDTO(role.getRoleId(), role.getName())) .toList();
        log.info("+------------+----------------------+");
        log.info(String.format("| %-10s | %-20s |", "Role ID", "Role Name"));
        log.info("+------------+----------------------+");
        response.forEach(r -> log.info(String.format("| %-10s | %-20s |", r.getId(), r.getName())));
        log.info("+------------+----------------------+");
        log.info("RESPONSE : 200 OK | userId={} | rolesCount={}", id, response.size());
        return ResponseEntity.ok( new ApiResponse<>(true, "Roles fetched successfully", response) );
    }



}





