package com.musical.musican.Service.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.musical.musican.Model.Entity.Account;
import com.musical.musican.Model.Entity.Account.Role;
import com.musical.musican.Repository.AccountRepository;
import com.musical.musican.Service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;
    private static final Pattern PASSWORD_PATTERN = Pattern
            .compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$");

    @Override
    public Account registerAccount(String username, String email, String password, String fullname) {
        logger.info("Attempting to register user: {}", username);

        // Kiểm tra username đã tồn tại
        if (accountRepository.findByUsername(username).isPresent()) {
            logger.warn("Username already exists: {}", username);
            throw new IllegalArgumentException("Tên đăng nhập đã được sử dụng!");
        }

        // Kiểm tra email đã tồn tại
        if (accountRepository.findByEmail(email).isPresent()) {
            logger.warn("Email already exists: {}", email);
            throw new IllegalArgumentException("Email đã được sử dụng!");
        }

        // Validate mật khẩu
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự gồm chữ hoa, số và ký tự đặc biệt!");
        }

        // Tạo mã OTP
        String otp = generateOTP();
        LocalDateTime otpExpiry = LocalDateTime.now().plusMinutes(10);

        // Tạo đối tượng Account
        Account account = new Account();
        account.setUsername(username);
        account.setEmail(email);
        account.setPassword(passwordEncoder.encode(password));
        account.setFullname(fullname);
        account.setActive(false);
        account.setRole(Role.USER);
        account.setCreatedAt(LocalDateTime.now());
        account.setOtp(otp);
        account.setOtpExpiry(otpExpiry);

        try {
            Account savedAccount = accountRepository.save(account);
            sendOtpEmail(email, otp);
            logger.info("User registered successfully, OTP sent to: {}", email);
            return savedAccount;
        } catch (Exception e) {
            logger.error("Error saving account for user: {}", username, e);
            throw new RuntimeException("Lỗi khi lưu tài khoản vào cơ sở dữ liệu", e);
        }
    }

    @Override
    public Account getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug("Fetching current user: {}", username);
        return accountRepository.findByUsername(username).orElse(null);
    }

    public boolean verifyOtp(String email, String otp) {
        Optional<Account> accountOpt = accountRepository.findByEmail(email);
        if (accountOpt.isEmpty()) {
            logger.warn("No account found for email: {}", email);
            throw new IllegalArgumentException("Tài khoản không tồn tại!");
        }

        Account account = accountOpt.get();
        if (account.getOtp() == null || account.getOtpExpiry() == null) {
            logger.warn("No OTP found for email: {}", email);
            throw new IllegalArgumentException("Mã OTP không hợp lệ!");
        }

        if (!account.getOtp().equals(otp)) {
            logger.warn("Invalid OTP for email: {}", email);
            throw new IllegalArgumentException("Mã OTP không đúng!");
        }

        if (LocalDateTime.now().isAfter(account.getOtpExpiry())) {
            logger.warn("OTP expired for email: {}", email);
            throw new IllegalArgumentException("Mã OTP đã hết hạn!");
        }

        // Kích hoạt tài khoản
        account.setActive(true);
        account.setOtp(null);
        account.setOtpExpiry(null);
        accountRepository.save(account);
        logger.info("OTP verified, account activated for email: {}", email);
        return true;
    }

    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Mã xác thực tài khoản MusicManager");
        message.setText("Mã OTP của bạn là: " + otp
                + "\nVui lòng nhập mã này để xác thực tài khoản. Mã có hiệu lực trong 10 phút.");
        try {
            mailSender.send(message);
            logger.info("OTP email sent to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send OTP email to: {}", to, e);
            throw new RuntimeException("Lỗi khi gửi email OTP", e);
        }
    }

    @Override
    public List<Account> getAllEmployees() {
        logger.info("Fetching all musicians");
        return accountRepository.findByRole(Account.Role.MUSICIAN);
    }

    @Override
    public Account getEmployeeById(Integer id) {
        logger.info("Fetching employee with ID: {}", id);
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nhân viên không tồn tại!"));
    }

    @Override
    public Account createEmployee(Account account) {
        logger.info("Creating new employee: {}", account.getEmail());

        if (accountRepository.findByEmail(account.getEmail()).isPresent()) {
            logger.warn("Email already exists: {}", account.getEmail());
            throw new IllegalArgumentException("Email đã được sử dụng!");
        }

        if (account.getPassword() == null || !PASSWORD_PATTERN.matcher(account.getPassword()).matches()) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự gồm chữ hoa, số và ký tự đặc biệt!");
        }
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setActive(true);
        account.setRole(Role.MUSICIAN);
        account.setCreatedAt(LocalDateTime.now());

        try {
            Account savedAccount = accountRepository.save(account);
            logger.info("Employee created successfully: {}", account.getEmail());
            return savedAccount;
        } catch (Exception e) {
            logger.error("Error creating employee: {}", account.getEmail(), e);
            throw new RuntimeException("Lỗi khi tạo nhân viên", e);
        }
    }

    @Override
    public void deleteEmployee(Integer id) {
        logger.info("Deleting employee with ID: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nhân viên không tồn tại!"));
        try {
            accountRepository.delete(account);
            logger.info("Employee deleted successfully: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting employee: {}", id, e);
            throw new RuntimeException("Lỗi khi xóa nhân viên", e);
        }
    }

    public Account updateEmployee(Integer id, Account accountDetails) {
        Account existingEmployee = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        // Update only the fields provided
        if (accountDetails.getActive() != null) {
            existingEmployee.setActive(accountDetails.getActive());
        }
        // Add other fields (e.g., fullname, email, avatar) if needed
        return accountRepository.save(existingEmployee);
    }

    public Account updateProfile(Integer id, Account profileDetails) {
        Account existingUser = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Update only provided fields
        if (profileDetails.getFullname() != null) {
            existingUser.setFullname(profileDetails.getFullname());
        }
        if (profileDetails.getEmail() != null) {
            existingUser.setEmail(profileDetails.getEmail());
        }
        if (profileDetails.getBio() != null) {
            existingUser.setBio(profileDetails.getBio());
        }
        if (profileDetails.getAvatar() != null) {
            existingUser.setAvatar(profileDetails.getAvatar());
        }
        return accountRepository.save(existingUser);
    }

    public void changePassword(String currentPassword, String newPassword) {
        Account currentUser = getCurrentUser();

        // Validate current password
        if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng!");
        }

        // Validate new password
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 6 ký tự!");
        }
        if (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$")) {
            throw new IllegalArgumentException(
                    "Mật khẩu mới phải có ít nhất 6 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt!");
        }

        // Encode and save new password
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(currentUser);
    }
}