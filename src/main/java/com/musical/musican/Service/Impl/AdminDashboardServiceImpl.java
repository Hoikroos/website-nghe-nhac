package com.musical.musican.Service.Impl;

import com.musical.musican.Model.Entity.Account;
import com.musical.musican.Repository.AccountRepository;
import com.musical.musican.Repository.ArtistRepository;
import com.musical.musican.Repository.CategoryRepository;
import com.musical.musican.Service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalUsers", accountRepository.count());
        stats.put("activeUsers", accountRepository.countByActiveTrue());
        stats.put("performance", 98.5);
        
        stats.put("adminCount", accountRepository.countByRole(Account.Role.ADMIN));
        stats.put("musicianCount", accountRepository.countByRole(Account.Role.MUSICIAN));
        stats.put("userCount", accountRepository.countByRole(Account.Role.USER));
        
        return stats;
    }

    @Override
    public List<Map<String, Object>> getRecentActivities() {
        List<Map<String, Object>> activities = new ArrayList<>();
        
        accountRepository.findTop5ByOrderByCreatedAtDesc()
            .forEach(acc -> activities.add(createActivity(
                "fas fa-user-plus", 
                "Thêm tài khoản: " + (acc.getFullname() != null ? acc.getFullname() : acc.getUsername()),
                acc.getCreatedAt()
            )));

        artistRepository.findTop3ByOrderByCreatedAtDesc()
            .forEach(art -> activities.add(createActivity(
                "fas fa-music", 
                "Thêm nghệ sĩ: " + art.getName(),
                art.getCreatedAt()
            )));

        categoryRepository.findTop3ByOrderByIdDesc()
            .forEach(cat -> activities.add(createActivity(
                "fas fa-tags", 
                "Thêm thể loại: " + cat.getName(),
                cat.getCreatedAt()
            )));
        Collections.reverse(activities);

        return activities.size() > 8 ? activities.subList(0, 8) : activities;
    }

    private Map<String, Object> createActivity(String icon, String title, LocalDateTime dateTime) {
        return Map.of(
            "icon", icon,
            "title", title,
            "time", timeAgo(dateTime)
        );
    }

    private String timeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return "Vừa xong";
        LocalDateTime now = LocalDateTime.now();
        long hours = java.time.Duration.between(dateTime, now).toHours();
        
        if (hours < 1) return "Vừa xong";
        if (hours < 24) return hours + " giờ trước";
        if (hours < 168) return (hours / 24) + " ngày trước";
        return (hours / 24 / 7) + " tuần trước";
    }
}