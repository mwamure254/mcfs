package com.mfano.mcfs.accounts;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/accounts")
public class AccountsController {
    @GetMapping("/dashboard")
    public String dashboard() {
        return "accounts/index";
    }

    @GetMapping("/landing")
    public String home() {
        return "index";
    }
}
