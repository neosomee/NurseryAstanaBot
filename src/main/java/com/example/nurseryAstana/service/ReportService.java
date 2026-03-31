package com.example.nurseryAstana.service;

import com.example.nurseryAstana.model.Report;
import com.example.nurseryAstana.model.User;
import com.example.nurseryAstana.repository.ReportRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * Сервис для управления репортами/обращениями.
 * Предоставляет операции сохранения и получения списка обращений.
 */

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserService userService;

    @Autowired
    public ReportService(ReportRepository reportRepository, UserService userService) {
        this.reportRepository = reportRepository;
        this.userService = userService;
    }

    @Transactional
    public Report saveReport(Long telegramId, String text, String photoUrl) {
        User user = userService.createOrGetUser(telegramId, null);
        Report report = new Report(user, text, photoUrl);
        return reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public List<Report> findAllReports() {
        return reportRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Report> getAllReportsByUser(User user) {
        return reportRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @Transactional(readOnly=true)
    public List<Report> getUserReport(Long telegramId) {
        return reportRepository.findByTelegramIdOrderByCreatedAtDesc(telegramId);
    }
}
