package com.musical.musican.Service;

import java.util.List;

import com.musical.musican.Model.Entity.Account;

public interface AccountService {
    /**
     * Đăng ký tài khoản mới
     * 
     * @param username tên đăng nhập
     * @param email    địa chỉ email
     * @param password mật khẩu (chưa mã hóa)
     * @param fullname tên hiển thị
     * @return Account đã lưu vào database
     */
    Account registerAccount(String username, String email, String password, String fullname);

    /**
     * Lấy thông tin user đang đăng nhập
     * 
     * @return Account hoặc null nếu chưa đăng nhập
     */
    Account getCurrentUser();

    boolean verifyOtp(String email, String otp);

    List<Account> getAllEmployees();

    Account getEmployeeById(Integer id);

    Account createEmployee(Account account);

    void deleteEmployee(Integer id);

    Account updateEmployee(Integer id, Account accountDetails);

    Account updateProfile(Integer id, Account profileDetails);

    void changePassword(String currentPassword, String newPassword);
}
