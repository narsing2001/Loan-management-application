package com.example.LoanTypeStrategyInterest.LoanInterestRateControllerTest;

import com.example.LoanTypeStrategyInterest.config.WebConfig;
import com.example.LoanTypeStrategyInterest.controller.UserApplicationDetailController;
import com.example.LoanTypeStrategyInterest.converter.StringToApplicationStatusConverter;
import com.example.LoanTypeStrategyInterest.dto.dto1.*;
import com.example.LoanTypeStrategyInterest.enums.enum1.*;
import com.example.LoanTypeStrategyInterest.exception.InvalidStatusException;
import com.example.LoanTypeStrategyInterest.globalexception.GlobalExceptionHandler;
import com.example.LoanTypeStrategyInterest.globalexception.GlobalExceptionHandler1;
import com.example.LoanTypeStrategyInterest.service.serv1.InterestRateCalculationService;
import com.example.LoanTypeStrategyInterest.service.serv1.UserApplicationDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@Import( {GlobalExceptionHandler1.class, GlobalExceptionHandler1.class,WebConfig.class, StringToApplicationStatusConverter.class})
@DisplayName("UserApplicationDetailController Tests")
class UserApplicationDetailControllerTest {

    @Mock
    private UserApplicationDetailService userApplicationDetailService;

    @Mock
    private InterestRateCalculationService interestRateCalculationService;

    @InjectMocks
    private UserApplicationDetailController userApplicationDetailController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    // --- Valid Request DTO (DOMESTIC loan) ------------------------------------------------------
    private UserApplicationRequestDTO validRequestDTO;
    private UserApplicationResponseDTO validResponseDTO;
    private UUID testApplicationId;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userApplicationDetailController)
                .setConversionService(createConversionService())
                .setControllerAdvice( new GlobalExceptionHandler1(),new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testApplicationId = UUID.randomUUID();

        validRequestDTO = new UserApplicationRequestDTO();
        validRequestDTO.setApplicantName("Narsing Patil");
        validRequestDTO.setApplicantEmail("narsingpatil@gmail.com");
        validRequestDTO.setGender(Gender.MALE);
        validRequestDTO.setLoanType(LoanType.DOMESTIC);
        validRequestDTO.setCourseType(CourseType.UNDERGRADUATE);
        validRequestDTO.setDomesticCourseType(DomesticCourseType.ENGINEERING);
        validRequestDTO.setRequestedAmount(new BigDecimal("500000"));
        validRequestDTO.setTenureYears(10);

        //Valid Response DTO ---------------------------------------------------------------------------------
        validResponseDTO = UserApplicationResponseDTO.builder()
                .id(testApplicationId)
                .applicantName("Narsing Patil")
                .applicantEmail("narsingpatil@gmail.com")
                .gender(Gender.MALE)
                .loanType(LoanType.DOMESTIC)
                .courseType(CourseType.UNDERGRADUATE)
                .domesticCourseType(DomesticCourseType.ENGINEERING)
                .requestedAmount(new BigDecimal("500000"))
                .approvedAmount(new BigDecimal("450000"))
                .tenureYears(10)
                .applicationStatus(ApplicationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    private DefaultFormattingConversionService createConversionService() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
        conversionService.addConverter(new StringToApplicationStatusConverter());
        return conversionService;
    }

    // Submit Application Tests-------------------------------------------------------------------------
    @Nested
    @DisplayName("POST /api/v1/loans/post/apply - Submit Loan Application")
    class SubmitApplicationTests {

        @Test
        @DisplayName("Should submit a valid DOMESTIC loan application and return 201 CREATED")
        void testSubmitApplicationValidDomesticLoan() throws Exception {
            when(userApplicationDetailService.applyLoan(any(UserApplicationRequestDTO.class))).thenReturn(validResponseDTO);

            mockMvc.perform(post("/api/v1/loans/post/apply")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.message", is("Loan application submitted successfully...!")))
                    .andExpect(jsonPath("$.data.applicantName", is("Narsing Patil")))
                    .andExpect(jsonPath("$.data.applicantEmail", is("narsingpatil@gmail.com")))
                    .andExpect(jsonPath("$.data.loanType", is("DOMESTIC")))
                    .andExpect(jsonPath("$.data.applicationStatus", is("PENDING")));

            verify(userApplicationDetailService, times(1)).applyLoan(any(UserApplicationRequestDTO.class));
        }

        @Test
        @DisplayName("Should submit a valid INTERNATIONAL loan application and return 201 CREATED")
        void testSubmitApplicationValidInternationalLoan() throws Exception {
            validRequestDTO.setLoanType(LoanType.INTERNATIONAL);
            validRequestDTO.setDomesticCourseType(null);
            validRequestDTO.setInternationalCourseType(InternationalCourseType.MBA);

            UserApplicationResponseDTO internationalResponse = UserApplicationResponseDTO.builder()
                    .id(UUID.randomUUID())
                    .applicantName("John Doe")
                    .loanType(LoanType.INTERNATIONAL)
                    .applicationStatus(ApplicationStatus.PENDING)
                    .build();

            when(userApplicationDetailService.applyLoan(any(UserApplicationRequestDTO.class))).thenReturn(internationalResponse);

            mockMvc.perform(post("/api/v1/loans/post/apply")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.loanType", is("INTERNATIONAL")))
                    .andExpect(jsonPath("$.data.applicationStatus", is("PENDING")));

            verify(userApplicationDetailService, times(1)).applyLoan(any(UserApplicationRequestDTO.class));
        }

        @Test
        @DisplayName("Should return 400 BAD_REQUEST when email is invalid")
        void testSubmitApplicationInvalidEmail() throws Exception {
            validRequestDTO.setApplicantEmail("not-a-valid-email");

            mockMvc.perform(post("/api/v1/loans/post/apply")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(userApplicationDetailService, never()).applyLoan(any());
        }

        @Test
        @DisplayName("Should return 400 BAD_REQUEST when applicantName is blank")
        void testSubmitApplicationBlankApplicantName() throws Exception {
            validRequestDTO.setApplicantName("");

            mockMvc.perform(post("/api/v1/loans/post/apply")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(userApplicationDetailService, never()).applyLoan(any());
        }

        @Test
        @DisplayName("Should return 400 BAD_REQUEST when requestedAmount is below minimum (100000)")
        void testSubmitApplicationAmountBelowMinimum() throws Exception {
            validRequestDTO.setRequestedAmount(new BigDecimal("50000"));

            mockMvc.perform(post("/api/v1/loans/post/apply")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(userApplicationDetailService, never()).applyLoan(any());
        }

        @Test
        @DisplayName("Should return 400 BAD_REQUEST when tenureYears exceeds 15")
        void testSubmitApplicationTenureExceedsMax() throws Exception {
            validRequestDTO.setTenureYears(20);

            mockMvc.perform(post("/api/v1/loans/post/apply")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(userApplicationDetailService, never()).applyLoan(any());
        }

        @Test
        @DisplayName("Should return 400 BAD_REQUEST when tenureYears is less than 1")
        void testSubmitApplicationTenureBelowMin() throws Exception {
            validRequestDTO.setTenureYears(0);

            mockMvc.perform(post("/api/v1/loans/post/apply")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(userApplicationDetailService, never()).applyLoan(any());
        }

        @Test
        @DisplayName("Should return 400 BAD_REQUEST when DOMESTIC loan has no domesticCourseType set")
        void testSubmitApplicationDomesticLoanMissingDomesticCourseType() throws Exception {
            validRequestDTO.setDomesticCourseType(null); // violates @AssertTrue

            mockMvc.perform(post("/api/v1/loans/post/apply")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(userApplicationDetailService, never()).applyLoan(any());
        }

        @Test
        @DisplayName("Should return 400 BAD_REQUEST with validation errors when all fields are missing")
        void testSubmitApplicationEmptyBody() throws Exception {
            mockMvc.perform(post("/api/v1/loans/post/apply")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new UserApplicationRequestDTO())))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("INVALID_EMPTY_LOAN_PARAMETERS"))
                    .andExpect(jsonPath("$.message").value("Missing required fields in loan request"));

            verify(userApplicationDetailService, never()).applyLoan(any());
        }

        @Test
        @DisplayName("Should return 500 INTERNAL_SERVER_ERROR when service throws RuntimeException")
        void testSubmitApplicationServiceThrowsException() throws Exception {
            when(userApplicationDetailService.applyLoan(any(UserApplicationRequestDTO.class)))
                    .thenThrow(new RuntimeException("Database connection failed"));

            mockMvc.perform(post("/api/v1/loans/post/apply")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());

            verify(userApplicationDetailService, times(1)).applyLoan(any());
        }
    }

    //Get All Applications Tests

    @Nested
    @DisplayName("GET /api/v1/loans/applicant/getAll - Fetch All Applications")
    class GetAllApplicationsTests {

        @Test
        @DisplayName("Should return all applications with correct applicantName fields")
        void testGetAllReturnsAllApplications() throws Exception {
            UserApplicationResponseDTO secondApp = UserApplicationResponseDTO.builder()
                    .id(UUID.randomUUID())
                    .applicantName("Jane Smith")
                    .applicantEmail("jane.smith@example.com")
                    .loanType(LoanType.INTERNATIONAL)
                    .applicationStatus(ApplicationStatus.APPROVED)
                    .build();

            when(userApplicationDetailService.getAll()).thenReturn(Arrays.asList(validResponseDTO, secondApp));

            mockMvc.perform(get("/api/v1/loans/applicant/getAll")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.message", is("Fetched all loan applications successfully!")))
                    .andExpect(jsonPath("$.data", hasSize(2)))
                    .andExpect(jsonPath("$.data[0].applicantName", is("Narsing Patil")))
                    .andExpect(jsonPath("$.data[1].applicantName", is("Jane Smith")));

            verify(userApplicationDetailService, times(1)).getAll();
        }

        @Test
        @DisplayName("Should return empty list when no applications exist")
        void testGetAllEmptyListWithEmptyData() throws Exception {
            when(userApplicationDetailService.getAll()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/loans/applicant/getAll")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.data", hasSize(0)));

            verify(userApplicationDetailService, times(1)).getAll();
        }

        @Test
        @DisplayName("Should return 500 when service throws exception")
        void testGetAllServiceException() throws Exception {
            when(userApplicationDetailService.getAll())
                    .thenThrow(new RuntimeException("DB error"));

            mockMvc.perform(get("/api/v1/loans/applicant/getAll")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }
    }

    //Get Applications by Status Tests-----------------------------------------------------------

    @Nested
    @DisplayName("GET /api/v1/loans/status/{status} - Fetch Applications by Status")
    class GetByStatusTests {

        @Test
        @DisplayName("Should return PENDING applications correctly")
        void testGetByStatusPending() throws Exception {
            UserApplicationStatusResponseDTO dto = new UserApplicationStatusResponseDTO();
            dto.setId(testApplicationId);
            dto.setApplicantName("John Doe");
            dto.setApplicationStatus(ApplicationStatus.PENDING);

            when(userApplicationDetailService.getByStatus(ApplicationStatus.PENDING))
                    .thenReturn(List.of(dto));

            mockMvc.perform(get("/api/v1/loans/status/{status}", "PENDING")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.message", is("Fetched Loan application by status successfully..!")))
                    .andExpect(jsonPath("$.data", hasSize(1)))
                    .andExpect(jsonPath("$.data[0].applicationStatus", is("PENDING")));

            verify(userApplicationDetailService, times(1)).getByStatus(ApplicationStatus.PENDING);
        }

        @Test
        @DisplayName("Should return APPROVED applications correctly")
        void testGetByStatusApproved() throws Exception {
            UserApplicationStatusResponseDTO dto = new UserApplicationStatusResponseDTO();
            dto.setApplicationStatus(ApplicationStatus.APPROVED);

            when(userApplicationDetailService.getByStatus(ApplicationStatus.APPROVED))
                    .thenReturn(List.of(dto));

            mockMvc.perform(get("/api/v1/loans/status/{status}", "APPROVED")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].applicationStatus", is("APPROVED")));

            verify(userApplicationDetailService, times(1)).getByStatus(ApplicationStatus.APPROVED);
        }

        @Test
        @DisplayName("Should return REJECTED applications correctly")
        void testGetByStatusRejected() throws Exception {
            when(userApplicationDetailService.getByStatus(ApplicationStatus.REJECTED))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/loans/status/{status}", "REJECTED")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(0)));

            verify(userApplicationDetailService, times(1)).getByStatus(ApplicationStatus.REJECTED);
        }

        @Test
        @DisplayName("Should return 400 BAD_REQUEST for invalid status value")
        void testGetByStatusInvalidStatus() throws Exception{

                mockMvc.perform(get("/api/v1/loans/status/{status}", "INVALID_STATUS")
                                .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.errorCode").value("INVALID_STATUS"))
                        .andExpect(jsonPath("$.message").value("Invalid status value: INVALID_STATUS"));

            verify(userApplicationDetailService, never()).getByStatus(any());
        }
    }

    //Get Applications by Loan Type Tests------------------------------------------------------------------------------
    @Nested
    @DisplayName("GET /api/v1/loans/loanType/{loanType} - Fetch Applications by Loan Type")
    class GetByLoanTypeTests {

        @Test
        @DisplayName("Should return DOMESTIC loan applications correctly")
        void testGetByLoanTypeDomestic() throws Exception {
            UserApplicationStatusResponseDTO dto = new UserApplicationStatusResponseDTO();
            dto.setLoanType(LoanType.DOMESTIC);
            dto.setApplicationStatus(ApplicationStatus.PENDING);

            when(userApplicationDetailService.getByLoanType(LoanType.DOMESTIC))
                    .thenReturn(List.of(dto));

            mockMvc.perform(get("/api/v1/loans/loanType/{loanType}", "DOMESTIC")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.message", is("Fetched Loan application by loanType successfully...!")))
                    .andExpect(jsonPath("$.data[0].loanType", is("DOMESTIC")));

            verify(userApplicationDetailService, times(1)).getByLoanType(LoanType.DOMESTIC);
        }

        @Test
        @DisplayName("Should return INTERNATIONAL loan applications correctly")
        void testGetByLoanTypeInternational() throws Exception {
            UserApplicationStatusResponseDTO dto1 = new UserApplicationStatusResponseDTO();
            dto1.setLoanType(LoanType.INTERNATIONAL);
            UserApplicationStatusResponseDTO dto2 = new UserApplicationStatusResponseDTO();
            dto2.setLoanType(LoanType.INTERNATIONAL);

            when(userApplicationDetailService.getByLoanType(LoanType.INTERNATIONAL))
                    .thenReturn(Arrays.asList(dto1, dto2));

            mockMvc.perform(get("/api/v1/loans/loanType/{loanType}", "INTERNATIONAL")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(2)))
                    .andExpect(jsonPath("$.data[0].loanType", is("INTERNATIONAL")));

            verify(userApplicationDetailService, times(1)).getByLoanType(LoanType.INTERNATIONAL);
        }

        @Test
        @DisplayName("Should return empty list when no applications match loanType")
        void testGetByLoanTypeNoMatchReturnsEmptyList() throws Exception {
            when(userApplicationDetailService.getByLoanType(LoanType.DOMESTIC))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/loans/loanType/{loanType}", "DOMESTIC")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(0)));
        }

        @Test
        @DisplayName("Should return 400 BAD_REQUEST for invalid loanType value")
        void testGetByLoanTypeInvalidLoanType() throws Exception {
            mockMvc.perform(get("/api/v1/loans/loanType/{loanType}", "HOME_LOAN")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(userApplicationDetailService, never()).getByLoanType(any());
        }
    }

    // ==================== Update Application Status Tests ====================

    @Nested
    @DisplayName("PATCH /api/v1/loans/patch/updateStatus/{id} - Update Application Status")
    class UpdateStatusTests {

        @Test
        @DisplayName("Should update status to APPROVED and return correct response")
        void testUpdateStatusToApproved() throws Exception {
            UpdateStatusRequest updateRequest = new UpdateStatusRequest();
            updateRequest.setId(testApplicationId);
            updateRequest.setNewStatus(ApplicationStatus.APPROVED);

            UserApplicationStatusUpdateResponseDTO updateResponse = new UserApplicationStatusUpdateResponseDTO();
            updateResponse.setId(testApplicationId);
            updateResponse.setApplicantName("John Doe");
            updateResponse.setApplicationStatus(ApplicationStatus.APPROVED);
            updateResponse.setLoanType(LoanType.DOMESTIC);
            updateResponse.setUpdatedAt(LocalDateTime.now());

            when(userApplicationDetailService.updateStatus(testApplicationId, ApplicationStatus.APPROVED))
                    .thenReturn(updateResponse);

            mockMvc.perform(patch("/api/v1/loans/patch/updateStatus/{id}", testApplicationId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.message", is("Application Status updated successfully...!")))
                    .andExpect(jsonPath("$.data.applicationStatus", is("APPROVED")))
                    .andExpect(jsonPath("$.data.applicantName", is("John Doe")));

            verify(userApplicationDetailService, times(1))
                    .updateStatus(testApplicationId, ApplicationStatus.APPROVED);
        }

        @Test
        @DisplayName("Should update status to REJECTED successfully")
        void testUpdateStatusToRejected() throws Exception {
            UpdateStatusRequest updateRequest = new UpdateStatusRequest();
            updateRequest.setId(testApplicationId);
            updateRequest.setNewStatus(ApplicationStatus.REJECTED);

            UserApplicationStatusUpdateResponseDTO updateResponse = new UserApplicationStatusUpdateResponseDTO();
            updateResponse.setId(testApplicationId);
            updateResponse.setApplicationStatus(ApplicationStatus.REJECTED);

            when(userApplicationDetailService.updateStatus(testApplicationId, ApplicationStatus.REJECTED)).thenReturn(updateResponse);
            mockMvc.perform(patch("/api/v1/loans/patch/updateStatus/{id}", testApplicationId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.applicationStatus", is("REJECTED")));

            verify(userApplicationDetailService, times(1)).updateStatus(testApplicationId, ApplicationStatus.REJECTED);
        }

        @Test
        @DisplayName("Should return 400 BAD_REQUEST when newStatus value is invalid")
        void testUpdateStatusInvalidStatus() throws Exception {
            String invalidJson = "{\"id\":\"" + testApplicationId + "\",\"newStatus\":\"INVALID_STATUS\"}";

            mockMvc.perform(patch("/api/v1/loans/patch/updateStatus/{id}", testApplicationId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(userApplicationDetailService, never()).updateStatus(any(), any());
        }

        @Test
        @DisplayName("Should return 404 when application ID does not exist")
        void testUpdateStatusApplicationNotFound() throws Exception {
            UUID nonExistentId = UUID.randomUUID();
            UpdateStatusRequest updateRequest = new UpdateStatusRequest();
            updateRequest.setId(nonExistentId);
            updateRequest.setNewStatus(ApplicationStatus.APPROVED);

            when(userApplicationDetailService.updateStatus(nonExistentId, ApplicationStatus.APPROVED))
                    .thenThrow(new RuntimeException("Application not found with id: " + nonExistentId));

            mockMvc.perform(patch("/api/v1/loans/patch/updateStatus/{id}", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }
    }

    // Evaluate / Reject Application Tests-----------------------------------------------------------------------

    @Nested
    @DisplayName("PUT /api/v1/loans/application/{id}/evaluate - Evaluate Application")
    class EvaluateApplicationTests {

        @Test
        @DisplayName("Should evaluate application and return rejection details with correct fields")
        void testEvaluateApplicationReturnsRejectionResponse() throws Exception {
            UserApplicationRejectResponseDTO rejectResponse = new UserApplicationRejectResponseDTO();
            rejectResponse.setApplicationId(testApplicationId);
            rejectResponse.setStatus("REJECTED");
            rejectResponse.setReasonCode("LOW_CREDIT_SCORE");
            rejectResponse.setMessage("Credit score is below the required threshold");
            rejectResponse.setRejectedBy("system");

            when(userApplicationDetailService.evaluateApplication(testApplicationId))
                    .thenReturn(rejectResponse);

            mockMvc.perform(put("/api/v1/loans/application/{id}/evaluate", testApplicationId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.applicationId", is(testApplicationId.toString())))
                    .andExpect(jsonPath("$.status", is("REJECTED")))
                    .andExpect(jsonPath("$.reasonCode", is("LOW_CREDIT_SCORE")))
                    .andExpect(jsonPath("$.message", is("Credit score is below the required threshold")));

            verify(userApplicationDetailService, times(1)).evaluateApplication(testApplicationId);
        }

        @Test
        @DisplayName("Should evaluate and approve application")
        void testEvaluateApplicationApproved() throws Exception {
            UserApplicationRejectResponseDTO approveResponse = new UserApplicationRejectResponseDTO();
            approveResponse.setApplicationId(testApplicationId);
            approveResponse.setStatus("APPROVED");
            approveResponse.setApprovedBy("system");

            when(userApplicationDetailService.evaluateApplication(testApplicationId))
                    .thenReturn(approveResponse);

            mockMvc.perform(put("/api/v1/loans/application/{id}/evaluate", testApplicationId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", is("APPROVED")));
        }

        @Test
        @DisplayName("Should return 500 when application is not found")
        void testEvaluateApplicationNotFoundReturns500() throws Exception {
            UUID nonExistentId = UUID.randomUUID();
            when(userApplicationDetailService.evaluateApplication(nonExistentId))
                    .thenThrow(new RuntimeException("Application not found"));

            mockMvc.perform(put("/api/v1/loans/application/{id}/evaluate", nonExistentId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());

            verify(userApplicationDetailService, times(1)).evaluateApplication(nonExistentId);
        }

        @Test
        @DisplayName("Should return eligibility criteria that failed")
        void testEvaluateApplicationWithEligibilityCriteriaFailed() throws Exception {
            UserApplicationRejectResponseDTO rejectResponse = new UserApplicationRejectResponseDTO();
            rejectResponse.setApplicationId(testApplicationId);
            rejectResponse.setStatus("REJECTED");
            rejectResponse.setEligibilityCriteriaFailed(
                    Arrays.asList("CREDIT_SCORE_TOO_LOW", "INCOME_INSUFFICIENT"));

            when(userApplicationDetailService.evaluateApplication(testApplicationId))
                    .thenReturn(rejectResponse);

            mockMvc.perform(put("/api/v1/loans/application/{id}/evaluate", testApplicationId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.eligibilityCriteriaFailed", hasSize(2)))
                    .andExpect(jsonPath("$.eligibilityCriteriaFailed[0]", is("CREDIT_SCORE_TOO_LOW")));
        }
    }

    // Calculate Interest Rate (NRM) Tests----------------------------------------------------------------

    @Nested
    @DisplayName("POST /api/v1/loans/cir - Calculate Interest Rate (Newton-Raphson)")
    class CalculateInterestRateNRMTests {

        @Test
        @DisplayName("Should calculate interest rate using NRM and return 200 with method in message")
        void testCalculateInterestRateNRM() throws Exception {
            InterestRateCalculationRequestDTO requestDTO = new InterestRateCalculationRequestDTO();
            requestDTO.setApprovedAmount(new BigDecimal("100000"));
            requestDTO.setMonthlyEmi(new BigDecimal("5000"));
            requestDTO.setTenureYears(5);

            InterestRateCalculationResponseDTO responseDTO = new InterestRateCalculationResponseDTO();
            responseDTO.setCalculatedAnnualInterestRate(new BigDecimal("10.5"));
            responseDTO.setCalculationMethod("Newton-Raphson Method");

            when(interestRateCalculationService.calculateInterestRate(any(InterestRateCalculationRequestDTO.class)))
                    .thenReturn(responseDTO);

            mockMvc.perform(post("/api/v1/loans/cir")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.message", containsString("Newton-Raphson Method")))
                    .andExpect(jsonPath("$.data.calculatedAnnualInterestRate", is(10.5)));

            verify(interestRateCalculationService, times(1))
                    .calculateInterestRate(any(InterestRateCalculationRequestDTO.class));
        }

        @Test
        @DisplayName("Should return 400 BAD_REQUEST for empty body")
        void testCalculateInterestRateEmptyBody() throws Exception {
            mockMvc.perform(post("/api/v1/loans/cir")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new InterestRateCalculationRequestDTO())))
                    .andExpect(status().isBadRequest());

            verify(interestRateCalculationService, never()).calculateInterestRate(any());
        }

        @Test
        @DisplayName("Should return 500 when NRM service throws exception")
        void testCalculateInterestRateServiceException() throws Exception {
            InterestRateCalculationRequestDTO requestDTO = new InterestRateCalculationRequestDTO();
            requestDTO.setApprovedAmount(new BigDecimal("100000"));
            requestDTO.setMonthlyEmi(new BigDecimal("5000"));
            requestDTO.setTenureYears(5);

            when(interestRateCalculationService.calculateInterestRate(any()))
                    .thenThrow(new RuntimeException("Convergence failed"));

            mockMvc.perform(post("/api/v1/loans/cir")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }
    }

    // Calculate Interest Rate (Binary Search) Tests -------------------------------------------------------------------

    @Nested
    @DisplayName("POST /api/v1/loans/cir/bs - Calculate Interest Rate (Binary Search)")
    class CalculateInterestRateBinarySearchTests {

        @Test
        @DisplayName("Should calculate interest rate using Binary Search and return 200")
        void testCalculateInterestRateBinarySearch() throws Exception {
            InterestRateCalculationRequestDTO requestDTO = new InterestRateCalculationRequestDTO();
            requestDTO.setApprovedAmount(new BigDecimal("200000"));
            requestDTO.setMonthlyEmi(new BigDecimal("8000"));
            requestDTO.setTenureYears(7);

            InterestRateCalculationResponseDTO responseDTO = new InterestRateCalculationResponseDTO();
            responseDTO.setCalculatedAnnualInterestRate(new BigDecimal("9.8"));
            responseDTO.setCalculationMethod("Binary Search Method");

            when(interestRateCalculationService.calculateInterestRateBinarySearch(any(InterestRateCalculationRequestDTO.class)))
                    .thenReturn(responseDTO);

            mockMvc.perform(post("/api/v1/loans/cir/bs")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.message", containsString("Binary Search Method")))
                    .andExpect(jsonPath("$.data.calculatedAnnualInterestRate", is(9.8)));

            verify(interestRateCalculationService, times(1))
                    .calculateInterestRateBinarySearch(any(InterestRateCalculationRequestDTO.class));
        }

        @Test
        @DisplayName("Should return 400 BAD_REQUEST for empty body")
        void testCalculateInterestRateBinarySearchEmptyBody() throws Exception {
            mockMvc.perform(post("/api/v1/loans/cir/bs")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new InterestRateCalculationRequestDTO())))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(interestRateCalculationService, never()).calculateInterestRateBinarySearch(any());
        }

        @Test
        @DisplayName("Should return 500 when Binary Search service throws exception")
        void testCalculateInterestRateBinarySearchServiceException() throws Exception {
            InterestRateCalculationRequestDTO requestDTO = new InterestRateCalculationRequestDTO();
            requestDTO.setApprovedAmount(new BigDecimal("100000"));
            requestDTO.setMonthlyEmi(new BigDecimal("5000"));
            requestDTO.setTenureYears(5);

            when(interestRateCalculationService.calculateInterestRateBinarySearch(any()))
                    .thenThrow(new RuntimeException("Calculation failed"));

            mockMvc.perform(post("/api/v1/loans/cir/bs")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }
    }

    // Integration / Workflow Tests-------------------------------------------------------------------------

    @Nested
    @DisplayName("Integration Workflow Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Full workflow: Submit DOMESTIC application → Update status to APPROVED")
        void testWorkflowEvaluateSubmitThenApprove() throws Exception {
            // Step 1: Submit
            when(userApplicationDetailService.applyLoan(any()))
                    .thenReturn(validResponseDTO);

            mockMvc.perform(post("/api/v1/loans/post/apply")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.applicationStatus", is("PENDING")));

            // Step 2: Update to APPROVED
            UpdateStatusRequest updateRequest = new UpdateStatusRequest();
            updateRequest.setId(testApplicationId);
            updateRequest.setNewStatus(ApplicationStatus.APPROVED);

            UserApplicationStatusUpdateResponseDTO updateResponse = new UserApplicationStatusUpdateResponseDTO();
            updateResponse.setId(testApplicationId);
            updateResponse.setApplicationStatus(ApplicationStatus.APPROVED);
            updateResponse.setLoanType(LoanType.DOMESTIC);

            when(userApplicationDetailService.updateStatus(testApplicationId, ApplicationStatus.APPROVED))
                    .thenReturn(updateResponse);

            mockMvc.perform(patch("/api/v1/loans/patch/updateStatus/{id}", testApplicationId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.applicationStatus", is("APPROVED")));

            verify(userApplicationDetailService, times(1)).applyLoan(any());
            verify(userApplicationDetailService, times(1)).updateStatus(any(), any());
        }

        @Test
        @DisplayName("Should submit both DOMESTIC and INTERNATIONAL loan types successfully")
        void testSubmitBothLoanTypes() throws Exception {

            when(userApplicationDetailService.applyLoan(any())).thenReturn(validResponseDTO);
            mockMvc.perform(post("/api/v1/loans/post/apply")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andDo(print())
                    .andExpect(status().isCreated());

            validRequestDTO.setLoanType(LoanType.INTERNATIONAL);
            validRequestDTO.setDomesticCourseType(null);
            validRequestDTO.setInternationalCourseType(InternationalCourseType.MBA);

            UserApplicationResponseDTO internationalResponse = UserApplicationResponseDTO.builder()
                    .id(UUID.randomUUID())
                    .loanType(LoanType.INTERNATIONAL)
                    .applicationStatus(ApplicationStatus.PENDING)
                    .build();

            when(userApplicationDetailService.applyLoan(any()))
                    .thenReturn(internationalResponse);

            mockMvc.perform(post("/api/v1/loans/post/apply")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.loanType", is("INTERNATIONAL")));

            verify(userApplicationDetailService, times(2)).applyLoan(any());
        }
    }
}