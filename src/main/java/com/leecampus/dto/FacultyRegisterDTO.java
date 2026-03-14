package com.leecampus.dto;

public class FacultyRegisterDTO {

    private String name;
    private String email;
    private String password;
    private String department;
    private String section;   // nullable
    private Integer year;     // nullable

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
}