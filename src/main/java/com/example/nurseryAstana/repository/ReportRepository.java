package com.example.nurseryAstana.repository;

import com.example.nurseryAstana.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Report> findAllByOrderByCreatedAtDesc();
    @Query("SELECT r FROM Report r WHERE r.user.telegramId = :telegramId")
    List<Report> findByTelegramIdOrderByCreatedAtDesc(Long telegramId);
}