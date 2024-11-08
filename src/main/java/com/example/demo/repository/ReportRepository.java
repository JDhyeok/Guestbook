package com.example.demo.repository;

import com.example.demo.model.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    List<ReportEntity> findByIndustry(String industry);
    List<ReportEntity> findByCompanyName(String companyName);
}