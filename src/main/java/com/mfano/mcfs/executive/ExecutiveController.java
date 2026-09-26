package com.mfano.mcfs.executive;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/executive")
public class ExecutiveController {
    @GetMapping("/dashboard")
    public String dashboard() {
        return "executive/index";
    }

}
