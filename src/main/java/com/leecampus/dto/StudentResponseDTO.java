package com.leecampus.dto;

public class StudentResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String rollNumber;
    private String leetcodeUsername;
    private String section;
    private int year;
    private String department;
    private String course;
    
    // getters & setters
    
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRollNumber() {
		return rollNumber;
	}
	public void setRollNumber(String rollNumber) {
		this.rollNumber = rollNumber;
	}
	public String getLeetcodeUsername() {
		return leetcodeUsername;
	}
	public void setLeetcodeUsername(String leetcodeUsername) {
		this.leetcodeUsername = leetcodeUsername;
	}
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getCourse() {
		return course;
	}
	public void setCourse(String course) {
		this.course = course;
	}

   
    
    
}