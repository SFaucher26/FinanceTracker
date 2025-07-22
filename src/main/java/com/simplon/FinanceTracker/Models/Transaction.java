package com.simplon.FinanceTracker.Models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Nullable
    private String description;
    private BigDecimal amount;
    private LocalDateTime date;

    @ManyToOne
    private User owner;

    @ManyToOne
    private Category category;

    @ManyToOne
    private PaymentMethod paymentMethod;
}