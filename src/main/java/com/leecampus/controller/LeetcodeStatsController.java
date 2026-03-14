package com.leecampus.controller;

import com.leecampus.dto.LeetcodeStatsResponseDTO;
import com.leecampus.entity.LeetcodeStats;
import com.leecampus.service.LeetcodeStatsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leecampus/leetcode-stats")
@CrossOrigin(origins="http://localhost:5173")
public class LeetcodeStatsController {

    private final LeetcodeStatsService statsService;

    public LeetcodeStatsController(LeetcodeStatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/fetch/{studentId}")
    public LeetcodeStats fetchStats(@PathVariable Long studentId) {
        return statsService.fetchAndStoreStats(studentId);
    }

    @GetMapping("/{studentId}")
    public LeetcodeStatsResponseDTO getStats(
            @PathVariable Long studentId) {
        return statsService.getStats(studentId);
    }
}