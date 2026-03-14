package com.leecampus.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leecampus.dto.LeetcodeStatsResponseDTO;
import com.leecampus.entity.LeetcodeStats;
import com.leecampus.entity.Student;
import com.leecampus.repository.LeetcodeStatsRepository;
import com.leecampus.repository.StudentRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class LeetcodeStatsService {

    private final LeetcodeStatsRepository statsRepository;
    private final StudentRepository studentRepository;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public LeetcodeStatsService(
            LeetcodeStatsRepository statsRepository,
            StudentRepository studentRepository) {

        this.statsRepository = statsRepository;
        this.studentRepository = studentRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    // ==============================
    // FETCH & STORE STATS
    // ==============================
    public LeetcodeStats fetchAndStoreStats(Long studentId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        String username = student.getLeetcodeUsername();
        String url = "https://alfa-leetcode-api.onrender.com/" + username + "/profile";

        try {

            String json = restTemplate.getForObject(url, String.class);

            if (json == null)
                throw new RuntimeException("Empty response from Leetcode API");

            Map<String, Object> response =
                    objectMapper.readValue(
                            json,
                            new TypeReference<Map<String, Object>>() {}
                    );

            // ❌ If GraphQL error exists → invalid user
            if (response.containsKey("errors")) {
                throw new RuntimeException("Leetcode user does not exist");
            }

            // 🔥 Extract stats directly (top-level keys)
            int totalSolved = getInt(response.get("totalSolved"));
            int easySolved = getInt(response.get("easySolved"));
            int mediumSolved = getInt(response.get("mediumSolved"));
            int hardSolved = getInt(response.get("hardSolved"));
            int ranking = getInt(response.get("ranking"));

            // Safety check
            if (totalSolved == 0 && ranking == 0) {
                throw new RuntimeException("Invalid Leetcode user");
            }

            LeetcodeStats stats = statsRepository
                    .findByStudent_Id(studentId)
                    .orElse(new LeetcodeStats());

            stats.setUsername(username);
            stats.setTotalSolved(totalSolved);
            stats.setEasySolved(easySolved);
            stats.setMediumSolved(mediumSolved);
            stats.setHardSolved(hardSolved);
            stats.setLeetcodeRank(ranking);
            stats.setStudent(student);
            stats.setLastUpdated(LocalDateTime.now());

            return statsRepository.save(stats);

        } catch (Exception e) {
            throw new RuntimeException("Leetcode fetch failed: " + e.getMessage());
        }
    }
    // ==============================
    // GET STATS
    // ==============================
    public LeetcodeStatsResponseDTO getStats(Long studentId) {

        LeetcodeStats stats = statsRepository
                .findByStudent_Id(studentId)
                .orElseThrow(() ->
                        new RuntimeException("Stats not found for this student"));

        return mapToDTO(stats);
    }


    // ==============================
    // VALIDATE USERNAME
    // ==============================
 // ==============================
 // VALIDATE USERNAME
 // ==============================
 public boolean validateLeetcodeUsername(String username) {
     try {
         String url = "http://localhost:3000/" + username + "/profile";
         String json = restTemplate.getForObject(url, String.class);

         if (json == null) return false;

         Map<String, Object> response =
                 objectMapper.readValue(
                         json,
                         new TypeReference<Map<String, Object>>() {}
                 );

         // If errors key present → invalid user
         if (response.containsKey("errors")) return false;

         // 🔥 Stats are top-level, not nested under "data"
         Object totalSolved      = response.get("totalSolved");
         Object matchedUserStats = response.get("matchedUserStats");

         return totalSolved != null && matchedUserStats != null;

     } catch (Exception e) {
         return false;
     }
 }    // ==============================
    // SCHEDULER (Every 10 mins)
    // ==============================
    @Scheduled(fixedRate = 6000000)
    public void updateAllStudentStats() {

        List<Student> students = studentRepository.findAll();

        for (Student student : students) {
            try {
                fetchAndStoreStats(student.getId());
            } catch (Exception e) {
                System.out.println("Failed for "
                        + student.getLeetcodeUsername()
                        + " → " + e.getMessage());
            }
        }
    }

    // ==============================
    // DTO MAPPING
    // ==============================
    private LeetcodeStatsResponseDTO mapToDTO(LeetcodeStats stats) {

        LeetcodeStatsResponseDTO dto = new LeetcodeStatsResponseDTO();

        dto.setStudentId(stats.getStudent().getId());
        dto.setName(stats.getStudent().getName());
        dto.setYear(stats.getStudent().getYear());
        dto.setDepartment(stats.getStudent().getDepartment());
        dto.setCourse(stats.getStudent().getCourse());
        dto.setSection(stats.getStudent().getSection());

        dto.setUsername(stats.getUsername());
        dto.setTotalSolved(stats.getTotalSolved());
        dto.setEasySolved(stats.getEasySolved());
        dto.setMediumSolved(stats.getMediumSolved());
        dto.setHardSolved(stats.getHardSolved());
        dto.setLeetcodeRank(stats.getLeetcodeRank());
        dto.setLastUpdated(stats.getLastUpdated());

        return dto;
    }
    

    // ==============================
    // SAFE INTEGER CONVERSION
    // ==============================
    private int getInt(Object value) {

        if (value == null) return 0;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number) return ((Number) value).intValue();
        return 0;
    }
}