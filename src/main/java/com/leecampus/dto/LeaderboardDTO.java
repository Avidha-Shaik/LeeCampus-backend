package com.leecampus.dto;

public class LeaderboardDTO {

    private Long studentId;
    private int rank;
    private int globalRank;
    private String leetcodeUsername;
    private String name;
    private String department;
    private String course;
    private String section;
    private int year;
    private int totalSolved;
    private int easySolved;
    private int mediumSolved;
    private int hardSolved;
    private int leetcodeRank;

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getLeetcodeUsername() { return leetcodeUsername; }
    public void setLeetcodeUsername(String leetcodeUsername) { this.leetcodeUsername = leetcodeUsername; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public int getGlobalRank() { return globalRank; }
    public void setGlobalRank(int globalRank) { this.globalRank = globalRank; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

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
}