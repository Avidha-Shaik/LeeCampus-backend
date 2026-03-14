package com.leecampus.service;

import com.leecampus.dto.DashboardResponseDTO;
import com.leecampus.dto.LeaderboardDTO;
import com.leecampus.dto.MyRankResponseDTO;
import com.leecampus.entity.LeetcodeStats;
import com.leecampus.repository.LeetcodeStatsRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final LeetcodeStatsRepository statsRepository;

    public DashboardService(LeetcodeStatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    public DashboardResponseDTO getOverallDashboard(int page, int size) {
        return buildDashboard(statsRepository.findAll(buildPageable(page, size)));
    }

    public DashboardResponseDTO getYearDashboard(int year, int page, int size) {
        return buildDashboard(statsRepository.findByStudent_Year(year, buildPageable(page, size)));
    }

    public DashboardResponseDTO getCourseDashboard(String course, int page, int size) {
        return buildDashboard(statsRepository.findByStudent_Course(course, buildPageable(page, size)));
    }

    public DashboardResponseDTO getDepartmentDashboard(String department, int page, int size) {
        return buildDashboard(statsRepository.findByStudent_Department(department, buildPageable(page, size)));
    }

    public DashboardResponseDTO getYearCourseDeptSectionDashboard(
            int year, String course, String department, String section, int page, int size) {
        return buildDashboard(
            statsRepository.findByStudent_YearAndStudent_CourseAndStudent_DepartmentAndStudent_Section(
                year, course, department, section, buildPageable(page, size))
        );
    }

    public DashboardResponseDTO searchStudents(String query, int page, int size) {
        return buildDashboard(
            statsRepository.searchStudents(query.trim(), buildPageable(page, size))
        );
    }

    // ==============================
    // 🔥 MY RANK — find which page the student is on under current filter
    // ==============================
    public MyRankResponseDTO getMyRankPage(Long studentId, String filterType, String filterValue, int size) {
        // Get ALL entries for this filter sorted by totalSolved desc, no pagination
        Sort sort = Sort.by("totalSolved").descending();
        List<LeetcodeStats> all;

        switch (filterType) {
            case "year"       -> all = statsRepository.findByStudent_Year(Integer.parseInt(filterValue), sort);
            case "department" -> all = statsRepository.findByStudent_Department(filterValue, sort);
            case "course"     -> all = statsRepository.findByStudent_Course(filterValue, sort);
            default           -> all = statsRepository.findAll(sort);
        }

        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getStudent().getId().equals(studentId)) {
                int rank = i + 1;
                int page = i / size;
                MyRankResponseDTO dto = new MyRankResponseDTO();
                dto.setRank(rank);
                dto.setPage(page);
                return dto;
            }
        }

        // Student not found in this filter (e.g. different dept)
        MyRankResponseDTO dto = new MyRankResponseDTO();
        dto.setRank(-1);
        dto.setPage(-1);
        return dto;
    }

    // ==============================
    // PRIVATE HELPERS
    // ==============================
    private Pageable buildPageable(int page, int size) {
        return PageRequest.of(page, size, Sort.by("totalSolved").descending());
    }

    private DashboardResponseDTO buildDashboard(Page<LeetcodeStats> page) {
        DashboardResponseDTO dto = new DashboardResponseDTO();
        List<LeaderboardDTO> leaderboard = new ArrayList<>();
        int rankStart = page.getNumber() * page.getSize() + 1;

        for (int i = 0; i < page.getContent().size(); i++) {
            LeetcodeStats s = page.getContent().get(i);
            LeaderboardDTO lb = new LeaderboardDTO();
            lb.setRank(rankStart + i);
            lb.setStudentId(s.getStudent().getId());
            lb.setLeetcodeUsername(s.getStudent().getLeetcodeUsername());
            lb.setName(s.getStudent().getName());
            lb.setDepartment(s.getStudent().getDepartment());
            lb.setCourse(s.getStudent().getCourse());
            lb.setSection(s.getStudent().getSection());
            lb.setYear(s.getStudent().getYear());
            lb.setTotalSolved(s.getTotalSolved());
            lb.setEasySolved(s.getEasySolved());
            lb.setMediumSolved(s.getMediumSolved());
            lb.setHardSolved(s.getHardSolved());
            lb.setLeetcodeRank(s.getLeetcodeRank());
            leaderboard.add(lb);
        }

        dto.setLeaderboard(leaderboard);
        dto.setCurrentPage(page.getNumber());
        dto.setTotalPages(page.getTotalPages());
        dto.setTotalElements(page.getTotalElements());
        return dto;
    }
}