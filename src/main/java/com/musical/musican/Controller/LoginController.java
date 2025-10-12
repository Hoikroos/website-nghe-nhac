package com.musical.musican.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Dangnhap")
public class LoginController {
@GetMapping
    public String login() {
        return "Home/login";
    }

}
