package com.leecampus.dto;

public class FacultyResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String department;
    private String section;
    private Integer year;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
}