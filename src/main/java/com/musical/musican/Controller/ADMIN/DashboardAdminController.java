package com.musical.musican.Controller.ADMIN;

import com.musical.musican.Service.AdminDashboardService;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class DashboardAdminController {

    @Autowired
    private AdminDashboardService dashboardService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            var stats = dashboardService.getDashboardStats();
            var activities = dashboardService.getRecentActivities();
            
            model.addAttribute("stats", stats);
            model.addAttribute("recentActivities", activities);
            
        } catch (Exception e) {
            model.addAttribute("stats", Map.of(
                "totalUsers", 0L, "activeUsers", 0L, "musicianCount", 0L,
                "adminCount", 0L, "userCount", 0L, "performance", 98.0
            ));
            model.addAttribute("recentActivities", List.of());
            System.err.println("Dashboard error: " + e.getMessage());
        }
        
        return "Admin/dashboardAdmin";
    }
}