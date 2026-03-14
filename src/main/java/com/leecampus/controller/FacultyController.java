package com.leecampus.controller;

import com.leecampus.dto.*;
import com.leecampus.service.FacultyService;
import org.springframework.web.bind.annotation.*;
import com.leecampus.dto.FacultyRegisterDTO;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/leecampus/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    // POST /leecampus/faculty/login
    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody FacultyRequestDTO dto) {
        return facultyService.login(dto);
    }

    // GET /leecampus/faculty/profile/{id}
    @GetMapping("/profile/{id}")
    public FacultyResponseDTO getProfile(@PathVariable Long id) {
        return facultyService.getProfile(id);
    }

    // GET /leecampus/faculty/students?department=CSE&section=A&year=3
    @GetMapping("/students")
    public List<Map<String, Object>> getStudents(
            @RequestParam String department,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) Integer year) {
        return facultyService.getStudentsWithStats(department, section, year);
    }

    // GET /leecampus/faculty/summary?department=CSE
    @GetMapping("/summary")
    public Map<String, Object> getSummary(
            @RequestParam String department) {
        return facultyService.getDeptSummary(department);
    }
 // POST /leecampus/faculty/register  (admin only)
    @PostMapping("/register")
    public FacultyResponseDTO registerFaculty(
            @RequestBody FacultyRegisterDTO dto) {
        return facultyService.registerFaculty(dto);
    }
}