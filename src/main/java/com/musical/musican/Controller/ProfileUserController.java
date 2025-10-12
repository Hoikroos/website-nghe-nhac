package com.musical.musican.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
public class ProfileUserController {
    @GetMapping
    public String getMethodName() {
        return "Users/profile";
    }
}
