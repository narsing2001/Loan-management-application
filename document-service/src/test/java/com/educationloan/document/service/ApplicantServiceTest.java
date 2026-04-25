package com.educationloan.document.service;
import com.educationloan.document.dto.ApplicantResponseDTO;
import com.educationloan.document.entity.ApplicantEntity;
import com.educationloan.document.globalExceptionHandling.CustomException.ApplicantNotFoundException;
import com.educationloan.document.repository.ApplicantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicantServiceTest {
    @InjectMocks
    private ApplicantServiceImpl applicantService;

    @Mock
    private ApplicantRepository applicantRepository;

    @Test
    void getApplicantById_success() {
        ApplicantEntity entity = new ApplicantEntity();
        entity.setId(1L);
        entity.setName("John");
        entity.setDob("2000-01-01");
        entity.setAadhaarNumber("123456789012");

        when(applicantRepository.findById(1L)).thenReturn(Optional.of(entity));
        ApplicantResponseDTO response = applicantService.getApplicantById(1L);
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John", response.getName());
        assertEquals("123456789012", response.getAadhaarNumber());
    }

    @Test
    void getApplicantById_notFound() {
        when(applicantRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ApplicantNotFoundException.class, () -> applicantService.getApplicantById(1L));
    }

    @Test
    void getApplicantById_nullId() {
        assertThrows(IllegalArgumentException.class, () -> applicantService.getApplicantById(null));
    }

    @Test
    void getApplicantById_repositoryFailure() {
        when(applicantRepository.findById(1L)).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> applicantService.getApplicantById(1L));
    }
}
