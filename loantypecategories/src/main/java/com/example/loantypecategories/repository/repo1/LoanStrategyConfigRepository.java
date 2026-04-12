package com.example.loantypecategories.repository.repo1;

import com.example.loantypecategories.constant.cont1.BankType;
import com.example.loantypecategories.constant.CourseType;
import com.example.loantypecategories.constant.Gender;
import com.example.loantypecategories.constant.LoanType;
import com.example.loantypecategories.entity.entit1.LoanStrategyConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanStrategyConfigRepository extends JpaRepository<LoanStrategyConfig, Long> {

    /**
     * Find exact strategy configuration for a specific combination
     */
    Optional<LoanStrategyConfig> findByBankTypeAndLoanTypeAndCourseTypeAndGenderAndActiveTrue(
            BankType bankType,
            LoanType loanType,
            CourseType courseType,
            Gender gender
    );

    /**
     * Find all configurations for a specific bank
     */
    List<LoanStrategyConfig> findByBankTypeAndActiveTrue(BankType bankType);

    /**
     * Find all configurations for a specific loan type
     */
    List<LoanStrategyConfig> findByLoanTypeAndActiveTrue(LoanType loanType);

    /**
     * Find configurations by bank and loan type
     */
    List<LoanStrategyConfig> findByBankTypeAndLoanTypeAndActiveTrue(
            BankType bankType,
            LoanType loanType
    );

    /**
     * Find configurations with subsidy eligibility
     */
    @Query("SELECT c FROM LoanStrategyConfig c WHERE c.subsidyEligible = true AND c.active = true")
    List<LoanStrategyConfig> findSubsidyEligibleConfigs();

    /**
     * Find best interest rate for given parameters
     */
    @Query("SELECT MIN(c.baseInterestRate) FROM LoanStrategyConfig c " +
            "WHERE c.loanType = :loanType AND c.courseType = :courseType AND c.active = true")
    Optional<java.math.BigDecimal> findBestInterestRate(
            @Param("loanType") LoanType loanType,
            @Param("courseType") CourseType courseType
    );

    /**
     * Find government bank configurations (typically better rates)
     */
    @Query("SELECT c FROM LoanStrategyConfig c WHERE c.active = true " +
            "AND c.bankType IN ('SBI', 'PNB', 'BOB', 'CANARA', 'UNION', 'IDBI')")
    List<LoanStrategyConfig> findGovernmentBankConfigs();

    /**
     * Compare rates across banks for specific loan parameters
     */
    @Query("SELECT c FROM LoanStrategyConfig c WHERE c.loanType = :loanType " +
            "AND c.courseType = :courseType AND c.gender = :gender AND c.active = true " +
            "ORDER BY c.baseInterestRate ASC")
    List<LoanStrategyConfig> findBestRatesForComparison(
            @Param("loanType") LoanType loanType,
            @Param("courseType") CourseType courseType,
            @Param("gender") Gender gender
    );


    /**
     * Count active configurations by bank
     */
    long countByBankTypeAndActiveTrue(BankType bankType);

    /**
     * Count active configurations by loan type
     */
    long countByLoanTypeAndActiveTrue(LoanType loanType);

    /**
     * Find all active configurations by course type
     */
    List<LoanStrategyConfig> findByCourseTypeAndActiveTrue(CourseType courseType);

    /**
     * Find all active configurations by gender
     */
    List<LoanStrategyConfig> findByGenderAndActiveTrue(Gender gender);

    /**
     * Average base interest rate for a given loan type
     */
    @Query("SELECT AVG(c.baseInterestRate) FROM LoanStrategyConfig c " +
            "WHERE c.loanType = :loanType AND c.active = true")
    Optional<java.math.BigDecimal> averageBaseInterestRateByLoanType(@Param("loanType") LoanType loanType);

    /**
     * Average final interest rate (after discounts) for a given loan type
     */
    @Query("SELECT AVG(c.baseInterestRate - COALESCE(c.femaleDiscount,0) - COALESCE(c.govtBankDiscount,0)) " +
            "FROM LoanStrategyConfig c WHERE c.loanType = :loanType AND c.active = true")
    Optional<java.math.BigDecimal> averageFinalInterestRateByLoanType(@Param("loanType") LoanType loanType);

    /**
     * Find all active configurations created by subsidy eligibility and course type
     */
    List<LoanStrategyConfig> findByCourseTypeAndSubsidyEligibleTrueAndActiveTrue(CourseType courseType);





}