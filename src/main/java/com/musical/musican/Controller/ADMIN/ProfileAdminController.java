package com.musical.musican.Controller.ADMIN;

import com.musical.musican.Model.Entity.Account;
import com.musical.musican.Service.AccountService;
import com.musical.musican.Service.FileUploadService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/profile")
public class ProfileAdminController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private HttpSession session;

    @GetMapping
    public String getProfilePage(Model model) {
        Account currentUser = accountService.getCurrentUser();
        model.addAttribute("account", currentUser);
        return "Admin/profileAdmin";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam("name") String name,
                                @RequestParam(value = "bio", required = false) String bio,
                                @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                                Model model) {
        try {
            Account currentUser = accountService.getCurrentUser();
            currentUser.setFullname(name);
            currentUser.setBio(bio);

            if (avatarFile != null && !avatarFile.isEmpty()) {
                if (currentUser.getAvatar() != null) {
                    fileUploadService.deleteFile(currentUser.getAvatar());
                }
                String avatarPath = fileUploadService.uploadFile(avatarFile, "avatars");
                currentUser.setAvatar(avatarPath);
            }

            Account updated = accountService.updateProfile(currentUser.getId(), currentUser);
            session.setAttribute("currentUser", updated);
            model.addAttribute("account", updated);
            model.addAttribute("message", "Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi cập nhật hồ sơ: " + e.getMessage());
        }
        return "Admin/profileAdmin";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Model model) {
        Account currentUser = accountService.getCurrentUser();
        model.addAttribute("account", currentUser);

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Mật khẩu xác nhận không khớp!");
            return "Admin/profileAdmin";
        }

        try {
            accountService.changePassword(currentPassword, newPassword);
            session.invalidate(); 
            model.addAttribute("message", "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }

        return "Admin/profileAdmin";
    }
}
