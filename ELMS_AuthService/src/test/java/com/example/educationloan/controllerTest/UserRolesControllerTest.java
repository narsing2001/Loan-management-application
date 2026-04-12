package com.example.educationloan.controllerTest;


import com.example.educationloan.controller.UserRolesController;
import com.example.educationloan.dto.AssignRoleDTO;
import com.example.educationloan.dto.UserRoleDTO;
import com.example.educationloan.entity.Role;
import com.example.educationloan.entity.User;
import com.example.educationloan.entity.UserRole;
import com.example.educationloan.enumconstant.RoleEnum;
import com.example.educationloan.exception.RoleMappingNotFoundException;
import com.example.educationloan.globalexception.GlobalExceptionHandler;
import com.example.educationloan.service.UserRoleService;
import com.example.educationloan.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
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

@Import(GlobalExceptionHandler.class)
@DisplayName("UserRolesController -api Test cases")
class UserRolesControllerTest {

    @Mock
    UserService     userService;

    @Mock
    UserRoleService userRoleService;

    @InjectMocks
    UserRolesController userRolesController;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    private User     user;
    private Role     role;
    private UserRole userRole;
    private UserRoleDTO userRoleDTO;

    @BeforeEach
    void init() {
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
        mockMvc = MockMvcBuilders.standaloneSetup(userRolesController)
                .setControllerAdvice(exceptionHandler)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();

        role = new Role();
        role.setRoleId(1L);
        role.setName(RoleEnum.USER);
        role.setUserRoles(new HashSet<>());

        user = new User();
        user.setId(1L);
        user.setUsername("john_doe_1234");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setIsActive(true);
        user.setIsEmailVerified(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUserRoles(new HashSet<>());

        userRole = new UserRole();
        userRole.setId(1L);
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setAssignedBy("ADMIN");
        userRole.setAssignedAt(LocalDateTime.now());


        userRoleDTO = new UserRoleDTO();
        userRoleDTO.setUserId(1L);
        userRoleDTO.setRoleName(RoleEnum.valueOf("USER"));
        userRoleDTO.setAssignedBy("ADMIN");
    }


    // 1. GET /api/v1/user_roles/user/{userId}
    @Nested
    @DisplayName("GET /user/{userId}")
    class GetRolesByUserIdTests {

        @Test
        @DisplayName("returns UserRoleDTOs for valid userId")
        void getRolesByUserIdFound() throws Exception {
            when(userRoleService.getUserRolesByUserId(1L)).thenReturn(List.of(userRole));

            mockMvc.perform(get("/api/v1/user_roles/user/1")
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("User roles fetched successfully"))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(1))
                    .andExpect(jsonPath("$.data[0].userId").value(1))
                    .andExpect(jsonPath("$.data[0].roleName").value("USER"))
                   .andExpect(jsonPath("$.data[0].assignedBy").value("ADMIN"));

            verify(userRoleService, times(1)).getUserRolesByUserId(1L);
        }

        @Test
        @DisplayName("user has no roles returns empty list")
        void getRolesByUserIdNoRoles() throws Exception {
            when(userRoleService.getUserRolesByUserId(2L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/user_roles/user/2")
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("User roles fetched successfully"))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data").isEmpty());

        }

        @Test
        @DisplayName("service throws returns 500")
        void getRolesByUserIdServiceThrows() throws Exception {
            when(userRoleService.getUserRolesByUserId(99L))
                    .thenThrow(new RuntimeException("DB error"));

            mockMvc.perform(get("/api/v1/user_roles/user/99")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("DB error"));

            verify(userRoleService, times(1)).getUserRolesByUserId(99L);
        }

        @Test
        @DisplayName("non-numeric userId returns 400")
        void getRolesByUserIdNonNumeric() throws Exception {
            mockMvc.perform(get("/api/v1/user_roles/user/abc"))
                    .andExpect(status().isBadRequest());
        }
    }


    // 2. GET /api/v1/user_roles/role/{roleId}

    @Nested
    @DisplayName("GET /role/{roleId}")
    class GetRolesByRoleIdTests {

        @Test
        @DisplayName(" returns users for given roleId")
        void getRolesByRoleIdFound() throws Exception {
            when(userRoleService.getUserRolesByRoleId(1L)).thenReturn(List.of(userRole));

            mockMvc.perform(get("/api/v1/user_roles/role/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Role mappings fetched successfully"))
                    .andExpect(jsonPath("$.data[0].roleId").value(1));
        }

        @Test
        @DisplayName("no users for roleId returns empty list")
        void getRolesByRoleIdEmpty() throws Exception {
            when(userRoleService.getUserRolesByRoleId(5L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/user_roles/role/5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @Test
        @DisplayName("Exception – service throws returns 500")
        void getRolesByRoleIdServiceThrows() throws Exception {
            when(userRoleService.getUserRolesByRoleId(99L))
                    .thenThrow(new RuntimeException("Role not found"));

            mockMvc.perform(get("/api/v1/user_roles/role/99"))
                    .andExpect(status().isInternalServerError());
        }
    }


    // 3. POST /api/v1/user_roles/assign

    @Nested
    @DisplayName("POST /assign")
    class AssignRoleToUserTests {

        @Test
        @DisplayName("assigns role returns 200")
        void assignRoleSuccess() throws Exception {
            AssignRoleDTO req = new AssignRoleDTO(1L, 1L, "ADMIN");

            when(userService.getUserById(1L)).thenReturn(user);
            when(userService.getRoleById(1L)).thenReturn(role);
            when(userRoleService.giveRoleToUser(user, role, "ADMIN"))
                    .thenReturn(userRole);

            mockMvc.perform(post("/api/v1/user_roles/assign")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Role assigned successfully"))
                    .andExpect(jsonPath("$.data.assignedBy").value("ADMIN"));

            verify(userService).getUserById(1L);
            verify(userService).getRoleById(1L);
            verify(userRoleService).giveRoleToUser(user, role, "ADMIN");
        }

        @Test
        @DisplayName("user not found returns 500")
        void assignRoleUserNotFound() throws Exception {
            AssignRoleDTO req = new AssignRoleDTO(99L, 1L, "ADMIN");

            when(userService.getUserById(anyLong()))
                    .thenThrow(new RuntimeException("User not found"));

            mockMvc.perform(post("/api/v1/user_roles/assign")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("role not found returns 500")
        void assignRoleRoleNotFound() throws Exception {
            AssignRoleDTO req = new AssignRoleDTO(1L, 99L, "ADMIN");

            when(userService.getUserById(1L)).thenReturn(user);
            when(userService.getRoleById(anyLong()))
                    .thenThrow(new RuntimeException("Role not found"));

            mockMvc.perform(post("/api/v1/user_roles/assign")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("duplicate role throws returns 500")
        void assignRoleDuplicate() throws Exception {
            AssignRoleDTO req = new AssignRoleDTO(1L, 1L, "ADMIN");

            when(userService.getUserById(1L)).thenReturn(user);
            when(userService.getRoleById(1L)).thenReturn(role);
            when(userRoleService.giveRoleToUser(any(), any(), anyString()))
                    .thenThrow(new RuntimeException("Role already assigned"));

            mockMvc.perform(post("/api/v1/user_roles/assign")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("null assignedBy is accepted")
        void assignRoleNullAssignedBy() throws Exception {
            AssignRoleDTO req = new AssignRoleDTO(1L, 1L, null);

            when(userService.getUserById(1L)).thenReturn(user);
            when(userService.getRoleById(1L)).thenReturn(role);
            when(userRoleService.giveRoleToUser(user, role, null))
                    .thenReturn(userRole);

            mockMvc.perform(post("/api/v1/user_roles/assign")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk());
        }
    }

    // 4. DELETE /api/v1/user_roles/{userId}/role/{roleId}
    @Nested
    @DisplayName("DELETE /{userId}/role/{roleId}")
    class RemoveUserRoleTests {

        @Test
        @DisplayName("removes role returns 200")
        void removeUserRoleSuccess() throws Exception {
            doNothing().when(userService).removeUserRole1(1L, 1L);

            mockMvc.perform(delete("/api/v1/user_roles/1/role/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("User role removed successfully"));

            verify(userService).removeUserRole1(1L, 1L);
        }

        @Test
        @DisplayName("user not found returns 500")
        void removeUserRoleUserNotFound() throws Exception {
            doThrow(new RuntimeException("User not found"))
                    .when(userService).removeUserRole1(99L, 1L);

            mockMvc.perform(delete("/api/v1/user_roles/99/role/1"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("role mapping not found returns 500")
        void removeUserRoleMappingNotFound() throws Exception {
            doThrow(new RuntimeException("UserRole mapping not found"))
                    .when(userService).removeUserRole1(1L, 5L);

            mockMvc.perform(delete("/api/v1/user_roles/1/role/5"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("role mapping not found returns 404")
        void removeUserRoleMappingNotFound1() throws Exception {
            doThrow(new RoleMappingNotFoundException(1L, 5L))
                    .when(userService).removeUserRole1(1L, 5L);

            mockMvc.perform(delete("/api/v1/user_roles/1/role/5")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())  // 404, not 500
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Role mapping not found for user ID: 1 and role ID: 5"));
        }

        @Test
        @DisplayName("non-numeric ids return 400")
        void removeUserRoleInvalidIds() throws Exception {
            mockMvc.perform(delete("/api/v1/user_roles/abc/role/xyz")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").exists());
        }
    }
}
