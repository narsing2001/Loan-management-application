package com.example.educationloan.controllerTest;

import com.example.educationloan.controller.RoleController;
import com.example.educationloan.entity.Role;
import com.example.educationloan.entity.User;
import com.example.educationloan.entity.UserRole;
import com.example.educationloan.enumconstant.RoleEnum;
import com.example.educationloan.exception.RoleAlreadyAssignedException;
import com.example.educationloan.exception.UserNotFoundException;
import com.example.educationloan.globalexception.GlobalExceptionHandler;
import com.example.educationloan.service.RoleService;
import com.example.educationloan.service.UserRoleService;
import com.example.educationloan.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("RoleController - Pure Mockito Tests")
class RoleControllerTest {

    @Mock
    RoleService roleService;

    @Mock
    UserService userService;

    @Mock
    UserRoleService userRoleService;

    @InjectMocks
    RoleController roleController;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    private User sampleUser;
    private Role sampleRole;
    private UserRole sampleUserRole;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roleController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();

        sampleRole = new Role();
        sampleRole.setRoleId(1L);
        sampleRole.setName(RoleEnum.USER);
        sampleRole.setUserRoles(new HashSet<>());

        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("john_doe_1234");
        sampleUser.setFirstName("John");
        sampleUser.setLastName("Doe");
        sampleUser.setEmail("john.doe@example.com");
        sampleUser.setIsActive(true);
        sampleUser.setIsEmailVerified(false);
        sampleUser.setCreatedAt(LocalDateTime.now());
        sampleUser.setUpdatedAt(LocalDateTime.now());
        sampleUser.setUserRoles(new HashSet<>());

        sampleUserRole = new UserRole();
        sampleUserRole.setId(1L);
        sampleUserRole.setUser(sampleUser);
        sampleUserRole.setRole(sampleRole);
        sampleUserRole.setAssignedBy("ADMIN");
    }


    // 1. GET /byUserId/{userId}

    @Nested
    @DisplayName("GET /byUserId/{userId}")
    class GetRolesByUserIdTests {

        @Test
        @DisplayName("user found with roles returns 200")
        void getRolesByUserIdSuccess() throws Exception {
            when(userService.getUserById1(1L)).thenReturn(Optional.of(sampleUser));
            when(userRoleService.getUserRolesByUserId(1L)).thenReturn(List.of(sampleUserRole));

            mockMvc.perform(get("/api/v1/roles/byUserId/1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.username").value("john_doe_1234"))
                    .andExpect(jsonPath("$.data.roles").isArray())
                    .andExpect(jsonPath("$.data.roles").isNotEmpty());

            verify(userService, times(1)).getUserById1(1L);
            verify(userRoleService, times(1)).getUserRolesByUserId(1L);
        }

        @Test
        @DisplayName("user not found returns 404")
        void getRolesByUserIdNotFound() throws Exception {
            when(userService.getUserById1(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/v1/roles/byUserId/99"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").exists());

            verify(userService, times(1)).getUserById1(99L);
            verify(userRoleService, never()).getUserRolesByUserId(anyLong());
        }

        @Test
        @DisplayName("user exists but has no roles returns 200 with empty roles")
        void getRolesByUserIdNoRoles() throws Exception {
            when(userService.getUserById1(1L)).thenReturn(Optional.of(sampleUser));
            when(userRoleService.getUserRolesByUserId(1L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/roles/byUserId/1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.roles").isArray())
                    .andExpect(jsonPath("$.data.roles").isEmpty());

            verify(userService, times(1)).getUserById1(1L);
            verify(userRoleService, times(1)).getUserRolesByUserId(1L);
        }
    }


    // 2. GET /getAllData

    @Nested
    @DisplayName("GET /getAllData")
    class GetAllDataTests {

        @Test
        @DisplayName("returns all users with roles")
        void getAllDataSuccess() throws Exception {
            when(userService.getAllUsers()).thenReturn(List.of(sampleUser));
            when(userRoleService.getUserRolesByUserId(1L)).thenReturn(List.of(sampleUserRole));

            mockMvc.perform(get("/api/v1/roles/getAllData"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].id").value(1));

            verify(userService, times(1)).getAllUsers();
        }

        @Test
        @DisplayName("empty database returns empty list")
        void getAllDataEmpty() throws Exception {
            when(userService.getAllUsers()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/roles/getAllData"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isEmpty());

            verify(userService, times(1)).getAllUsers();
        }
    }


    // 3. POST /assignRole/{userId}/{roleName}

    @Nested
    @DisplayName("POST /assignRole/{userId}/{roleName}")
    class AssignRoleTests {

        @Test
        @DisplayName("successfully assigns role returns 200")
        void assignRoleSuccess() throws Exception {
            when(userService.assignRoleToUser(1L, RoleEnum.ADMIN)).thenReturn(sampleUser);

            mockMvc.perform(post("/api/v1/roles/assignRole/1/ADMIN"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").exists());

            verify(userService, times(1)).assignRoleToUser(1L, RoleEnum.ADMIN);
        }

        @Test
        @DisplayName("user not found returns 404 with error message")
        void assignRoleUserNotFound() throws Exception {
            when(userService.assignRoleToUser(99L, RoleEnum.ADMIN))
                    .thenThrow(new UserNotFoundException("User not found"));

            mockMvc.perform(post("/api/v1/roles/assignRole/99/ADMIN"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("User not found"));

            verify(userService, times(1)).assignRoleToUser(99L, RoleEnum.ADMIN);
        }
    }


    // 4. PUT /updateUserRole/{userId}/{oldRole}/{newRole}

    @Nested
    @DisplayName("PUT /updateUserRole/{userId}/{oldRole}/{newRole}")
    class UpdateUserRoleTests {

        @Test
        @DisplayName("successfully updates role returns 200")
        void updateUserRoleSuccess() throws Exception {
            when(roleService.updateUserRole(1L, RoleEnum.USER, RoleEnum.ADMIN)).thenReturn(sampleUser);

            mockMvc.perform(put("/api/v1/roles/updateUserRole/1/USER/ADMIN"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true));

            verify(roleService, times(1)).updateUserRole(1L, RoleEnum.USER, RoleEnum.ADMIN);
        }

        @Test
        @DisplayName("role not assigned returns 500 with error message")
        void updateUserRoleNotAssigned() throws Exception {
            when(roleService.updateUserRole(1L, RoleEnum.USER, RoleEnum.ADMIN))
                    .thenThrow(new RuntimeException("Role not assigned"));

            mockMvc.perform(put("/api/v1/roles/updateUserRole/1/USER/ADMIN"))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Role not assigned"));

            verify(roleService, times(1)).updateUserRole(1L, RoleEnum.USER, RoleEnum.ADMIN);
        }
    }


    // 5. DELETE /removeRole/{userId}/{roleName}

    @Nested
    @DisplayName("DELETE /removeRole/{userId}/{roleName}")
    class RemoveRoleTests {

        @Test
        @DisplayName("successfully removes role returns 200")
        void removeRoleSuccess() throws Exception {
            when(roleService.removeRoleFromUser(1L, RoleEnum.USER)).thenReturn(sampleUser);

            mockMvc.perform(delete("/api/v1/roles/removeRole/1/USER"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true));

            verify(roleService, times(1)).removeRoleFromUser(1L, RoleEnum.USER);
        }

        @Test
        @DisplayName("cannot remove last role returns 500 with error message")
        void removeRoleLastRole() throws Exception {
            when(roleService.removeRoleFromUser(1L, RoleEnum.USER))
                    .thenThrow(new RuntimeException("Cannot remove last role"));

            mockMvc.perform(delete("/api/v1/roles/removeRole/1/USER"))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Cannot remove last role"));

            verify(roleService, times(1)).removeRoleFromUser(1L, RoleEnum.USER);
        }
    }

    // 6. GET /{roleName}

    @Nested
    @DisplayName("GET /{roleName}")
    class GetRoleByNameTests {

        @Test
        @DisplayName("role found returns 200")
        void getRoleByNameFound() throws Exception {
            when(roleService.getByRoleName(RoleEnum.USER)).thenReturn(Optional.of(sampleRole));

            mockMvc.perform(get("/api/v1/roles/USER"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("User Role Found"))
                    .andExpect(jsonPath("$.data.roleId").value(1))
                    .andExpect(jsonPath("$.data.name").value("USER"));

            verify(roleService, times(1)).getByRoleName(RoleEnum.USER);
        }

        @Test
        @DisplayName("role not found returns 404")
        void getRoleByNameNotFound() throws Exception {
            when(roleService.getByRoleName(RoleEnum.ADMIN)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/v1/roles/ADMIN"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").exists());

            verify(roleService, times(1)).getByRoleName(RoleEnum.ADMIN);
        }
    }


    // 7. GET /getAllRoles

    @Nested
    @DisplayName("GET /getAllRoles")
    class GetAllRolesTests {

        @Test
        @DisplayName("returns all roles")
        void getAllRolesSuccess() throws Exception {
            when(roleService.getAllRoles()).thenReturn(List.of(sampleRole));

            mockMvc.perform(get("/api/v1/roles/getAllRoles"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].roleId").value(1))
                    .andExpect(jsonPath("$.data[0].name").value("USER"));

            verify(roleService, times(1)).getAllRoles();
        }

        @Test
        @DisplayName("empty database returns empty array")
        void getAllRolesEmpty() throws Exception {
            when(roleService.getAllRoles()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/roles/getAllRoles"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isEmpty());

            verify(roleService, times(1)).getAllRoles();
        }
    }


    // 8. GET /user/{userId}

    @Nested
    @DisplayName("GET /user/{userId}")
    class GetRolesByUserTests {

        @Test
        @DisplayName("returns roles for user")
        void getRolesByUserSuccess() throws Exception {
            when(roleService.getRolesByUserId(1L)).thenReturn(List.of(sampleRole));

            mockMvc.perform(get("/api/v1/roles/user/1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].roleId").value(1))
                    .andExpect(jsonPath("$.data[0].name").value("USER"));

            verify(roleService, times(1)).getRolesByUserId(1L);
        }

        @Test
        @DisplayName("user with no roles returns empty list")
        void getRolesByUserEmpty() throws Exception {
            when(roleService.getRolesByUserId(2L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/roles/user/2"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isEmpty());

            verify(roleService, times(1)).getRolesByUserId(2L);
        }
    }

    // 9. POST /{userId}/role/{roleName}

    @Nested
    @DisplayName("POST /{userId}/role/{roleName}")
    class AddRoleTests {

        @Test
        @DisplayName("successfully adds role returns 200")
        void addRoleSuccess() throws Exception {
            when(userService.assignRolesUser1(eq(1L), anyList())).thenReturn(sampleUser);

            mockMvc.perform(post("/api/v1/roles/1/role/USER"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Role added successfully"));

            verify(userService, times(1)).assignRolesUser1(eq(1L), anyList());
        }

        @Test
        @DisplayName("role already assigned returns 500 with error message")
        void addRoleAlreadyAssigned() throws Exception {
            when(userService.assignRolesUser1(eq(1L), anyList()))
                    .thenThrow(new RoleAlreadyAssignedException("Role already assigned"));

            mockMvc.perform(post("/api/v1/roles/1/role/ADMIN"))
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Role already assigned"));

            verify(userService, times(1)).assignRolesUser1(eq(1L), anyList());
        }
    }
}