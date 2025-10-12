package com.musical.musican.Controller;

import com.musical.musican.Model.Entity.Account;
import com.musical.musican.Model.DTO.AccountDTO;
import com.musical.musican.Service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AccountDTO accountDTO) {
        try {
            Account account = accountService.registerAccount(
                    accountDTO.getUsername(),
                    accountDTO.getEmail(),
                    accountDTO.getPassword(),
                    accountDTO.getFullName());
            return ResponseEntity.ok("Đăng ký thành công!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        try {
            boolean isVerified = accountService.verifyOtp(email, otp);
            if (isVerified) {
                return ResponseEntity.ok("Xác thực OTP thành công! Tài khoản đã được kích hoạt.");
            }
            return ResponseEntity.badRequest().body("Xác thực OTP thất bại!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser() {
        Account currentUser = accountService.getCurrentUser();
        if (currentUser != null) {
            return ResponseEntity.ok(currentUser);
        }
        return ResponseEntity.status(401).body("Chưa đăng nhập");
    }
}