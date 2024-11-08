package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 50000)
    private String content;
    private String industry;
    private String companyName;

    @Column(length = 50000)
    private String aiAnalysis;

    private double biasScore;
    private double riskAnalysisScore;
    private double optimismScore;

    @CreatedDate
    private LocalDateTime createdAt;
}