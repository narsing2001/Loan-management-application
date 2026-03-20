package com.example.educationloan.controller;

import com.example.educationloan.dto.*;
import com.example.educationloan.entity.Role;
import com.example.educationloan.entity.User;
import com.example.educationloan.enumconstant.RoleEnum;
import com.example.educationloan.response.ApiResponse;
import com.example.educationloan.service.UserService;
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

    //1.controller to create/register the new  user---------------------------------------------------------------------
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserDTO>> createUser1(@RequestBody User user) {
        log.info("REQUEST  : POST /api/v1/users/create | email={}", user.getEmail());
        User createdUser = userService.createUser(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName());
        UserDTO response = toUserDTO(createdUser);
        logUserTable("CREATE_USER", response);
        log.info("RESPONSE : 201 CREATED | userId={} created successfully", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true,"User created successfully",response));
    }

  // 2.read the  user by id---------------------------------------------------------------------------------------------
    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(@PathVariable Long id) {
        log.info("REQUEST  : GET /api/v1/users/get/{} | Fetching userId={}", id, id);
        User user = userService.getUserById(id);
        UserDTO response = toUserDTO(user);
        logUserTable("GET_USER_BY_ID", response);
        log.info("RESPONSE : 200 OK | userId={} fetched successfully", id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true,"User Read successfully",response));
    }

    //3.assign multiple roles to the existing user if same role is not exist--------------------------------------------
    @PostMapping("assign/{userId}/roles")
    public ResponseEntity<ApiResponse<UserDTO>> assignRoles(@PathVariable Long userId, @RequestBody List<RoleEnum> roleNames) {
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


    //5.UPDATE--firstname--lastname--password-for-email-update-enter-different-email---------------------------------------------------------------------------------------------------
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<UpdateUserDTO>> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO user) {
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


    //6.DELETE-----------------------------------------------------------------------------------------------------------
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Long>> deleteUser(@PathVariable Long id) {
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

    //7.PATCH - Partial Update ------------------------------------------------------------------------------------------
    @PatchMapping("/patch/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> patchUser(@PathVariable Long id, @RequestBody User user) {
        log.info("REQUEST  : PATCH /api/v1/users/patch/{} | Partial update for userId={}", id, id);
        User patchedUser = userService.patchUser(id, user.getUsername(), user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName());
        user.setIsActive(true);
        UserDTO response = toUserDTO(patchedUser);
        logUserTable("PATCH_USER", response);
        log.info("RESPONSE : 200 OK | userId={} patched successfully", id);
        return ResponseEntity.ok(new ApiResponse<>(true, "User patched successfully", response));
    }

    //get the user by username--------------------------------------------------
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByUsername(@PathVariable String username) {
        log.info("REQUEST  : GET /api/v1/users/username/{} | Fetching user by username={}", username, username);
        User user = userService.getUserByUsername(username);
        UserDTO response = UserDTO.toUserDTO(user);
        logUserTable("GET_USER_BY_USERNAME", response);
        log.info("RESPONSE : 200 OK | username={} found userId={}", username, response.getId());
        return ResponseEntity.ok( new ApiResponse<>(true, "User fetched successfully", response) );
    }

    //get the user detail by email-----------------------------------------------
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByEmail(@PathVariable String email) {
        log.info("REQUEST  : GET /api/v1/users/email/{} | Fetching user by email={}", email, email);
        User user = userService.getUserByEmail(email);
        UserDTO response = UserDTO.toUserDTO(user);
        logUserTable("GET_USER_BY_EMAIL", response);
        log.info("RESPONSE : 200 OK | email={} found userId={}", email, response.getId());
        return ResponseEntity.ok( new ApiResponse<>(true, "User fetched successfully", response) );
    }

  //get the users by roles and status---------------------------------
  @GetMapping("/getByRoleAndStatus/{roleName}/{isActive}")
    public ResponseEntity<ApiResponse<List<UserDTO>>> filterUsersByRoleAndStatus(@PathVariable RoleEnum roleName, @PathVariable Boolean isActive) {
      log.info("REQUEST  : GET /api/v1/users/getByRoleAndStatus/{}/{} | role={} isActive={}", roleName, isActive, roleName, isActive);
        List<User> users = userService.filterUsersByRoleAndStatus(roleName, isActive);
        List<UserDTO> response = users.stream().map(UserDTO::toUserDTO).toList();
      logUserListTable("FILTER_BY_ROLE_AND_STATUS", response);
      log.info("RESPONSE : 200 OK | role={} isActive={} | matchedUsers={}", roleName, isActive, response.size());
        return ResponseEntity.ok(new ApiResponse<>(true, String.format("Filtered users fetched successfully for role %s and status %s", roleName, isActive), response));
        }

  //whether the user with the given is admin or not----------------------
    @GetMapping("/{id}/isAdmin")
    public ResponseEntity<ApiResponse<String>> isUserAdmin(@PathVariable Long id) {
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

//check whether user with the given id is employee or not-------------------
    @GetMapping("/{id}/isEmployee")
    public ResponseEntity<ApiResponse<String>> isUserEmployee(@PathVariable Long id) {
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

    //get a data of particular user id to check if the user has given role provided in the url path--------------------
    @GetMapping("/{id}/hasRole/{roleName}")
    public ResponseEntity<ApiResponse<Boolean>> doesUserHaveRole( @PathVariable Long id, @PathVariable RoleEnum roleName) {
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


    //activate the user of the  given id------------------------------
    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Long>> activateUser(@PathVariable Long id) {
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

    //deactivate the user based on the id
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Long>> deactivateUser(@PathVariable Long id) {
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

    //verify email flag to true by the admin is email is verified-------------------------------------------------------
    @PutMapping("/{id}/verifyEmail")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@PathVariable Long id,@RequestBody EmailDTO email) {

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

    //update the password by the user
    @PutMapping("/{id}/updatePassword")
    public ResponseEntity<ApiResponse<Long>> updatePassword( @PathVariable Long id,@RequestBody PasswordUpdateDTO request) {
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

    // Remove a role from a user based on the id-----------------
    @DeleteMapping("/{id}/roles/{roleName}")
    public ResponseEntity<ApiResponse<Long>> removeRoleFromUser( @PathVariable Long id, @PathVariable RoleEnum roleName) {
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


    // Get all roles for a user
    @GetMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<List<RoleDTO>>> getRolesByUserId(@PathVariable Long id) {
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





