package com.musical.musican.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/favorite")
public class FavouriteController {

    @RequestMapping
    public String favoritePage() {
        return "Users/favourite";
    }
}
