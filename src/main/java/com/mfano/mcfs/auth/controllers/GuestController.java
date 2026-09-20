package com.mfano.mcfs.auth.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
//@RequestMapping("/guest")
public class GuestController {
    @GetMapping("/dashboard")
    public String dashboard() {
        return "index";
    }

    @GetMapping("/landing")
    public String home() {
        return "index";
    }
}
