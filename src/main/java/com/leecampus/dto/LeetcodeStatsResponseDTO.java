package com.leecampus.dto;

import java.time.LocalDateTime;

public class LeetcodeStatsResponseDTO {

    private Long studentId;
    private String name;
    private int year;
    private String department;
    private String course;
    private String section;

    private String username;
    private int totalSolved;
    private int easySolved;
    private int mediumSolved;
    private int hardSolved;
    private int leetcodeRank;

    private LocalDateTime lastUpdated;

    // ===== GETTERS & SETTERS =====

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getTotalSolved() { return totalSolved; }
    public void setTotalSolved(int totalSolved) { this.totalSolved = totalSolved; }

    public int getEasySolved() { return easySolved; }
    public void setEasySolved(int easySolved) { this.easySolved = easySolved; }

    public int getMediumSolved() { return mediumSolved; }
    public void setMediumSolved(int mediumSolved) { this.mediumSolved = mediumSolved; }

    public int getHardSolved() { return hardSolved; }
    public void setHardSolved(int hardSolved) { this.hardSolved = hardSolved; }

    public int getLeetcodeRank() { return leetcodeRank; }
    public void setLeetcodeRank(int leetcodeRank) { this.leetcodeRank = leetcodeRank; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}