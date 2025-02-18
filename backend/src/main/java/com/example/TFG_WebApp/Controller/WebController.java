package com.example.TFG_WebApp.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    @RequestMapping(value = {"/ranking", "/miembros", "/eventos", "/disciplines", "/calendario", "/discipline-details/*", "/profile/*", "/events/new"})
    public String redirectToIndex() {
        return "forward:/index.html";
    }
}
