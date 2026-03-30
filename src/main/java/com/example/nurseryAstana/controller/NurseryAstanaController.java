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
