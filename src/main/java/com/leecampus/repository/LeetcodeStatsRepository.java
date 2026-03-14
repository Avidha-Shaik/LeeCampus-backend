package com.leecampus.repository;

import com.leecampus.entity.LeetcodeStats;
import com.leecampus.entity.Student;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LeetcodeStatsRepository
        extends JpaRepository<LeetcodeStats, Long> {

    // ===== DASHBOARD (PAGEABLE) =====

    Page<LeetcodeStats> findByStudent_Year(
            int year, Pageable pageable);

    Page<LeetcodeStats> findByStudent_YearAndStudent_Course(
            int year, String course, Pageable pageable);

    Page<LeetcodeStats> findByStudent_YearAndStudent_CourseAndStudent_Department(
            int year, String course, String department, Pageable pageable);

    Page<LeetcodeStats> findByStudent_YearAndStudent_CourseAndStudent_DepartmentAndStudent_Section(
            int year, String course, String department, String section, Pageable pageable);

    // ===== STATS (LIST) =====
    
    List<LeetcodeStats> findByStudent_Year(int year);

    List<LeetcodeStats> findByStudent_YearAndStudent_Course(
            int year, String course);

    List<LeetcodeStats> findByStudent_YearAndStudent_CourseAndStudent_Department(
            int year, String course, String department);

    List<LeetcodeStats> findByStudent_YearAndStudent_CourseAndStudent_DepartmentAndStudent_Section(
            int year, String course, String department, String section);

    Optional<LeetcodeStats> findByStudent_Id(Long studentId);
    
    
    Page<LeetcodeStats> findByStudent_Course(String course, Pageable pageable);
    Page<LeetcodeStats> findByStudent_Department(String department, Pageable pageable);
//    Page<LeetcodeStats> findByStudent_Year(int year, Pageable pageable);
  
    @Modifying
    @Transactional
    void deleteByStudent_Id(Long studentId);
    
    @Query("SELECT ls FROM LeetcodeStats ls WHERE " +
            "LOWER(ls.student.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(ls.student.rollNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(ls.student.leetcodeUsername) LIKE LOWER(CONCAT('%', :query, '%'))")
     Page<LeetcodeStats> searchStudents(@Param("query") String query, Pageable pageable);

 // In LeetcodeStatsRepository.java — add these if missing
    List<LeetcodeStats> findAll(Sort sort);
    List<LeetcodeStats> findByStudent_Year(int year, Sort sort);
    List<LeetcodeStats> findByStudent_Department(String department, Sort sort);
    List<LeetcodeStats> findByStudent_Course(String course, Sort sort);
    



    
}