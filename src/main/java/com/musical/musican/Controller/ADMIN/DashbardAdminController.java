package com.musical.musican.Controller.ADMIN;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class DashbardAdminController {
    @GetMapping("/dashboard")
    public String getMethodName() {
        return "Admin/dashboardAdmin";
    }

}
