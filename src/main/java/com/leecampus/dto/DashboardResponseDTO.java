package com.leecampus.dto;

import java.util.List;

public class DashboardResponseDTO {

    private List<LeaderboardDTO> leaderboard;

    private int currentPage;
    private int totalPages;
    private long totalElements;

    public List<LeaderboardDTO> getLeaderboard() {
        return leaderboard;
    }

    public void setLeaderboard(List<LeaderboardDTO> leaderboard) {
        this.leaderboard = leaderboard;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}