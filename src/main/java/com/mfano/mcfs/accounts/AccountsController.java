package com.mfano.mcfs.accounts;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mfano.mcfs.auth.services.ProfileService;
import com.mfano.mcfs.auth.services.UserService;
import com.mfano.mcfs.config.CustomUserDetails;

@Controller
@RequestMapping("/accounts")
public class AccountsController {
    private final UserService userService;
    private final ProfileService profileService;

    public AccountsController(UserService userService, ProfileService profileService) {
        this.userService = userService;
        this.profileService = profileService;
    }

    @GetMapping("/{option}")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable String option, Model red) {
        red.addAttribute("profile", profileService.checkProfile(auth.getId()));
        red.addAttribute("user", userService.findById(auth.getId()));

        String dir = "redirect";
        switch (option) {
            case "dashboard":
                dir = "accounts/index";

                break;

        }
        return dir;
    }

    @GetMapping("/landing")
    public String home() {
        return "index";
    }
}
