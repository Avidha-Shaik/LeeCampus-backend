package com.leecampus.service;

import com.leecampus.dto.*;
import com.leecampus.entity.LeetcodeStats;
import com.leecampus.repository.LeetcodeStatsRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final LeetcodeStatsRepository statsRepository;

    public StatsService(LeetcodeStatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    public StatsResponseDTO getOverallStats() {
        return buildStats(statsRepository.findAll());
    }

    public StatsResponseDTO getYearStats(int year) {
        return buildStats(statsRepository.findByStudent_Year(year));
    }

    public StatsResponseDTO getYearCourseStats(int year, String course) {
        return buildStats(
                statsRepository.findByStudent_YearAndStudent_Course(year, course));
    }

    public StatsResponseDTO getYearCourseDeptStats(
            int year, String course, String department) {

        return buildStats(
                statsRepository.findByStudent_YearAndStudent_CourseAndStudent_Department(
                        year, course, department));
    }

    public StatsResponseDTO getFullFilterStats(
            int year, String course, String department, String section) {

        return buildStats(
                statsRepository
                        .findByStudent_YearAndStudent_CourseAndStudent_DepartmentAndStudent_Section(
                                year, course, department, section));
    }

    private StatsResponseDTO buildStats(List<LeetcodeStats> statsList) {

        StatsResponseDTO dto = new StatsResponseDTO();

        long totalStudents = statsList.size();

        long totalSolved = statsList.stream()
                .mapToLong(LeetcodeStats::getTotalSolved).sum();

        long totalEasy = statsList.stream()
                .mapToLong(LeetcodeStats::getEasySolved).sum();

        long totalMedium = statsList.stream()
                .mapToLong(LeetcodeStats::getMediumSolved).sum();

        long totalHard = statsList.stream()
                .mapToLong(LeetcodeStats::getHardSolved).sum();

        // Top 5
        List<LeaderboardDTO> topFive = statsList.stream()
                .sorted(Comparator.comparingInt(LeetcodeStats::getTotalSolved).reversed())
                .limit(5)
                .map(this::mapToLeaderboard)
                .collect(Collectors.toList());

        dto.setTotalStudents(totalStudents);
        dto.setTotalSolved(totalSolved);
        dto.setTotalEasy(totalEasy);
        dto.setTotalMedium(totalMedium);
        dto.setTotalHard(totalHard);
        dto.setTopFive(topFive);

        return dto;
    }

    private LeaderboardDTO mapToLeaderboard(LeetcodeStats s) {

        LeaderboardDTO dto = new LeaderboardDTO();

        dto.setName(s.getStudent().getName());
        dto.setDepartment(s.getStudent().getDepartment());
        dto.setCourse(s.getStudent().getCourse());
        dto.setSection(s.getStudent().getSection());
        dto.setYear(s.getStudent().getYear());

        dto.setTotalSolved(s.getTotalSolved());
        dto.setEasySolved(s.getEasySolved());
        dto.setMediumSolved(s.getMediumSolved());
        dto.setHardSolved(s.getHardSolved());

        dto.setLeetcodeRank(s.getLeetcodeRank());

        return dto;
    }
    
    
}