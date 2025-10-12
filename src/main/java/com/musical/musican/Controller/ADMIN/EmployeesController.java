package com.musical.musican.Controller.ADMIN;

import com.musical.musican.Model.Entity.Account;
import com.musical.musican.Service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    // REST API endpoints (unchanged, can be kept for other uses if needed)
    @GetMapping("/api")
    public ResponseEntity<List<Account>> getAllEmployees() {
        List<Account> employees = accountService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/api/{id}")
    public ResponseEntity<Account> getEmployeeById(@PathVariable Integer id) {
        try {
            Account employee = accountService.getEmployeeById(id);
            return ResponseEntity.ok(employee);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/api")
    public ResponseEntity<Account> createEmployee(@RequestBody Account account) {
        try {
            Account createdEmployee = accountService.createEmployee(account);
            return ResponseEntity.ok(createdEmployee);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/api/{id}")
    public ResponseEntity<Account> updateEmployee(@PathVariable Integer id, @RequestBody Account accountDetails) {
        try {
            Account updatedEmployee = accountService.updateEmployee(id, accountDetails);
            return ResponseEntity.ok(updatedEmployee);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/api/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Integer id) {
        try {
            accountService.deleteEmployee(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}