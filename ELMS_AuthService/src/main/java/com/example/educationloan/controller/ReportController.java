package com.example.educationloan.controller;

import com.example.educationloan.dto.AuthLogDTO;
import com.example.educationloan.dto.UserDTO;
import com.example.educationloan.entity.User;
import com.example.educationloan.entity.UserRole;
import com.example.educationloan.exception.ReportGenerationException;
import com.example.educationloan.report.AuthLogStore;
import com.example.educationloan.report.JasperReportService;
import com.example.educationloan.report.UserRoleReportRow;
import com.example.educationloan.security.jwt.AuthService;
import com.example.educationloan.service.UserRoleService;
import com.example.educationloan.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "📜Report Generation", description = "User report generation for User,Roles,User-Roles endpoints")
public class ReportController {

    private final JasperReportService reportService;
    private final UserService         userService;
    private final UserRoleService     userRoleService;
    private final AuthService authService;
    private final AuthLogStore authLogStore;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // 1. User List pdf report endpoints----------------------------------------------------------------
    @Operation(
            summary = "📄 Generate User List PDF Report",
            description = """
                    Generate a comprehensive PDF report containing all users in the system.
                    
                    Report Contents:
                    - User ID, Username, Full Name
                    - Email address
                    - Account status (Active/Inactive)
                    - Email verification status
                    - Account creation date
                    - Assigned roles (if any)
                    
                    Use Cases:
                    - Audit and compliance reporting
                    - User account overview for administrators
                    - Data export for external analysis
                    
                    Response:
                    - Content-Type: `application/pdf`
                    - File Download: `UserListReport.pdf`
                    
                    Authorization: Requires valid JWT token (ADMIN or EMPLOYEE role recommended)
                    """,
            parameters = {
                    @Parameter(
                            name = "generatedBy",
                            description = "Name of the person/system generating the report (default: ADMIN)",
                            example = "ADMIN",
                            required = false
                    )
            }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "✅ PDF report generated successfully",
                    content = @Content(
                            mediaType = "application/pdf",
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "🔒 Unauthorized - Invalid or missing JWT token",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "❌ Report generation failed",
                    content = @Content
            )
    })
    //1.User Repot Controller--------------------------------------------------------------------------------------------------
    @GetMapping("/users")
    public ResponseEntity<byte[]> downloadUserListReport(@RequestParam(defaultValue = "ADMIN") String generatedBy) throws JRException, IOException {
        log.info("REQUEST  : GET /api/v1/reports/users | generatedBy={}", generatedBy);
            List<User> users = userService.getAllUsers();
            List<UserDTO> userDTOs = users.stream().map(UserDTO::toUserDTO).collect(Collectors.toList());
            byte[] pdf = reportService.generateUserListReport(userDTOs, generatedBy);
            log.info("RESPONSE : 200 OK | UserListReport generated | records={}", userDTOs.size());
            return pdfResponse(pdf, "UserListReport.pdf");
    }
  //--------------------------------------------------------------------------------------------------------------------








    // 2. User-Roles List PDF REPORT ENDPOINTS--------------------------------------------------------------------------
    @Operation(
            summary = "🎭 Generate User-Roles PDF Report",
            description = """
                    Generate a detailed PDF report showing user-role assignments across the system.
                    
                    Report Contents:
                    - User details (ID, Username, Name, Email)
                    - Assigned role information
                    - Role assignment metadata (who assigned, when assigned)
                    - User account status
                    
                    Filtering Options:
                    - `ALL` - Include all roles (default)
                    - `ADMIN` - Only users with ADMIN role
                    - `EMPLOYEE` - Only users with EMPLOYEE role
                    - `USER` - Only users with USER role
                    
                    Use Cases:
                    - Access control auditing
                    - Role distribution analysis
                    - Compliance reporting
                    - Permission verification
                    
                    Response:
                    - Content-Type: `application/pdf`
                    - File Download: `UserRolesReport.pdf`
                    
                    Authorization: Requires ADMIN role
                    """,
            parameters = {
                    @Parameter(
                            name = "generatedBy",
                            description = "Name of the report generator",
                            example = "System",
                            required = false
                    ),
                    @Parameter(
                            name = "roleFilter",
                            description = "Filter by specific role (ALL, ADMIN, EMPLOYEE, USER)",
                            example = "ALL",
                            required = false
                    )
            }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "✅ PDF report generated successfully",
                    content = @Content(
                            mediaType = "application/pdf",
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "🔒 Unauthorized - Invalid or missing JWT token",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "🚫 Forbidden - Insufficient permissions (ADMIN required)",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "❌ Report generation failed",
                    content = @Content
            )
    })

    // 2. User-Roles-controller-----------------------------------------------------------------------------------------
    @GetMapping("/user-roles")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadUserRolesReport(
            @RequestParam(defaultValue = "System") String generatedBy,
            @RequestParam(defaultValue = "ALL")    String roleFilter) throws JRException, IOException {
        log.info("REQUEST  : GET /api/v1/reports/user-roles | generatedBy={} roleFilter={}", generatedBy, roleFilter);

            List<UserRole> userRoles = userRoleService.getAllUserRoles();
            List<UserRoleReportRow> rows = userRoles.stream()
                    .filter(ur -> "ALL".equalsIgnoreCase(roleFilter)
                                             || ur.getRole().getName().name().equalsIgnoreCase(roleFilter))
                    .map(ur -> UserRoleReportRow.builder()
                            .id(ur.getId())
                            .userId(ur.getUser().getId())
                            .username(ur.getUser().getUsername())
                            .firstName(ur.getUser().getFirstName())
                            .lastName(ur.getUser().getLastName())
                            .email(ur.getUser().getEmail())
                            .isActive(ur.getUser().getIsActive())
                            .roleId(ur.getRole().getRoleId())
                            .roleName(ur.getRole().getName().name())
                            .assignedBy(ur.getAssignedBy())
                            .assignedAt(ur.getAssignedAt())
                            .build())
                    .collect(Collectors.toList());
            byte[] pdf = reportService.generateUserRolesReport(rows, generatedBy, roleFilter);
            log.info("RESPONSE : 200 OK | UserRolesReport generated | records={}", rows.size());
            return pdfResponse(pdf, "UserRolesReport.pdf");
    }
//----------------------------------------------------------------------------------------------------------------------








    // 3.auth-summary REPORT ENDPOINTS----------------------------------------------------------------------------------
    @Operation(
            summary = "🔐 Generate Authentication Summary PDF Report",
            description = """
                    Generate a comprehensive audit report of all authentication events in the system.
                    
                    Report Contents:
                    - Authentication events (LOGIN, REGISTER, REFRESH_TOKEN)
                    - Timestamp of each event
                    - Username and user details
                    - Success/Failure status
                    - IP addresses and user agents
                    - Token expiration details
                    
                    Date Filtering:
                    - Optional date range filtering
                    - Format: `dd-MM-yyyy` (e.g., "01-04-2025")
                    - Use `–` for no date filter (shows all records)
                    
                    Use Cases:
                    - Security audit trails
                    - User activity monitoring
                    - Compliance reporting
                    - Suspicious activity investigation
                    - Login pattern analysis
                    
                    Response:
                    - Content-Type: `application/pdf`
                    - File Download: `AuthSummaryReport.pdf`
                    
                    Authorization: Requires ADMIN role
                    """,
            parameters = {
                    @Parameter(
                            name = "generatedBy",
                            description = "Name of the report generator",
                            example = "System",
                            required = false
                    ),
                    @Parameter(
                            name = "fromDate",
                            description = "Start date for filtering (format: dd-MM-yyyy, or '–' for no filter)",
                            example = "01-04-2025",
                            required = false
                    ),
                    @Parameter(
                            name = "toDate",
                            description = "End date for filtering (format: dd-MM-yyyy, or '–' for no filter)",
                            example = "30-04-2025",
                            required = false
                    )
            }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "✅ PDF report generated successfully",
                    content = @Content(
                            mediaType = "application/pdf",
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "🔒 Unauthorized - Invalid or missing JWT token",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "🚫 Forbidden - Insufficient permissions (ADMIN required)",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "❌ Invalid date format",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "❌ Report generation failed",
                    content = @Content
            )
    })
//3.auth summary controller---------------------------------------------------------------------------------------------
    @GetMapping("/auth-summary")
    public ResponseEntity<byte[]> downloadAuthSummaryReport(
            @RequestParam(defaultValue = "System") String generatedBy,
            @RequestParam(defaultValue = "–")      String fromDate,
            @RequestParam(defaultValue = "–")      String toDate) throws JRException, IOException {
        log.info("REQUEST  : GET /api/v1/reports/auth-summary | generatedBy={} from={} to={}",
                generatedBy, fromDate, toDate);

            List<AuthLogDTO> authLogs = authLogStore.getAll();
            byte[] pdf = reportService.generateAuthSummaryReport(authLogs, generatedBy, fromDate, toDate);
            log.info("RESPONSE : 200 OK | AuthSummaryReport generated | records={}", authLogs.size());
            return pdfResponse(pdf, "AuthSummaryReport.pdf");
    }
//----------------------------------------------------------------------------------------------------------------------







    // JSON for live HTML preview---------------------------------------------------------------------------------------
    //4.auth-summary fetch data for live html preview in admin panel ---------------------------------------------------
    @Operation(
            summary = "📋 Get Authentication Summary Data (JSON)",
            description = """
                    Retrieve authentication log data in JSON format for live previews or custom integrations.
                    
                    Returns:
                    - Array of authentication events
                    - Same data as PDF report but in JSON format
                    
                    Use Cases:
                    - Live dashboard updates
                    - Custom report generation
                    - Data integration with external systems
                    - Real-time monitoring
                    
                    Note: This endpoint returns raw JSON data, not a PDF file.
                    """,
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "✅ Data retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthLogDTO.class),
                                    examples = @ExampleObject(
                                            name = "Authentication Logs",
                                            value = """
                                                    [
                                                      {
                                                        "id": 1,
                                                        "username": "johndoe",
                                                        "operation": "LOGIN",
                                                        "timestamp": "2025-04-09T10:30:00",
                                                        "ipAddress": "192.168.1.100",
                                                        "userAgent": "Mozilla/5.0...",
                                                        "success": true
                                                      }
                                                    ]
                                                    """
                                    )
                            )
                    )
            }
    )
    //4.auth-summary-data controller------------------------------------------------------------------------------------
    @GetMapping("/auth-summary-data")
    public ResponseEntity<List<AuthLogDTO>> getAuthSummaryData() {
        List<AuthLogDTO> logs = authLogStore.getAll();
        return ResponseEntity.ok(logs);
    }
//----------------------------------------------------------------------------------------------------------------------




//5.get user-data-------------------------------------------------------------------------------------------------------
    @Operation(
            summary = "👥 Get Users Data (JSON)",
            description = """
                    Retrieve all user data in JSON format for live previews or custom integrations.
                    
                    Returns:
                    - Array of user objects
                    - Complete user profile information
                    - Same data as PDF report but in JSON format
                    
                    Use Cases:
                    - Live user management dashboards
                    - Custom analytics and reporting
                    - Data export for external systems
                    - User statistics and metrics
                    
                    Note: This endpoint returns raw JSON data, not a PDF file.
                    """,
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "✅ Data retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    )
            }
    )
    //5.user-data-controller ------------------------------------------
    @GetMapping("/users-data")
    public ResponseEntity<List<UserDTO>> getUsersData() {
        List<UserDTO> users = userService.getAllUsers()
                .stream()
                .map(UserDTO::toUserDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }
//----------------------------------------------------------------------------------------------------------------------







//6.fetch user-role data for live jason preview-------------------------------------------------------------------------
    @Operation(
            summary = "🎭 Get User-Roles Data (JSON)",
            description = """
                    Retrieve user-role assignment data in JSON format for live previews or custom integrations.
                    
                    Returns:
                    - Array of user-role mapping objects
                    - Includes user details and role information
                    - Same data as PDF report but in JSON format
                    
                    Filtering:
                    - Optional role filter (ALL, ADMIN, EMPLOYEE, USER)
                    - Default: ALL roles
                    
                    Use Cases:
                    - Role distribution charts
                    - Access control dashboards
                    - Permission analysis
                    - User role verification
                    
                    Note: This endpoint returns raw JSON data, not a PDF file.
                    """,
            parameters = {
                    @Parameter(
                            name = "roleFilter",
                            description = "Filter by specific role (ALL, ADMIN, EMPLOYEE, USER)",
                            example = "ALL",
                            required = false
                    )
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "✅ Data retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserRoleReportRow.class),
                                    examples = @ExampleObject(
                                            name = "User-Role Mappings",
                                            value = """
                                                    [
                                                      {
                                                        "id": 1,
                                                        "userId": 101,
                                                        "username": "johndoe",
                                                        "firstName": "John",
                                                        "lastName": "Doe",
                                                        "email": "john.doe@example.com",
                                                        "isActive": true,
                                                        "roleId": 1,
                                                        "roleName": "ADMIN",
                                                        "assignedBy": "System",
                                                        "assignedAt": "2025-04-01T10:00:00"
                                                      }
                                                    ]
                                                    """
                                    )
                            )
                    )
            }
    )
    //6.User-role fetch controller--------------------------------------------------------------------------------------
    @GetMapping("/user-roles-data")
    @Transactional(readOnly = true)
    public ResponseEntity<List<UserRoleReportRow>> getUserRolesData(
            @RequestParam(defaultValue = "ALL") String roleFilter) {

        List<UserRoleReportRow> rows = userRoleService.getAllUserRoles()
                .stream()
                .filter(ur -> "ALL".equalsIgnoreCase(roleFilter)
                        || ur.getRole().getName().name().equalsIgnoreCase(roleFilter))
                .map(ur -> UserRoleReportRow.builder()
                        .id(ur.getId())
                        .userId(ur.getUser().getId())
                        .username(ur.getUser().getUsername())
                        .firstName(ur.getUser().getFirstName())
                        .lastName(ur.getUser().getLastName())
                        .email(ur.getUser().getEmail())
                        .isActive(ur.getUser().getIsActive())
                        .roleId(ur.getRole().getRoleId())
                        .roleName(ur.getRole().getName().name())
                        .assignedBy(ur.getAssignedBy())
                        .assignedAt(ur.getAssignedAt())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(rows);
    }
//----------------------------------------------------------------------------------------------------------------------




    //7.open report controller in html format for preview---------------------------------------------------------------
    @Operation(
            summary = "🌐 Open Report Viewer (HTML)",
            description = """
                    Redirect to the interactive HTML report viewer interface.
                    
                    Features:
                    - View reports in browser without downloading
                    - Interactive filters and search
                    - Real-time data updates
                    - Export options
                    
                    Authentication:
                    - Automatically passes JWT token to the viewer
                    - Token extracted from Authorization header
                    
                    Redirect:
                    - Target: `/Report-API.html?token={jwt-token}`
                    
                    Note: This is a redirect endpoint, not a data endpoint.
                    """,
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "302",
                            description = "🔀 Redirect to report viewer",
                            content = @Content
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "🔒 Unauthorized - No JWT token provided",
                            content = @Content
                    )
            }
    )
    //6.open report controller------------------------------------------------------------------------------------------
    @GetMapping("/open")
    public void openReport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        response.sendRedirect("/Report-API.html?token=" + token);
    }
//---------------------------------------------------------------------------------------------------------------------


    private ResponseEntity<byte[]> pdfResponse(byte[] pdf, String filename) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(pdf.length))
                .body(pdf);
    }


}
