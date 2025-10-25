package com.musical.musican.Service;

import java.util.List;
import java.util.Map;

public interface AdminDashboardService {

    Map<String, Object> getDashboardStats();
    List<Map<String, Object>> getRecentActivities();
}
