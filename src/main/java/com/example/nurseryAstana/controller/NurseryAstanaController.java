package com.example.nurseryAstana.controller;

import com.example.nurseryAstana.model.Report;
import com.example.nurseryAstana.model.User;
import com.example.nurseryAstana.service.ReportService;
import com.example.nurseryAstana.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST контроллер для управления данными питомника.
 * Предоставляет API endpoints для получения информации о пользователях и репортах.
 *
 * <p>Базовый путь: {@code /api}
 *
 * <p>Доступные endpoints:
 * <ul>
 *   <li>GET /api/users - получить список всех пользователей</li>
 *   <li>GET /api/reports - получить список всех репортов/обращений</li>
 * </ul>
 *
 * @author Nursery Astana Team
 * @version 1.0
 */

@RestController
@RequestMapping("api")
public class NurseryAstanaController {

    private final ReportService reportService;
    private final UserService userService;

    public NurseryAstanaController(ReportService reportService, UserService userService) {
        this.reportService = reportService;
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/reports")
    public ResponseEntity<List<Report>> getAllReports() {
        List<Report> reports = reportService.findAllReports();
        return ResponseEntity.ok(reports);
    }


}
