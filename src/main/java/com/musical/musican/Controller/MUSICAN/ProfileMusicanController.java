package com.musical.musican.Controller.MUSICAN;

import com.musical.musican.Model.Entity.Account;
import com.musical.musican.Service.AccountService;
import com.musical.musican.Service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/musican/profile")
public class ProfileMusicanController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping
    public String profilePage(Model model, RedirectAttributes redirectAttributes) {
        Account currentUser = accountService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/DangNhap";
        }
        model.addAttribute("message", redirectAttributes.getFlashAttributes().get("message"));
        model.addAttribute("errorMessage", redirectAttributes.getFlashAttributes().get("errorMessage"));
        
        model.addAttribute("user", currentUser);
        return "Musican/profile";
    }
    @PostMapping("/update")
    public String updateProfile(
            @RequestParam(required = false) String fullname,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) MultipartFile avatar,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String bio,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Account currentUser = accountService.getCurrentUser();
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng!");
                return "redirect:/musican/profile";
            }
            if (avatar != null && !avatar.isEmpty()) {
                if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
                    fileUploadService.deleteFile(currentUser.getAvatar());
                }
                String imageUrl = fileUploadService.uploadFile(avatar, "avatars");
                currentUser.setAvatar(imageUrl);
                System.out.println("✅ NEW AVATAR: " + imageUrl);
            }
            if (fullname != null && !fullname.trim().isEmpty()) {
                currentUser.setFullname(fullname.trim());
            }
            if (email != null && !email.trim().isEmpty()) {
                currentUser.setEmail(email.trim());
            }
            if (bio != null) {
                currentUser.setBio(bio.trim().isEmpty() ? null : bio.trim());
            }
            if (active != null) {
                currentUser.setActive(active);
            }
            accountService.updateProfile(currentUser.getId(), currentUser);
            redirectAttributes.addFlashAttribute("message", "Cập nhật hồ sơ thành công!");
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }

        return "redirect:/musican/profile";
    }
}