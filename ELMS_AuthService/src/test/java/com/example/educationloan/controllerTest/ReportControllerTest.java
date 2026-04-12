package com.example.educationloan.controllerTest;

import com.example.educationloan.controller.ReportController;
import com.example.educationloan.dto.AuthLogDTO;
import com.example.educationloan.dto.UserDTO;
import com.example.educationloan.entity.Role;
import com.example.educationloan.entity.User;
import com.example.educationloan.entity.UserRole;
import com.example.educationloan.enumconstant.RoleEnum;
import com.example.educationloan.exception.ReportGenerationException;
import com.example.educationloan.globalexception.GlobalExceptionHandler;
import com.example.educationloan.report.AuthLogStore;
import com.example.educationloan.report.JasperReportService;
import com.example.educationloan.service.UserRoleService;
import com.example.educationloan.service.UserService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@Import(GlobalExceptionHandler.class)

@DisplayName("ReportController - Pure Mockito Tests")
class ReportControllerTest {


    @Mock
    JasperReportService reportService;

    @Mock
    UserService         userService;

    @Mock
    UserRoleService     userRoleService;

    @Mock
    AuthLogStore        authLogStore;

    @InjectMocks
    ReportController reportController;

    MockMvc mockMvc;

    private User        user;
    private Role        role;
    private UserRole    userRole;
    private UserDTO     userDTO;
    private AuthLogDTO  authLogDTO;
    private final byte[] samplePdfBytes = "PDF_CONTENT".getBytes();


    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(reportController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(
                        new MappingJackson2HttpMessageConverter(),
                        new ByteArrayHttpMessageConverter()
                        )
                .build();

        role = new Role();
        role .setRoleId(1L);
        role .setName(RoleEnum.USER);
        role .setUserRoles(new HashSet<>());

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

        userDTO=UserDTO.toUserDTO(user);

        userRole = new UserRole();
        userRole.setId(1L);
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setAssignedBy("ADMIN");
        userRole.setAssignedAt(LocalDateTime.now());

        authLogDTO = new AuthLogDTO();
        authLogDTO.setUsername("john_doe_1234");
        authLogDTO.setOperation("LOGIN");
        authLogDTO.setSuccess(true);
        authLogDTO.setTimestamp(LocalDateTime.now());




    }
    // test cases for the user report controller --> GET /api/v1/reports/users
    @Nested
    @DisplayName("GET /users")
    class UserListReportTests {

        @Test
        @DisplayName("generates pdf returns 200 with APPLICATION_PDF")
        void downloadUserListReportSuccess() throws Exception {


            when(userService.getAllUsers()).thenReturn(List.of(user));
            when(reportService.generateUserListReport(anyList(), anyString()))
                    .thenReturn(samplePdfBytes);

            mockMvc.perform(get("/api/v1/reports/users")
                            .param("generatedBy", "ADMIN"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                    .andExpect(header().string("Content-Disposition",
                            "attachment; filename=\"UserListReport.pdf\""));
            verify(userService, times(1)).getAllUsers();
            verify(reportService, times(1)).generateUserListReport( anyList(), eq("ADMIN"));
            verifyNoMoreInteractions(userService, reportService);
        }

        @Test
        @DisplayName("default generatedBy param works without providing it")
        void downloadUserListReportSuccessDefault() throws Exception {
            when(userService.getAllUsers()).thenReturn(List.of(user));
            when(reportService.generateUserListReport(anyList(), eq("ADMIN")))
                    .thenReturn(samplePdfBytes);

            mockMvc.perform(get("/api/v1/reports/users"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("empty user list still generates report")
        void downloadUserListReportWithNoUser() throws Exception {
            when(userService.getAllUsers()).thenReturn(Collections.emptyList());
            when(reportService.generateUserListReport(anyList(), anyString()))
                    .thenReturn(samplePdfBytes);

            mockMvc.perform(get("/api/v1/reports/users"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("report service throws returns 500")
        void downloadUserListReportServiceThrows() throws Exception {
            when(userService.getAllUsers()).thenReturn(List.of(user));
            when(reportService.generateUserListReport(anyList(), anyString()))
                    .thenThrow(new ReportGenerationException("Jasper compilation error"));

            mockMvc.perform(get("/api/v1/reports/users"))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Jasper compilation error"));
        }

        @Test
        @DisplayName("template not found throws ReportGenerationException")
        void downloadUserListReportJasperCompilationError() throws Exception {
            when(userService.getAllUsers()).thenReturn(List.of(user));
            when(reportService.generateUserListReport(anyList(), anyString()))
                    .thenThrow(new ReportGenerationException("Report template not found"));

            mockMvc.perform(get("/api/v1/reports/users"))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Report template not found"));
        }

        @Test
        @DisplayName("data binding error throws ReportGenerationException")
        void downloadUserListReportDataBindingError() throws Exception {
            when(userService.getAllUsers()).thenReturn(List.of(user));
            when(reportService.generateUserListReport(anyList(), anyString()))
                    .thenThrow(new ReportGenerationException("Failed to bind data to report"));

            mockMvc.perform(get("/api/v1/reports/users"))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Failed to bind data to report"));
        }




    }

    // 2. GET /api/v1/reports/user-roles-------------------------------------------------

    @Nested
    @DisplayName("GET /user-roles pdf")
    class UserRolesReportTests {

        @Test
        @DisplayName("roleFilter=ALL returns pdf")
        void downloadUserRolesReportAllFilterSuccess() throws Exception {
            when(userRoleService.getAllUserRoles()).thenReturn(List.of(userRole));
            when(reportService.generateUserRolesReport(anyList(), anyString(), anyString()))
                    .thenReturn(samplePdfBytes);

            mockMvc.perform(get("/api/v1/reports/user-roles")
                            .param("generatedBy", "System")
                            .param("roleFilter", "ALL"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                    .andExpect(header().string("Content-Disposition",
                            "attachment; filename=\"UserRolesReport.pdf\""));
        }

        @Test
        @DisplayName("roleFilter=USER filters only USER rows")
        void downloadUserRolesReportFilterByRoleSuccess() throws Exception {
            when(userRoleService.getAllUserRoles()).thenReturn(List.of(userRole));
            when(reportService.generateUserRolesReport(anyList(), anyString(), eq("USER")))
                    .thenReturn(samplePdfBytes);

            mockMvc.perform(get("/api/v1/reports/user-roles")
                            .param("roleFilter", "USER"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                    .andExpect(header().string("Content-Disposition",
                            "attachment; filename=\"UserRolesReport.pdf\""));
        }

        @Test
        @DisplayName("Exception – report service throws returns 500")
        void downloadUserRolesReportServiceThrows() throws Exception {
            when(userRoleService.getAllUserRoles()).thenReturn(List.of(userRole));
            when(reportService.generateUserRolesReport(anyList(), anyString(), anyString()))
                    .thenThrow(new ReportGenerationException("Jasper error"));

            mockMvc.perform(get("/api/v1/reports/user-roles"))
                    .andDo(print())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Jasper error"));
        }
    }


    // 3. GET /api/v1/reports/auth-summary--------------------------------------
    @Nested
    @DisplayName("GET /auth-summary (PDF)")
    class AuthSummaryReportTests {

        @Test
        @DisplayName("generates auth summary pdf")
        void downloadAuthSummaryReportSuccess() throws Exception {
            when(authLogStore.getAll()).thenReturn(List.of(authLogDTO));
            when(reportService.generateAuthSummaryReport(anyList(), anyString(), anyString(), anyString()))
                    .thenReturn(samplePdfBytes);

            mockMvc.perform(get("/api/v1/reports/auth-summary")
                            .param("generatedBy", "System")
                            .param("fromDate", "01-01-2024")
                            .param("toDate", "31-01-2024"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                    .andExpect(header().string("Content-Disposition",
                            "attachment; filename=\"AuthSummaryReport.pdf\""));
        }

        @Test
        @DisplayName("no auth logs still generates report")
        void downloadAuthSummaryReportNoLogsSuccess() throws Exception {
            when(authLogStore.getAll()).thenReturn(Collections.emptyList());
            when(reportService.generateAuthSummaryReport(anyList(), anyString(), anyString(), anyString()))
                    .thenReturn(samplePdfBytes);

            mockMvc.perform(get("/api/v1/reports/auth-summary"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Exception – report service throws returns 500")
        void downloadAuthSummaryReportServiceThrows() throws Exception {
            when(authLogStore.getAll()).thenReturn(List.of(authLogDTO));
            when(reportService.generateAuthSummaryReport(anyList(), anyString(), anyString(), anyString()))
                    .thenThrow(new ReportGenerationException("Jasper error"));

            mockMvc.perform(get("/api/v1/reports/auth-summary"))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Jasper error"));
        }
    }


    // 4. GET /api/v1/reports/auth-summary-data

    @Nested
    @DisplayName("GET /auth-summary-data (JSON)")
    class AuthSummaryDataTests {

        @Test
        @DisplayName("if user login with jwt returns list of AuthLogDTOs as JSON")
        void getAuthSummaryDataSuccess() throws Exception {
            when(authLogStore.getAll()).thenReturn(List.of(authLogDTO));

            mockMvc.perform(get("/api/v1/reports/auth-summary-data")
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].username").value("john_doe_1234"))
                    .andExpect(jsonPath("$[0].operation").value("LOGIN"))
                    .andExpect(jsonPath("$[0].success").value(true));
        }

        @Test
        @DisplayName("if no logs  than returns empty JSON array")
        void getAuthSummaryDataEmpty() throws Exception {
            when(authLogStore.getAll()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/reports/auth-summary-data")
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }


    // 5. GET /api/v1/reports/users-data (JSON)-----------------------------------------

    @Nested
    @DisplayName("GET /users-data")
    class UsersDataTests {

        @Test
        @DisplayName("if particular user data present than returns list of UserDTOs as JSON")
        void getUsersDataSuccess() throws Exception {
            when(userService.getAllUsers()).thenReturn(List.of(user));

            mockMvc.perform(get("/api/v1/reports/users-data")
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));
        }

        @Test
        @DisplayName("if user data is not present/no users returns empty array")
        void getUsersDataEmpty() throws Exception {
            when(userService.getAllUsers()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/reports/users-data")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @DisplayName("service throws returns 500 with error message")
        void getUsersDataServiceThrowsError() throws Exception {
            when(userService.getAllUsers()).thenThrow(new RuntimeException("DB error"));

            mockMvc.perform(get("/api/v1/reports/users-data")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("DB error"));
        }
    }


    // 6. GET /api/v1/reports/user-roles-data------------------------------------------------

    @Nested
    @DisplayName("GET /user-roles-data (JSON)")
    class UserRolesDataTests {

        @Test
        @DisplayName("roleFilter=ALL returns all rows as JSON")
        void getUserRolesDataAll() throws Exception {
            when(userRoleService.getAllUserRoles()).thenReturn(List.of(userRole));

            mockMvc.perform(get("/api/v1/reports/user-roles-data")
                            .param("roleFilter", "ALL")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].username").value("john_doe_1234"))
                    .andExpect(jsonPath("$[0].roleName").value("USER"));
        }

        @Test
        @DisplayName("ADMIN filter returns empty when only USER role exists")
        void getUserRolesDataFilterNoMatch() throws Exception {
            when(userRoleService.getAllUserRoles()).thenReturn(List.of(userRole));

            mockMvc.perform(get("/api/v1/reports/user-roles-data")
                            .param("roleFilter", "ADMIN")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @DisplayName("empty DB returns empty array")
        void getUserRolesDataEmpty() throws Exception {
            when(userRoleService.getAllUserRoles()).thenReturn(Collections.emptyList());
            mockMvc.perform(get("/api/v1/reports/user-roles-data")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @DisplayName("service throws returns 500")
        void getUserRolesDataServiceThrows() throws Exception {
            when(userRoleService.getAllUserRoles())
                    .thenThrow(new RuntimeException("DB connection failed"));

            mockMvc.perform(get("/api/v1/reports/user-roles-data")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("DB connection failed"));
        }
    }
}
