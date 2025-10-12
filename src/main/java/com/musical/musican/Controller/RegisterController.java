package com.musical.musican.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/Dangky")
public class RegisterController {
    @GetMapping
    public String register() {
        return "Home/register";
    }
}
