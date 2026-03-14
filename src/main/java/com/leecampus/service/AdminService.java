package com.leecampus.service;

import com.leecampus.config.JwtUtil;
import com.leecampus.dto.*;
import com.leecampus.entity.Admin;
import com.leecampus.entity.Student;
import com.leecampus.repository.AdminRepository;
import com.leecampus.repository.LeetcodeStatsRepository;
import com.leecampus.repository.StudentRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;
    private final LeetcodeStatsService leetcodeStatsService;
    private final LeetcodeStatsRepository statsRepository;
    private final JwtUtil jwtUtil;

    public AdminService(AdminRepository adminRepository,
                        StudentRepository studentRepository,
                        LeetcodeStatsRepository statsRepository,
                        LeetcodeStatsService leetcodeStatsService,
                        JwtUtil jwtUtil) {
        this.adminRepository = adminRepository;
        this.studentRepository = studentRepository;
        this.statsRepository = statsRepository;
        this.leetcodeStatsService = leetcodeStatsService;
        this.jwtUtil = jwtUtil;
    }

    // ── LOGIN ──
    public AuthResponseDTO login(AdminRequestDTO dto) {

        Admin admin = adminRepository
                .findByEmail(dto.getEmail().trim())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (!admin.getPassword().trim().equals(dto.getPassword().trim()))
            throw new RuntimeException("Invalid credentials");

        String token = jwtUtil.generateToken(
                admin.getEmail(), "ADMIN", admin.getId());

        return new AuthResponseDTO(
                token, "ADMIN",
                admin.getId(),
                admin.getName(),
                admin.getEmail()
        );
    }

    // ── GET ALL STUDENTS ──
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(s -> {
                    StudentResponseDTO dto = new StudentResponseDTO();
                    dto.setId(s.getId());
                    dto.setName(s.getName());
                    dto.setEmail(s.getEmail());
                    dto.setRollNumber(s.getRollNumber());
                    dto.setLeetcodeUsername(s.getLeetcodeUsername());
                    dto.setDepartment(s.getDepartment());
                    dto.setCourse(s.getCourse());
                    dto.setSection(s.getSection());
                    dto.setYear(s.getYear());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ── DELETE STUDENT ──
    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id))
            throw new RuntimeException("Student not found");
        statsRepository.deleteByStudent_Id(id);
        studentRepository.deleteById(id);
    }

    // ── REFRESH STATS FOR ONE STUDENT ──
    public String refreshStats(Long studentId) {
        leetcodeStatsService.fetchAndStoreStats(studentId);
        return "Stats refreshed for student " + studentId;
    }

    // ── REFRESH ALL STATS ──
    public String refreshAllStats() {
        studentRepository.findAll().forEach(s -> {
            try {
                leetcodeStatsService.fetchAndStoreStats(s.getId());
            } catch (Exception e) {
                System.out.println("Failed: " + s.getLeetcodeUsername());
            }
        });
        return "All stats refreshed";
    }

    // ── TOTAL STUDENT COUNT ──
    public long getTotalStudents() {
        return studentRepository.count();
    }

    // ── ANALYTICS ──
    public Map<String, Object> getAnalytics(String department, String section, Integer yearFilter) {

        List<Student> allStudents = studentRepository.findAll();

        // 🔥 Filter by params if provided
        if (department != null && !department.isBlank()) {
            allStudents = allStudents.stream()
                .filter(s -> department.equalsIgnoreCase(s.getDepartment()))
                .collect(Collectors.toList());
        }
        if (section != null && !section.isBlank()) {
            allStudents = allStudents.stream()
                .filter(s -> section.equalsIgnoreCase(s.getSection()))
                .collect(Collectors.toList());
        }
        if (yearFilter != null) {
            allStudents = allStudents.stream()
                .filter(s -> yearFilter == s.getYear())
                .collect(Collectors.toList());
        }

        int totalEasy = 0, totalMedium = 0, totalHard = 0, totalSolved = 0;

        Map<String, int[]> deptMap     = new LinkedHashMap<>();
        Map<Integer, int[]> yearMap    = new LinkedHashMap<>();
        Map<String, List<Map<String, Object>>> topFivePerDept = new LinkedHashMap<>();

        for (Student s : allStudents) {

            var statsOpt = statsRepository.findByStudent_Id(s.getId());
            if (statsOpt.isEmpty()) continue;

            var stats = statsOpt.get();

            int easy   = stats.getEasySolved();
            int medium = stats.getMediumSolved();
            int hard   = stats.getHardSolved();
            int total  = stats.getTotalSolved();

            totalEasy   += easy;
            totalMedium += medium;
            totalHard   += hard;
            totalSolved += total;

            // Dept aggregation
            String dept = s.getDepartment();
            deptMap.computeIfAbsent(dept, k -> new int[5]);
            deptMap.get(dept)[0] += total;
            deptMap.get(dept)[1] += easy;
            deptMap.get(dept)[2] += medium;
            deptMap.get(dept)[3] += hard;
            deptMap.get(dept)[4]++;

            // 🔥 Fixed: renamed loop variable to studentYear (was shadowing parameter)
            int studentYear = s.getYear();
            yearMap.computeIfAbsent(studentYear, k -> new int[2]);
            yearMap.get(studentYear)[0] += total;
            yearMap.get(studentYear)[1]++;

            // Top 5 per dept
            topFivePerDept.computeIfAbsent(dept, k -> new ArrayList<>());
            Map<String, Object> studentRow = new LinkedHashMap<>();
            studentRow.put("name", s.getName());
            studentRow.put("leetcodeUsername", s.getLeetcodeUsername());
            studentRow.put("totalSolved", total);
            studentRow.put("easySolved", easy);
            studentRow.put("mediumSolved", medium);
            studentRow.put("hardSolved", hard);
            topFivePerDept.get(dept).add(studentRow);
        }

        // Sort and trim to top 5 per dept
        topFivePerDept.forEach((dept, list) -> {
            list.sort((a, b) -> Integer.compare(
                (int) b.get("totalSolved"),
                (int) a.get("totalSolved")
            ));
            if (list.size() > 5)
                topFivePerDept.put(dept, list.subList(0, 5));
        });

        // ── Dept chart ──
        List<Map<String, Object>> deptChart = new ArrayList<>();
        deptMap.forEach((dept, arr) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("dept", dept);
            row.put("totalSolved", arr[0]);
            row.put("easy", arr[1]);
            row.put("medium", arr[2]);
            row.put("hard", arr[3]);
            row.put("students", arr[4]);
            row.put("avgSolved", arr[4] > 0 ? arr[0] / arr[4] : 0);
            deptChart.add(row);
        });

        // ── Year chart ──
        List<Map<String, Object>> yearChart = new ArrayList<>();
        yearMap.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("year", "Year " + entry.getKey());
                row.put("totalSolved", entry.getValue()[0]);
                row.put("students", entry.getValue()[1]);
                row.put("avgSolved",
                    entry.getValue()[1] > 0
                        ? entry.getValue()[0] / entry.getValue()[1] : 0);
                yearChart.add(row);
            });

        // ── Difficulty donut ──
        List<Map<String, Object>> difficultyChart = List.of(
            Map.of("name", "Easy",   "value", totalEasy,   "color", "#4ade80"),
            Map.of("name", "Medium", "value", totalMedium, "color", "#facc15"),
            Map.of("name", "Hard",   "value", totalHard,   "color", "#f87171")
        );

        // ── Final response ──
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalStudents", allStudents.size());
        result.put("totalSolved", totalSolved);
        result.put("totalEasy", totalEasy);
        result.put("totalMedium", totalMedium);
        result.put("totalHard", totalHard);
        result.put("deptChart", deptChart);
        result.put("yearChart", yearChart);
        result.put("difficultyChart", difficultyChart);
        result.put("topFivePerDept", topFivePerDept);

        return result;
    }
}