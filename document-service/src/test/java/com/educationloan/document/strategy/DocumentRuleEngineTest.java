package com.educationloan.document.strategy;
import com.educationloan.document.enumConst.ApplicantType;
import com.educationloan.document.enumConst.StudyLocationType;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentRuleEngineTest {
    @Test
    void shouldReturnFromCorrectStrategy() {
        DocumentRuleStrategy strategy = mock(DocumentRuleStrategy.class);
        when(strategy.getAllowedApplicants(any()))
                .thenReturn(Set.of(ApplicantType.STUDENT));
        Map<String, DocumentRuleStrategy> map =
                Map.of("DOMESTIC", strategy);
        DocumentRuleEngine engine = new DocumentRuleEngine(map);

        Set<ApplicantType> result =
                engine.getAllowedApplicants(
                        StudyLocationType.DOMESTIC,
                        new BigDecimal("500000"));
        assertTrue(result.contains(ApplicantType.STUDENT));
    }

    @Test
    void shouldThrowException_whenStrategyNotFound() {
        Map<String, DocumentRuleStrategy> map = Map.of();
        DocumentRuleEngine engine = new DocumentRuleEngine(map);
        assertThrows(NullPointerException.class, () ->
                engine.getAllowedApplicants(
                        StudyLocationType.DOMESTIC,
                        new BigDecimal("500000")));
    }
}