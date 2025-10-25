package com.musical.musican.Controller.MUSICAN;

import com.musical.musican.Model.Entity.Account;
import com.musical.musican.Repository.AccountRepository;
import com.musical.musican.Security.CustomUserDetails;
import com.musical.musican.Service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/musican")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AccountRepository accountRepository;

    /**
     * Lấy thông tin tài khoản hiện tại từ Spring Security
     */
    private Account getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("Bạn cần đăng nhập để truy cập trang này!");
        }

        String username;
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails customUserDetails) {
            username = customUserDetails.getUsername();
        } else if (principal instanceof org.springframework.security.core.userdetails.User user) {
            username = user.getUsername();
        } else {
            username = authentication.getName(); // fallback
        }

        if (username == null || username.isEmpty()) {
            throw new IllegalStateException("");
        }

        // Nếu đăng nhập bằng email, hãy dùng findByEmail()
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Tai khoan khoan khong ton tai: " + username));

        return account;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            Account currentUser = getCurrentAccount();

            if (currentUser.getRole() == null || !"MUSICIAN".equalsIgnoreCase(currentUser.getRole().name())) {
                return "redirect:/?error=forbidden";
            }

            model.addAttribute("stats", dashboardService.getDashboardStats(currentUser));
            model.addAttribute("recentActivities", dashboardService.getRecentActivities(currentUser, 5));
            model.addAttribute("currentUser", currentUser);

            return "Musican/dashboard";

        } catch (IllegalStateException ex) {
            return "redirect:/Dangnhap?error=" + ex.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error";
        }
    }

}
