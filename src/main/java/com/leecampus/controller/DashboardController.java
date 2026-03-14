package com.leecampus.controller;

import com.leecampus.dto.DashboardResponseDTO;
import com.leecampus.dto.MyRankResponseDTO;
import com.leecampus.service.DashboardService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leecampus/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/overall")
    public DashboardResponseDTO overall(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return dashboardService.getOverallDashboard(page, size);
    }

    @GetMapping("/course/{course}")
    public DashboardResponseDTO byCourse(
            @PathVariable String course,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return dashboardService.getCourseDashboard(course, page, size);
    }

    @GetMapping("/department/{department}")
    public DashboardResponseDTO byDepartment(
            @PathVariable String department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return dashboardService.getDepartmentDashboard(department, page, size);
    }

    @GetMapping("/year/{year}")
    public DashboardResponseDTO byYear(
            @PathVariable int year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return dashboardService.getYearDashboard(year, page, size);
    }

    @GetMapping("/year/{year}/course/{course}/department/{department}/section/{section}")
    public DashboardResponseDTO fullFilter(
            @PathVariable int year,
            @PathVariable String course,
            @PathVariable String department,
            @PathVariable String section,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return dashboardService.getYearCourseDeptSectionDashboard(
                year, course, department, section, page, size);
    }

    @GetMapping("/search")
    public DashboardResponseDTO search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return dashboardService.searchStudents(query, page, size);
    }

    // ==============================
    // 🔥 MY RANK — returns { rank, page } for the logged-in student
    // GET /leecampus/dashboard/my-rank?studentId=5&filterType=overall&filterValue=&size=10
    // ==============================
    @GetMapping("/my-rank")
    public MyRankResponseDTO myRank(
            @RequestParam Long studentId,
            @RequestParam(defaultValue = "overall") String filterType,
            @RequestParam(defaultValue = "") String filterValue,
            @RequestParam(defaultValue = "10") int size) {
        return dashboardService.getMyRankPage(studentId, filterType, filterValue, size);
    }
}