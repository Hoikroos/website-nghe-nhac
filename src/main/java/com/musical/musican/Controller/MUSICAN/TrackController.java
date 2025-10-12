package com.musical.musican.Controller.MUSICAN;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/musican")
public class TrackController {
    @GetMapping("/tracks")
    public String getMethodName() {
        return "Musican/tracks";
    }

}
