package com.musical.musican.Controller.MUSICAN;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/musican")
public class ArtistsController {
    @RequestMapping("/artists")
    public String getMethodName() {
        return "Musican/artists";
    }
}
