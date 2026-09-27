package com.mfano.mcfs.auth.controllers;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mcfs.config.CustomUserDetails;
import com.mfano.mcfs.dtos.UserDto;
import com.mfano.mcfs.auth.models.User;
import com.mfano.mcfs.auth.models.Profile;
import com.mfano.mcfs.auth.services.AuditService;
import com.mfano.mcfs.auth.services.RoleService;
import com.mfano.mcfs.auth.services.UserService;
import com.mfano.mcfs.auth.services.ProfileService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final RoleService roleService;
    private final ProfileService profileService;

    private final PasswordEncoder passwordEncoder;
    private String msg = "security/message";
    private final String login = "redirect:/login?error";

    private final AuditService auditService;

    // guest user
    @GetMapping("/")
    public String redirectAfterLogin(@AuthenticationPrincipal CustomUserDetails auth, RedirectAttributes model) {
        if (auth == null) {
            return "redirect:/login";
        }

        // Extract roles
        Set<String> roles = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // If user is not assigned any role
        if (roles.isEmpty()) {
            model.addFlashAttribute("error", "Please contact the system admin for mapping.");
            return login;
        } else if (!auth.isEnabled()) {
            model.addFlashAttribute("error", "Contact the system admin for account verification.");
            return login;
        }

        // Redirect based on role priority
        if (roles.contains("ROLE_ADMIN"))
        {
            return "redirect:/admin/dashboard";

        } else if (roles.contains("ROLE_CEO")) {
            return "redirect:/executive/dashboard";

        } else if (roles.contains("ROLE_BDM")) {
            return "redirect:/executive/dashboard";

        }else if (roles.contains("ROLE_HRO")) {
            return "redirect:/hr/dashboard";

        } else if (roles.contains("ROLE_ICT")) {
            return "redirect:/ict/dashboard";

        } else if (roles.contains("ROLE_PMO")) {
            return "redirect:/procurement/dashboard";

        } else if (roles.contains("ROLE_ACO")) {
            return "redirect:/accounts/dashboard";

        } else if (roles.contains("ROLE_RMO")) {
            return "redirect:/records/dashboard";

        }else {
            model.addFlashAttribute("error", "Please contact the system admin for role mapping.");
            return login;
        }

    }

    @GetMapping("/error/403")
    public String forbidden() {
        return "error/403";
    }

    @GetMapping("/{option}")
    public String getAll(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable String option, 
        Model model, RedirectAttributes red) {

        String dir = "redirect";
        switch (option) {
            //Get register page
            case "register":
                model.addAttribute("roles", roleService.findAll());
                model.addAttribute("userDto", new UserDto());
                dir = "security/register";
                break;

            //Get login page
            case "login":
                // If user is already logged in → redirect to dashboard
                if (auth != null) {
                    return "redirect:/";
                }

                dir = "security/login"; // Return login view
                break;

            //Get logout
            case "logout": 
                if (auth != null) {
                    red.addFlashAttribute("message", "You have been logged out successfully");
                    dir = "redirect:/login?logout";
                } else{
                    red.addFlashAttribute("error", "Logout action failed. Please try again.");
                    dir = "redirect:/";
                }
                break;

            //Get resend
            case "resend":
                dir = "security/resend";
                break;

            //Get forgot page
            case "forgot":
                dir = "security/forgot-password";
                break;

            //Get profile page
            //@PreAuthorize("isAuthenticated()")
            case "profile":
                if (auth == null) {
                    model.addAttribute("error", "User not authenticated, login to proceed.");
                    return login;
                } else{
                    model.addAttribute("profile", profileService.checkProfile(auth.getId()));
                    model.addAttribute("user", userService.findById(auth.getId()));
                    // Add user info to model (for Thymeleaf dashboard pages)
                    model.addAttribute("user", userService.findById(auth.getId()));
                    dir = "security/profile";
                }
                break;

            //Get profile page
            //@PreAuthorize("isAuthenticated()")
            case "audits":
                if (auth == null) {
                    model.addAttribute("error", "User not authenticated, login to proceed.");
                    return login;
                } else {
                    model.addAttribute("profile", profileService.checkProfile(auth.getId()));
                    model.addAttribute("user", userService.findById(auth.getId()));
                    model.addAttribute("auditEntries", auditService.findAll());
                    dir = "accounts/audits";
                }
                break;
        }

        return dir;
    }

    // profile/update @PreAuthorize("isAuthenticated()")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/profile/update")
    public String userProfileUpdate(@AuthenticationPrincipal CustomUserDetails auth,
            @ModelAttribute Profile profile) {

        profileService.update(auth.getId(), profile);
        auditService.record("UPDATE_PROFILE", "SUCCESS", "User " + auth.getEmail() + " updated their profile");
        return "redirect:/profile";
    }

    // update user image
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/image/update/{userid}")
    public String imageUpdate(@PathVariable Long userid, @RequestParam("image") MultipartFile file,
            RedirectAttributes red) {
        try {
            profileService.updateProfileImage(userid, file, red);
        } catch (IOException e) {
            red.addFlashAttribute("error", e.getMessage());
        }
        auditService.record("UPDATE_IMAGE", "SUCCESS", "User Updated their profile image");
        red.addFlashAttribute("message", "Image updated successfully.");
        return "redirect:/profile";
    }

    // delete user image
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/image/delete/{userid}")
    public String imageDelete(@PathVariable Long userid, RedirectAttributes red) {
        try {
            profileService.deleteProfileImage(userid, red);
        } catch (IOException e) {
            red.addFlashAttribute("error", e.getMessage());
        }
        auditService.record("DELETE_IMAGE", "SUCCESS", "User deleted their profile image");
        red.addFlashAttribute("message", "Image deleted successfully.");
        return "redirect:/profile";
    }

    @GetMapping("/verify")
    public String verify(@RequestParam("token") String token, Model model) {
        String result = userService.validateVerificationToken(token);
        if ("valid".equals(result)) {
            model.addAttribute("message", "Email verified! You can now login.");
            return msg;
        } else if ("expired".equals(result)) {
            model.addAttribute("error", "Token expired. Please register again.");
            return msg;
        } else {
            model.addAttribute("error", "Invalid token.");
            return msg;
        }
    }

    @PostMapping("/resend")
    public String resendSubmit(@RequestParam("email") String email, RedirectAttributes model) {
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addFlashAttribute("error", "No account with that email.");
            return "redirect:/resend";
        }

        if (user.isEnabled()) {
            model.addFlashAttribute("message", "Email already verified. You can login.");
            return "redirect:/login";
        }
        userService.createAndSendToken(user);
        model.addFlashAttribute("message", "Verification email resent. Check your inbox.");
        return "redirect:/login";
    }

    @PostMapping("/forget")
    public String forgotSubmit(@RequestParam String email, RedirectAttributes model) {
        if (userService.findByEmail(email) == null) {
            model.addFlashAttribute("error", "No account matches the email address.");
            return "redirect:/forgot";
        }

        try {
            userService.createPasswordResetToken(email);
            model.addFlashAttribute("message", "Check your email, a reset link was sent.");
        } catch (Exception e) {
            model.addFlashAttribute("error", "Something went wrong, please try again.");
            return "redirect:/forgot";
        }
        return "redirect:/login";
    }

    // self-serve password email change
    @GetMapping("/password-reset")
    public String resetPasswordForm(@RequestParam("token") String token, Model model) {
        String res = userService.validatePasswordResetToken(token);
        if ("valid".equals(res)) {
            model.addAttribute("token", token);
            return "security/reset-password";
        } else if ("expired".equals(res)) {
            model.addAttribute("error", "Token expired.");
            return msg;
        } else {
            model.addAttribute("error", "Invalid token.");
            return msg;
        }
    }

    // self-serve password change request
    @PostMapping("/reset-password")
    public String resetPasswordSubmit(@RequestParam String token, @RequestParam String password, RedirectAttributes model) {
        var optUser = userService.getUserByPasswordResetToken(token);
        if (optUser.isEmpty()) {
            model.addFlashAttribute("error", "Invalid token.");
            return "redirect:/password-reset";
        }
        userService.changePassword(optUser.get(), password);
        model.addFlashAttribute("message", "Password changed. You can now login.");
        return "redirect:/login";
    }

    // logged user change password
    // Reset user password
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/reset")
    public String resetPassword(@AuthenticationPrincipal CustomUserDetails auth, @RequestParam String password, @RequestParam String NP,
            RedirectAttributes red) {
        User user = userService.findById(auth.getId());
        if (!NP.equals(password) || NP.isEmpty() || password.isEmpty()) {
            red.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/profile";
        }

        if (user != null) {
            user.setPassword(passwordEncoder.encode(password));
            userService.save(user);
            auditService.record("RESET_PASSWORD", "SUCCESS", "user " + auth.getEmail() + " Reset their password");
            red.addFlashAttribute("message", "Password reset successful");
            return "redirect:/profile";
        } else {
            red.addFlashAttribute("error", "Failed to reset password");
            return "redirect:/profile";
        }
    }
}
