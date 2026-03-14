package com.leecampus.service;

import com.leecampus.config.JwtUtil;
import com.leecampus.dto.*;
import com.leecampus.entity.Faculty;
import com.leecampus.entity.Student;
import com.leecampus.repository.FacultyRepository;
import com.leecampus.repository.StudentRepository;
import com.leecampus.repository.LeetcodeStatsRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;
    private final LeetcodeStatsRepository statsRepository;
    private final JwtUtil jwtUtil;

    public FacultyService(FacultyRepository facultyRepository,
                          StudentRepository studentRepository,
                          LeetcodeStatsRepository statsRepository,
                          JwtUtil jwtUtil) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
        this.statsRepository = statsRepository;
        this.jwtUtil = jwtUtil;
    }

    // ── LOGIN ──
    public AuthResponseDTO login(FacultyRequestDTO dto) {

        Faculty faculty = facultyRepository
                .findByEmail(dto.getEmail().trim())
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        if (!faculty.getPassword().trim().equals(dto.getPassword().trim()))
            throw new RuntimeException("Invalid credentials");

        String token = jwtUtil.generateToken(
                faculty.getEmail(), "FACULTY", faculty.getId());

        return new AuthResponseDTO(
                token, "FACULTY",
                faculty.getId(),
                faculty.getName(),
                faculty.getEmail()
        );
    }

    // ── GET FACULTY PROFILE ──
    public FacultyResponseDTO getProfile(Long facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        FacultyResponseDTO dto = new FacultyResponseDTO();
        dto.setId(faculty.getId());
        dto.setName(faculty.getName());
        dto.setEmail(faculty.getEmail());
        dto.setDepartment(faculty.getDepartment());
        dto.setSection(faculty.getSection());
        dto.setYear(faculty.getYear());
        return dto;
    }

    // ── GET STUDENTS WITH STATS (flexible filter) ──
    public List<Map<String, Object>> getStudentsWithStats(
            String department,
            String section,
            Integer year) {

        List<Student> students = studentRepository.findAll().stream()
                .filter(s -> s.getDepartment().equalsIgnoreCase(department))
                .filter(s -> section == null || section.isEmpty() ||
                        s.getSection().equalsIgnoreCase(section))
                .filter(s -> year == null ||
                        s.getYear() == year)
                .collect(Collectors.toList());

        List<Map<String, Object>> result = new ArrayList<>();

        for (Student s : students) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("studentId", s.getId());
            row.put("name", s.getName());
            row.put("email", s.getEmail());
            row.put("rollNumber", s.getRollNumber());
            row.put("leetcodeUsername", s.getLeetcodeUsername());
            row.put("department", s.getDepartment());
            row.put("course", s.getCourse());
            row.put("section", s.getSection());
            row.put("year", s.getYear());

            // Attach stats
            statsRepository.findByStudent_Id(s.getId()).ifPresentOrElse(
                stats -> {
                    row.put("totalSolved", stats.getTotalSolved());
                    row.put("easySolved", stats.getEasySolved());
                    row.put("mediumSolved", stats.getMediumSolved());
                    row.put("hardSolved", stats.getHardSolved());
                    row.put("leetcodeRank", stats.getLeetcodeRank());
                    row.put("lastUpdated", stats.getLastUpdated());
                },
                () -> {
                    row.put("totalSolved", 0);
                    row.put("easySolved", 0);
                    row.put("mediumSolved", 0);
                    row.put("hardSolved", 0);
                    row.put("leetcodeRank", 0);
                    row.put("lastUpdated", null);
                }
            );

            result.add(row);
        }

        // Sort by totalSolved descending
        result.sort((a, b) ->
            Integer.compare(
                (int) b.get("totalSolved"),
                (int) a.get("totalSolved")
            )
        );

        // Add rank
        for (int i = 0; i < result.size(); i++) {
            result.get(i).put("rank", i + 1);
        }

        return result;
    }

    // ── DEPT SUMMARY STATS ──
    public Map<String, Object> getDeptSummary(String department) {

        List<Student> students = studentRepository.findAll().stream()
                .filter(s -> s.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());

        int totalStudents = students.size();
        int totalSolved = 0, totalEasy = 0, totalMedium = 0, totalHard = 0;

        for (Student s : students) {
            Optional<com.leecampus.entity.LeetcodeStats> stats =
                statsRepository.findByStudent_Id(s.getId());
            if (stats.isPresent()) {
                totalSolved  += stats.get().getTotalSolved();
                totalEasy    += stats.get().getEasySolved();
                totalMedium  += stats.get().getMediumSolved();
                totalHard    += stats.get().getHardSolved();
            }
        }

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("department", department);
        summary.put("totalStudents", totalStudents);
        summary.put("totalSolved", totalSolved);
        summary.put("totalEasy", totalEasy);
        summary.put("totalMedium", totalMedium);
        summary.put("totalHard", totalHard);
        summary.put("avgSolved",
            totalStudents > 0 ? totalSolved / totalStudents : 0);

        return summary;
    }
    
 // ── REGISTER FACULTY ──
    public FacultyResponseDTO registerFaculty(FacultyRegisterDTO dto) {

        if (facultyRepository.findByEmail(dto.getEmail().trim()).isPresent())
            throw new RuntimeException("Email already exists");

        Faculty faculty = new Faculty();
        faculty.setName(dto.getName());
        faculty.setEmail(dto.getEmail().trim());
        faculty.setPassword(dto.getPassword().trim());
        faculty.setDepartment(dto.getDepartment());
        faculty.setSection(dto.getSection());
        faculty.setYear(dto.getYear());

        Faculty saved = facultyRepository.save(faculty);

        FacultyResponseDTO response = new FacultyResponseDTO();
        response.setId(saved.getId());
        response.setName(saved.getName());
        response.setEmail(saved.getEmail());
        response.setDepartment(saved.getDepartment());
        response.setSection(saved.getSection());
        response.setYear(saved.getYear());

        return response;
    }
}