package com.loanmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "repayment_schedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "month")
    private int month;

    @Column(precision = 15, scale = 2)
    private BigDecimal emi;

    @Column(precision = 15, scale = 2)
    private BigDecimal principal;

    @Column(precision = 15, scale = 2)
    private BigDecimal interest;

    @Column(precision = 15, scale = 2)
    private BigDecimal balance;

    private boolean paid;
    private boolean holiday;

    private LocalDate dueDate;
    private LocalDate paymentDate;

    @Column(precision = 15, scale = 2)
    private BigDecimal prepaymentCharges;
}