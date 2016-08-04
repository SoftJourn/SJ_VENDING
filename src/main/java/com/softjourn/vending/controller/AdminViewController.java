package com.softjourn.vending.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminViewController {

    static final String STATIC_FOLDER_PREFIX = "../static/";

    @RequestMapping("/admin")
    public String index(@RequestParam("access_token") final String token, Model model) {
        model.addAttribute("token", token);
        return STATIC_FOLDER_PREFIX + "admin";
    }
}
