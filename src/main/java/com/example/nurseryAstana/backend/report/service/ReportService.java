package com.example.nurseryAstana.backend.report.service;

import com.example.nurseryAstana.backend.user.service.imple.UserServiceImple;
import com.example.nurseryAstana.backend.report.model.Report;
import com.example.nurseryAstana.backend.user.model.User;
import com.example.nurseryAstana.backend.repository.ReportRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления репортами/обращениями.
 * Предоставляет операции сохранения и получения списка обращений.
 */

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserServiceImple userService;

    @Autowired
    public ReportService(ReportRepository reportRepository, UserServiceImple userService) {
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

    @Transactional(readOnly = true)
    public Optional<Report> findById(Long id) {
        return reportRepository.findById(id);
    }
}
