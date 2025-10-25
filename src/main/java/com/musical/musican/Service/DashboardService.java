package com.musical.musican.Service;

import java.util.List;
import com.musical.musican.Model.DTO.ActivityDto;
import com.musical.musican.Model.DTO.DashboardStatsDto;
import com.musical.musican.Model.Entity.Account;

public interface DashboardService {
    DashboardStatsDto getDashboardStats(Account account);

    List<ActivityDto> getRecentActivities(Account account, int limit);
}
