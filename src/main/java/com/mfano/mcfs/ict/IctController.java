package com.mfano.mcfs.ict;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ict")
public class IctController {
    @GetMapping("/dashboard")
    public String dashboard() {
        return "ict/index";
    }

}
