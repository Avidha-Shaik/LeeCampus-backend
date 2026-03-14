package com.leecampus.service;

import com.leecampus.config.JwtUtil;
import com.leecampus.dto.*;
import com.leecampus.entity.Student;
import com.leecampus.repository.LeetcodeStatsRepository;
import com.leecampus.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final JwtUtil jwtUtil;
    private final LeetcodeStatsRepository leetcodeStatsRepository;

    @Autowired
    private LeetcodeStatsService leetcodeStatsService;

    public StudentService(StudentRepository studentRepository,
                          JwtUtil jwtUtil,
                          LeetcodeStatsRepository leetcodeStatsRepository) {
        this.studentRepository = studentRepository;
        this.jwtUtil = jwtUtil;
        this.leetcodeStatsRepository = leetcodeStatsRepository;
    }

    // REGISTER
    @Transactional
    public StudentResponseDTO registerStudent(StudentRequestDTO dto) {

        if (studentRepository.existsByEmail(dto.getEmail()))
            throw new RuntimeException("Email already exists");

        if (studentRepository.existsByRollNumber(dto.getRollNumber()))
            throw new RuntimeException("Roll number already exists");

        if (studentRepository.existsByLeetcodeUsername(dto.getLeetcodeUsername()))
            throw new RuntimeException("Leetcode username already exists");

        boolean valid = leetcodeStatsService
                .validateLeetcodeUsername(dto.getLeetcodeUsername());

        if (!valid)
            throw new RuntimeException("Invalid Leetcode username");

        Student student = new Student();
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setPassword(dto.getPassword().trim());
        student.setLeetcodeUsername(dto.getLeetcodeUsername());
        student.setRollNumber(dto.getRollNumber());
        student.setDepartment(dto.getDepartment());
        student.setCourse(dto.getCourse());
        student.setSection(dto.getSection());
        student.setYear(dto.getYear());

        Student saved = studentRepository.save(student);

        try {
            leetcodeStatsService.fetchAndStoreStats(saved.getId());
        } catch (Exception e) {
            System.out.println("LeetCode stats fetch failed: " + e.getMessage());
        }
        return mapToResponseDTO(saved);
    }

    // GET ALL
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // GET BY ID
    public StudentResponseDTO getStudentById(Long id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return mapToResponseDTO(student);
    }

    // UPDATE
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO dto) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setRollNumber(dto.getRollNumber());
        student.setLeetcodeUsername(dto.getLeetcodeUsername());
        student.setPassword(dto.getPassword());
        student.setSection(dto.getSection());
        student.setYear(dto.getYear());
        student.setDepartment(dto.getDepartment());
        student.setCourse(dto.getCourse());

        Student updated = studentRepository.save(student);

        // refresh stats
        leetcodeStatsService.fetchAndStoreStats(updated.getId());

        return mapToResponseDTO(updated);
    }

    // DELETE (FIXED)
    @Transactional
    public void deleteStudent(Long id) {

        if (!studentRepository.existsById(id))
            throw new RuntimeException("Student not found");

        leetcodeStatsRepository.deleteByStudent_Id(id);
        studentRepository.deleteById(id);
    }

    // LOGIN
    public AuthResponseDTO login(LoginRequestDTO loginRequest) {

        String email = loginRequest.getEmail().trim();
        String password = loginRequest.getPassword().trim();

        Student student = studentRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!student.getPassword().trim().equals(password))
            throw new RuntimeException("Invalid credentials");

        String token = jwtUtil.generateToken(email, "STUDENT", student.getId());

        return new AuthResponseDTO(
                token,
                "STUDENT",
                student.getId(),
                student.getName(),
                student.getEmail()
        );
    }

    // DTO MAPPER
    private StudentResponseDTO mapToResponseDTO(Student student) {

        StudentResponseDTO dto = new StudentResponseDTO();

        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setRollNumber(student.getRollNumber());
        dto.setLeetcodeUsername(student.getLeetcodeUsername());
        dto.setSection(student.getSection());
        dto.setYear(student.getYear());
        dto.setDepartment(student.getDepartment());
        dto.setCourse(student.getCourse());

        return dto;
    }

    // BULK REGISTER
    public List<BulkResponseDTO> registerBulk(List<StudentRequestDTO> dtos) {

        List<BulkResponseDTO> results = new ArrayList<>();

        for (StudentRequestDTO dto : dtos) {

            try {

                if (studentRepository.existsByEmail(dto.getEmail())) {
                    results.add(new BulkResponseDTO(dto.getEmail(), "FAILED", "Email exists"));
                    continue;
                }

                if (studentRepository.existsByRollNumber(dto.getRollNumber())) {
                    results.add(new BulkResponseDTO(dto.getEmail(), "FAILED", "Roll number exists"));
                    continue;
                }

                if (studentRepository.existsByLeetcodeUsername(dto.getLeetcodeUsername())) {
                    results.add(new BulkResponseDTO(dto.getEmail(), "FAILED", "Leetcode username exists"));
                    continue;
                }

                boolean valid = leetcodeStatsService
                        .validateLeetcodeUsername(dto.getLeetcodeUsername());

                if (!valid) {
                    results.add(new BulkResponseDTO(dto.getEmail(), "FAILED", "Invalid Leetcode username"));
                    continue;
                }

                Student student = new Student();

                student.setName(dto.getName());
                student.setEmail(dto.getEmail());
                student.setPassword(dto.getPassword().trim());
                student.setLeetcodeUsername(dto.getLeetcodeUsername());
                student.setRollNumber(dto.getRollNumber());
                student.setDepartment(dto.getDepartment());
                student.setCourse(dto.getCourse());
                student.setSection(dto.getSection());
                student.setYear(dto.getYear());

                Student saved = studentRepository.save(student);

                leetcodeStatsService.fetchAndStoreStats(saved.getId());

                results.add(new BulkResponseDTO(dto.getEmail(), "SUCCESS", "Inserted"));

            } catch (Exception e) {

                results.add(new BulkResponseDTO(dto.getEmail(), "FAILED", e.getMessage()));
            }
        }

        return results;
    }
}