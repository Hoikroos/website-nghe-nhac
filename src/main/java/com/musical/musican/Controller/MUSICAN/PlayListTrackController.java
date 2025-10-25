package com.musical.musican.Controller.MUSICAN;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/musican/playlist-tracks")
public class PlayListTrackController {
    @GetMapping
    public String getMethodName() {
        return "Musican/playlisttrack";
    }
    
}
