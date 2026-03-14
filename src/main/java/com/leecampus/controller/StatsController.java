package com.leecampus.controller;

import com.leecampus.dto.StatsResponseDTO;
import com.leecampus.service.StatsService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/leecampus/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping
    public StatsResponseDTO overall() {
        return statsService.getOverallStats();
    }

    @GetMapping("/{year}")
    public StatsResponseDTO byYear(@PathVariable int year) {
        return statsService.getYearStats(year);
    }

    @GetMapping("/{year}/{course}")
    public StatsResponseDTO byYearCourse(
            @PathVariable int year,
            @PathVariable String course) {

        return statsService.getYearCourseStats(year, course);
    }

    @GetMapping("/{year}/{course}/{department}")
    public StatsResponseDTO byYearCourseDept(
            @PathVariable int year,
            @PathVariable String course,
            @PathVariable String department) {

        return statsService.getYearCourseDeptStats(year, course, department);
    }

    @GetMapping("/{year}/{course}/{department}/{section}")
    public StatsResponseDTO fullFilter(
            @PathVariable int year,
            @PathVariable String course,
            @PathVariable String department,
            @PathVariable String section) {

        return statsService.getFullFilterStats(year, course, department, section);
    }
}