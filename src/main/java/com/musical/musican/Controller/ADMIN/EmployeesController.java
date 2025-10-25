package com.musical.musican.Controller.ADMIN;

import com.musical.musican.Model.Entity.Account;
import com.musical.musican.Service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/employees")
@PreAuthorize("hasAuthority('ADMIN')")
public class EmployeesController {

    @Autowired
    private AccountService accountService;
    @GetMapping
    public String getEmployeePage(Model model) {
        List<Account> employees = accountService.getAllEmployees();
        model.addAttribute("employees", employees);
        return "Admin/employee";
    }

    @PostMapping("/add")
    public String addEmployee(
            @RequestParam("username") String username,
            @RequestParam("fullname") String fullname,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu xác nhận không khớp!");
            return "redirect:/admin/employees";
        }

        try {
            Account account = new Account();
            account.setUsername(username);
            account.setFullname(fullname);
            account.setEmail(email);
            account.setPassword(password);
            account.setActive(true);
            account.setRole(Account.Role.MUSICIAN);
            account.setCreatedAt(LocalDateTime.now());

            accountService.createEmployee(account);
            redirectAttributes.addFlashAttribute("message", "Thêm nhân viên thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm nhân viên: " + e.getMessage());
        }

        return "redirect:/admin/employees";
    }

    @PostMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            accountService.deleteEmployee(id);
            redirectAttributes.addFlashAttribute("message", "Xóa nhân viên thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa nhân viên này.");
        }
        return "redirect:/admin/employees";
    }

    @GetMapping("/view/{id}")
    public String viewEmployee(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Account employee = accountService.getEmployeeById(id);
            redirectAttributes.addFlashAttribute("viewEmployee", employee);
            redirectAttributes.addFlashAttribute("showViewModal", true);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy nhân viên.");
        }
        return "redirect:/admin/employees";
    }

    @PostMapping("/update-status/{id}")
    public String updateEmployeeStatus(@PathVariable Integer id,
            @RequestParam("active") boolean active,
            RedirectAttributes redirectAttributes) {
        try {
            Account employee = accountService.getEmployeeById(id);
            employee.setActive(active);
            accountService.updateEmployee(id, employee);
            redirectAttributes.addFlashAttribute("message", "Cập nhật trạng thái thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể cập nhật trạng thái.");
        }
        return "redirect:/admin/employees";
    }

}
