package com.example.educationloan.controllerTest;

import com.example.educationloan.controller.UserController;
import com.example.educationloan.dto.*;
import com.example.educationloan.entity.Role;
import com.example.educationloan.entity.User;
import com.example.educationloan.entity.UserRole;
import com.example.educationloan.enumconstant.RoleEnum;
import com.example.educationloan.exception.*;
import com.example.educationloan.globalexception.GlobalExceptionHandler;
import com.example.educationloan.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("Pure Mockito Tests")
class UserControllerTest {

    @Mock
    UserService userService;

    @InjectMocks
    UserController userController;

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    private User user;
    private Role role;


    private MockHttpServletRequestBuilder getJson(String uri) {
        return get(uri).accept(MediaType.APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder postJson(String uri) {
        return post(uri).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder putJson(String uri) {
        return put(uri).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder patchJson(String uri) {
        return patch(uri).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder deleteJson(String uri) {
        return delete(uri).accept(MediaType.APPLICATION_JSON);
    }
    @BeforeEach
    void init() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();

        role = new Role();
        role.setRoleId(1L);
        role.setName(RoleEnum.USER);
        role.setUserRoles(new HashSet<>());

        UserRole userRole = new UserRole();
        userRole.setId(1L);
        userRole.setRole(role);

        user = new User();
        user.setId(1L);
        user.setUsername("john_doe_1234");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("encodedPassword");
        user.setIsActive(true);
        user.setIsEmailVerified(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUserRoles(new HashSet<>(List.of(userRole)));
        userRole.setUser(user);
    }


    // 1. POST /api/v1/users/create

    @Nested
    @DisplayName("POST /create")
    class CreateUserTests {

        @Test
        @DisplayName("returns 201 CREATED")
        void createUserSuccess() throws Exception {
            when(userService.createUser(anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(user);

            mockMvc.perform(post("/api/v1/users/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"john.doe@example.com\",\"password\":\"pass1234\",\"firstName\":\"John\",\"lastName\":\"Doe\"}"))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.email").value("john.doe@example.com"));
        }

        @Test
        @DisplayName("duplicate email returns 409 CONFLICT")
        void createUserDuplicateEmail() throws Exception {
            when(userService.createUser(anyString(), anyString(), anyString(), anyString()))
                    .thenThrow(new DuplicateResourceException("Email already exists"));

            mockMvc.perform(post("/api/v1/users/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"john.doe@example.com\",\"password\":\"pass1234\",\"firstName\":\"John\",\"lastName\":\"Doe\"}"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Email already exists"));
        }
    }

    // 2. GET /api/v1/users/get/{id}

    @Nested
    @DisplayName("GET /get/{id}")
    class GetUserByIdTests {

        @Test
        @DisplayName("returns user for valid id")
        void getUserValidId() throws Exception {
            when(userService.getUserById(1L)).thenReturn(user);

            mockMvc.perform(get("/api/v1/users/get/1")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.username").value("john_doe_1234"));

            verify(userService).getUserById(1L);
        }

        @Test
        @DisplayName("user not found returns 500")
        void getUserNotFound() throws Exception {
            when(userService.getUserById(99L))
                    .thenThrow(new UserNotFoundException("User not found"));

            mockMvc.perform(get("/api/v1/users/get/99")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())  // 404 instead of 500
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("User not found"));
        }

        @Test
        @DisplayName("non-numeric id returns 400")
        void getUserNonNumericId() throws Exception {
            mockMvc.perform(get("/api/v1/users/get/abc")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").exists());
        }
    }


    // 3. POST /api/v1/users/assign/{userId}/roles

    @Nested
    @DisplayName("POST /assign/{userId}/roles")
    class AssignRolesTests {

        @Test
        @DisplayName("assigns roles returns 200")
        void assignRolesSuccess() throws Exception {
            when(userService.assignRolesUser1(eq(1L), anyList())).thenReturn(user);

            mockMvc.perform(post("/api/v1/users/assign/1/roles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content("[\"USER\",\"ADMIN\"]"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("role already assigned returns 500")
        void assignRolesAlreadyAssigned() throws Exception {
            when(userService.assignRolesUser1(anyLong(), anyList()))
                    .thenThrow(new RoleAlreadyAssignedException("Role already assigned"));

            mockMvc.perform(post("/api/v1/users/assign/1/roles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content("[\"USER\"]"))
                    .andDo(print())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Role already assigned"));
        }

        @Test
        @DisplayName("empty role list calls service")
        void assignRolesEmptyList() throws Exception {
            when(userService.assignRolesUser1(eq(1L), anyList())).thenReturn(user);

            mockMvc.perform(post("/api/v1/users/assign/1/roles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("[]"))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(userService).assignRolesUser1(eq(1L), eq(Collections.emptyList()));
        }
    }

    // 4. GET /api/v1/users/getAll
    @Nested
    @DisplayName("GET /getAll")
    class GetAllUsersTests {

        @Test
        @DisplayName("returns list of users")
        void getAllUsers() throws Exception {
            when(userService.getAllUsers()).thenReturn(List.of(user));

            mockMvc.perform(get("/api/v1/users/getAll")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].id").value(1));
        }

        @Test
        @DisplayName("empty list returns 200 with empty array")
        void getAllUsersEmpty() throws Exception {
            when(userService.getAllUsers()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/users/getAll")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }


    // 5. PUT /api/v1/users/update/{id}

    @Nested
    @DisplayName("PUT /update/{id}")
    class UpdateUserTests {

        @Test
        @DisplayName("updates and returns 200")
        void updateUserSuccess() throws Exception {
            when(userService.updateUser(eq(1L), any(), any(), any(), any(), any()))
                    .thenReturn(user);

            mockMvc.perform(put("/api/v1/users/update/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content("{\"firstName\":\"Jane\",\"email\":\"jane@example.com\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("user not found returns 404 NOT FOUND")
        void updateUserNotFound() throws Exception {
            when(userService.updateUser(anyLong(), any(), any(), any(), any(), any()))
                    .thenThrow(new UserNotFoundException("User not found"));

            mockMvc.perform(put("/api/v1/users/update/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content("{\"firstName\":\"Jane\"}"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("User not found"));
        }

        // 6. DELETE /api/v1/users/delete/{id}
        @Nested
        @DisplayName("DELETE /delete/{id}")
        class DeleteUserTests {

            @Test
            @DisplayName("deletes and returns 200")
            void deleteUserSuccess() throws Exception {
                when(userService.deleteUser(1L)).thenReturn(true);

                mockMvc.perform(delete("/api/v1/users/delete/1")
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").value("User deleted successfully"));
            }

            @Test
            @DisplayName("returns 404 when service returns false")
            void deleteUserNotFound() throws Exception {
                when(userService.deleteUser(99L)).thenReturn(false);

                mockMvc.perform(delete("/api/v1/users/delete/99")
                                .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.success").value(false));
            }
        }

        // 7. PATCH /api/v1/users/patch/{id}
        @Nested
        @DisplayName("PATCH /patch/{id}")
        class PatchUserTests {

            @Test
            @DisplayName("partial update returns 200")
            void patchUserSuccess() throws Exception {
                when(userService.patchUser(eq(1L), any(), any(), any(), any(), any()))
                        .thenReturn(user);

                mockMvc.perform(patch("/api/v1/users/patch/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content("{\"firstName\":\"Updated\"}"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true));
            }

            @Test
            @DisplayName("user not found returns 500")
            void patchUserNotFound() throws Exception {
                when(userService.patchUser(anyLong(), any(), any(), any(), any(), any()))
                        .thenThrow(new UserNotFoundException("User not found"));

                mockMvc.perform(patch("/api/v1/users/patch/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content("{\"firstName\":\"X\"}"))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.message").value("User not found"));
            }
        }


        // 8. GET /api/v1/users/username/{username}
        @Nested
        @DisplayName("GET /username/{username}")
        class GetByUsernameTests {

            @Test
            @DisplayName("returns user by username")
            void getByUsernameSuccess() throws Exception {
                when(userService.getUserByUsername("john_doe_1234")).thenReturn(user);

                mockMvc.perform(get("/api/v1/users/username/john_doe_1234")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.username").value("john_doe_1234"));
            }

            @Test
            @DisplayName("username not found returns 500")
            void getByUsernameNotFound() throws Exception {
                when(userService.getUserByUsername("ghost"))
                        .thenThrow(new UserNotFoundException("User not found"));

                mockMvc.perform(get("/api/v1/users/username/ghost")
                                .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isNotFound())  // 404 instead of 500
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.message").value("User not found"));
            }
        }


        // 9. GET /api/v1/users/email/{email}

        @Nested
        @DisplayName("GET /email/{email}")
        class GetByEmailTests {

            @Test
            @DisplayName("returns user by email")
            void getByEmailSuccess() throws Exception {
                when(userService.getUserByEmail("john.doe@example.com")).thenReturn(user);

                mockMvc.perform(get("/api/v1/users/email/john.doe@example.com")
                                .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.email").value("john.doe@example.com"));
            }

            @Test
            @DisplayName("email not found returns 500")
            void getByEmailNotFound() throws Exception {
                when(userService.getUserByEmail("nobody@example.com"))
                        .thenThrow(new EmailNotFoundException("User not found with email: nobody@example.com"));


                mockMvc.perform(get("/api/v1/users/email/nobody@example.com")
                                .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.message").value("User not found with email: nobody@example.com"))
                        .andExpect(jsonPath("$.data").isEmpty());
            }
        }


        // 10. GET /api/v1/users/getByRoleAndStatus/{roleName}/{isActive}
        @Nested
        @DisplayName("GET /getByRoleAndStatus/{roleName}/{isActive}")
        class FilterByRoleAndStatusTests {

            @Test
            @DisplayName("returns matching users")
            void filterSuccess() throws Exception {
                when(userService.filterUsersByRoleAndStatus(RoleEnum.USER, true))
                        .thenReturn(List.of(user));

                mockMvc.perform(get("/api/v1/users/getByRoleAndStatus/USER/true")
                                .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data").isArray());
            }

            @Test
            @DisplayName("empty list returns 200")
            void filterNoMatch() throws Exception {
                when(userService.filterUsersByRoleAndStatus(RoleEnum.ADMIN, false))
                        .thenReturn(Collections.emptyList());

                mockMvc.perform(getJson("/api/v1/users/getByRoleAndStatus/ADMIN/false"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data").isEmpty());
            }
        }


        // 11. GET /{id}/isAdmin

        @Nested
        @DisplayName("GET /{id}/isAdmin")
        class IsAdminTests {

            @Test
            @DisplayName("returns true")
            void isAdminTrue() throws Exception {
                when(userService.isUserAdmin(1L)).thenReturn(true);

                mockMvc.perform(getJson("/api/v1/users/1/isAdmin"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data").value("User With id:1, isadmin:true"));
            }

            @Test
            @DisplayName("User is not admin – returns false")
            void isAdminFalse() throws Exception {
                when(userService.isUserAdmin(2L)).thenReturn(false);

                mockMvc.perform(getJson("/api/v1/users/2/isAdmin"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data").value("User With id:2, isadmin:false"));
            }
        }


        // 12. GET /{id}/isEmployee

        @Nested
        @DisplayName("GET /{id}/isEmployee")
        class IsEmployeeTests {

            @Test
            @DisplayName("User is employee – returns true")
            void isEmployeeTrue() throws Exception {
                when(userService.isUserEmployee(1L)).thenReturn(true);

                mockMvc.perform(getJson("/api/v1/users/1/isEmployee"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data").value("User with id:1, isEmployee:true"));
            }

            @Test
            @DisplayName("User is not employee – returns false")
            void isEmployeeFalse() throws Exception {
                when(userService.isUserEmployee(3L)).thenReturn(false);

                mockMvc.perform(getJson("/api/v1/users/3/isEmployee"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data").value("User with id:3, isEmployee:false"));
            }
        }

        // 13. GET /{id}/hasRole/{roleName}

        @Nested
        @DisplayName("GET /{id}/hasRole/{roleName}")
        class HasRoleTests {

            @Test
            @DisplayName("User has role – returns true")
            void hasRoleTrue() throws Exception {
                when(userService.doesUserHaveRole(1L, RoleEnum.USER)).thenReturn(true);

                mockMvc.perform(getJson("/api/v1/users/1/hasRole/USER"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data").value(true));
            }

            @Test
            @DisplayName("User does not have role – returns false")
            void hasRoleFalse() throws Exception {
                when(userService.doesUserHaveRole(1L, RoleEnum.ADMIN)).thenReturn(false);

                mockMvc.perform(getJson("/api/v1/users/1/hasRole/ADMIN"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data").value(false));
            }
        }


        // 14. PUT /{id}/activate

        @Nested
        @DisplayName("PUT /{id}/activate")
        class ActivateUserTests {

            @Test
            @DisplayName("Happy Path – activates user returns 200")
            void activateUserSuccess() throws Exception {
                doNothing().when(userService).activateUser(1L);

                mockMvc.perform(putJson("/api/v1/users/1/activate"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.data").value(1));
            }

            @Test
            @DisplayName("user not found returns 404")
            void activateUserNotFound() throws Exception {
                doThrow(new UserNotFoundException("User not found")).when(userService).activateUser(99L);

                mockMvc.perform(putJson("/api/v1/users/99/activate"))
                        .andDo(print())
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.message").value("User not found"));
            }
        }


        // 15. PUT /{id}/deactivate

        @Nested
        @DisplayName("PUT /{id}/deactivate")
        class DeactivateUserTests {

            @Test
            @DisplayName("deactivates user returns 200")
            void deactivateUserSuccess() throws Exception {
                doNothing().when(userService).deactivateUser(1L);

                mockMvc.perform(putJson("/api/v1/users/1/deactivate"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true));
            }

            @Test
            @DisplayName("user not found returns 404")
            void deactivateUserNotFound() throws Exception {
                doThrow(new UserNotFoundException("User not found")).when(userService).deactivateUser(99L);

                mockMvc.perform(putJson("/api/v1/users/99/deactivate"))
                        .andDo(print())
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.message").value("User not found"));
            }
        }

        // 16. PUT /{id}/verifyEmail

        @Nested
        @DisplayName("PUT /{id}/verifyEmail")
        class VerifyEmailTests {

            @Test
            @DisplayName("verifies email returns 200")
            void verifyEmailSuccess() throws Exception {
                doNothing().when(userService).verifyEmail(1L, "john.doe@example.com");

                mockMvc.perform(putJson("/api/v1/users/1/verifyEmail")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"john.doe@example.com\"}"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true));
            }

            @Test
            @DisplayName("email mismatch returns 500")
            void verifyEmailMismatch() throws Exception {
                doThrow(new EmailMismatchException("Email mismatch or email not exist to verify"))
                        .when(userService).verifyEmail(anyLong(), anyString());

                mockMvc.perform(putJson("/api/v1/users/1/verifyEmail")
                                .content("{\"email\":\"wrong@example.com\"}"))
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.message").value("Email mismatch or email not exist to verify"));
            }
        }

        // 17. PUT /{id}/updatePassword
        @Nested
        @DisplayName("PUT /{id}/updatePassword")
        class UpdatePasswordTests {

            @Test
            @DisplayName("updates password returns 200")
            void updatePasswordSuccess() throws Exception {
                doNothing().when(userService).updatePassword(1L, "newPass123");

                mockMvc.perform(putJson("/api/v1/users/1/updatePassword")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"newPassword\":\"newPass123\"}"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true));
            }

            @Test
            @DisplayName("user not found returns 500")
            void updatePasswordNotFound() throws Exception {
                doThrow(new UserNotFoundException("User not found"))
                        .when(userService).updatePassword(anyLong(), anyString());

                mockMvc.perform(putJson("/api/v1/users/99/updatePassword")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"newPassword\":\"pass1234\"}"))
                        .andDo(print())
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.message").value("User not found"));
            }
        }


        // 18. DELETE /{id}/roles/{roleName}

        @Nested
        @DisplayName("DELETE /{id}/roles/{roleName}")
        class RemoveRoleTests {

            @Test
            @DisplayName("removes role returns 200")
            void removeRoleSuccessTest() throws Exception {
                doNothing().when(userService).removeRoleFromUser(1L, RoleEnum.USER);

                mockMvc.perform(deleteJson("/api/v1/users/1/roles/USER"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").exists());  
            }

            @Test
            @DisplayName("removing non-existent role returns 404")
            void removeRoleNotFound() throws Exception {
                doThrow(new RoleMappingNotFoundException("Role want to remove not found/exist with user"))
                        .when(userService).removeRoleFromUser(anyLong(), any());

                mockMvc.perform(deleteJson("/api/v1/users/99/roles/USER"))
                        .andDo(print())
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.message").value("Role want to remove not found/exist with user"));
            }
        }

        // 19. GET /{id}/roles
        @Nested
        @DisplayName("GET /{id}/roles")
        class GetRolesByUserIdTests {

            @Test
            @DisplayName("returns roles for user")
            void getRolesSuccess() throws Exception {
                when(userService.getRolesByUserId(1L)).thenReturn(List.of(role));

                mockMvc.perform(getJson("/api/v1/users/1/roles"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").value("Roles fetched successfully"))
                        .andExpect(jsonPath("$.data").isArray())
                        .andExpect(jsonPath("$.data.length()").value(1))
                        .andExpect(jsonPath("$.data[0].id").value(1))
                        .andExpect(jsonPath("$.data[0].name").value("USER"));
            }

            @Test
            @DisplayName("user with no roles returns empty list")
            void getRolesEmpty() throws Exception {
                when(userService.getRolesByUserId(2L)).thenReturn(Collections.emptyList());

                mockMvc.perform(getJson("/api/v1/users/2/roles"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").value("Roles fetched successfully"))
                        .andExpect(jsonPath("$.data").isArray())
                        .andExpect(jsonPath("$.data").isEmpty());
            }
        }
    }
}
