package com.musical.musican.Controller.ADMIN;

import com.musical.musican.Model.Entity.Account;
import com.musical.musican.Service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class ProfileAdminController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/profile")
    public String getProfilePage() {
        return "Admin/profileAdmin";
    }
}

@RestController
@RequestMapping("/api/current-user")
@PreAuthorize("hasAuthority('ADMIN')")
class ProfileApiController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private HttpSession session;

    @GetMapping
    public ResponseEntity<Account> getCurrentUser() {
        try {
            Account currentUser = accountService.getCurrentUser();
            return ResponseEntity.ok(currentUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping
    public ResponseEntity<Account> updateProfile(@RequestBody Account profileDetails) {
        try {
            Account currentUser = accountService.getCurrentUser();
            Account updatedUser = accountService.updateProfile(currentUser.getId(), profileDetails);
            // Update session
            session.setAttribute("currentUser", updatedUser);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("avatar") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File không được để trống!");
            }
            if (!file.getContentType().startsWith("image/")) {
                throw new IllegalArgumentException("File phải là ảnh!");
            }
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("Kích thước ảnh không được vượt quá 5MB!");
            }

            // Lưu file
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            String uploadDir = "uploads/avatars/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());
            Account currentUser = accountService.getCurrentUser();
            if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
                Path oldAvatarPath = Paths.get("src/main/resources/static" + currentUser.getAvatar());
                if (Files.exists(oldAvatarPath)) {
                    Files.delete(oldAvatarPath);
                }
            }
            currentUser.setAvatar("/uploads/avatars/" + fileName);
            Account updatedUser = accountService.updateProfile(currentUser.getId(), currentUser);
            session.setAttribute("currentUser", updatedUser);

            return ResponseEntity.ok(updatedUser);
        } catch (IOException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest request) {
        try {
            accountService.changePassword(request.getCurrentPassword(), request.getNewPassword());
            // Invalidate session to force re-login
            session.invalidate();
            return ResponseEntity.ok("Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public static class PasswordChangeRequest {
        private String currentPassword;
        private String newPassword;

        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}