package com.musical.musican.Controller.MUSICAN;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/musican")
public class PlaylistsController {
    @GetMapping("/playlists")
    public String getMethodName() {
        return "Musican/playlist";
    }

}
