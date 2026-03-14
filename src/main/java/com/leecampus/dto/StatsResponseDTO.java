package com.leecampus.dto;

import java.util.List;

public class StatsResponseDTO {

    private long totalStudents;
    private long totalSolved;
    private long totalEasy;
    private long totalMedium;
    private long totalHard;

    private List<LeaderboardDTO> topFive;

    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }

    public long getTotalSolved() { return totalSolved; }
    public void setTotalSolved(long totalSolved) { this.totalSolved = totalSolved; }

    public long getTotalEasy() { return totalEasy; }
    public void setTotalEasy(long totalEasy) { this.totalEasy = totalEasy; }

    public long getTotalMedium() { return totalMedium; }
    public void setTotalMedium(long totalMedium) { this.totalMedium = totalMedium; }

    public long getTotalHard() { return totalHard; }
    public void setTotalHard(long totalHard) { this.totalHard = totalHard; }

    public List<LeaderboardDTO> getTopFive() { return topFive; }
    public void setTopFive(List<LeaderboardDTO> topFive) { this.topFive = topFive; }
}