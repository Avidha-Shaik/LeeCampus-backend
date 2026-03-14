package com.leecampus.controller;

import com.leecampus.dto.AdminRequestDTO;
import com.leecampus.dto.StudentResponseDTO;
import com.leecampus.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/leecampus/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ── LOGIN ──
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminRequestDTO dto) {
        try {
            return ResponseEntity.ok(adminService.login(dto));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // ── GET ALL STUDENTS ──
    @GetMapping("/students")
    public List<StudentResponseDTO> getAllStudents() {
        return adminService.getAllStudents();
    }

    // ── DELETE STUDENT ──
    @DeleteMapping("/students/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        try {
            adminService.deleteStudent(id);
            return ResponseEntity.ok("Deleted");
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // ── REFRESH ONE STUDENT ──
    @PostMapping("/refresh/{id}")
    public ResponseEntity<?> refreshStats(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(adminService.refreshStats(id));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // ── REFRESH ALL STUDENTS ──
    @PostMapping("/refreshAll")
    public ResponseEntity<?> refreshAllStats() {
        try {
            return ResponseEntity.ok(adminService.refreshAllStats());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // ── ADMIN STATS ──
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(Map.of("totalStudents", adminService.getTotalStudents()));
    }

    // ── ANALYTICS ── 🔥 updated with optional query params
    @GetMapping("/analytics")
    public Map<String, Object> getAnalytics(
        @RequestParam(required = false) String department,
        @RequestParam(required = false) String section,
        @RequestParam(required = false) Integer year
    ) {
        return adminService.getAnalytics(department, section, year);
    }
}