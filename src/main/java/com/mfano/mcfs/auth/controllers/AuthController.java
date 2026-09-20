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
import com.mfano.mcfs.auth.services.AuditService;
import com.mfano.mcfs.auth.services.RoleService;
import com.mfano.mcfs.auth.services.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;
    private String msg = "security/message";
    private final String login = "redirect:/login?error";

    private final AuditService auditService;

    // guest user
    @GetMapping("/")
    public String redirectAfterLogin(@AuthenticationPrincipal CustomUserDetails auth, RedirectAttributes model) {

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
        //model.addFlashAttribute("profile", profileService.checkProfile(auth.getId()));
        auditService.record(
                "user_login",
                "User " + auth.getUsername() + " logged in successfully.");

        // Redirect based on role priority
        if (roles.contains("ROLE_ADMIN"))
        {
            return "redirect:/admin/dashboard";
        } else if (roles.contains("ROLE_MANAGER")) {
            return "redirect:/manager/dashboard";
        } else if (roles.contains("ROLE_CASHIER")) {
            return "redirect:/cashier/dashboard";
        } else if (roles.contains("ROLE_PROCUREMENT")) {
            return "redirect:/procurement/dashboard";
        } else if (roles.contains("ROLE_USER")) {
            return "redirect:/guest/dashboard";
        }else {
            model.addFlashAttribute("error", "Please contact the system admin for role mapping.");
            return login;
        }

    }

    @GetMapping("/register")
    public String registerForm(Model model) {

        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("userDto", new UserDto());
        return "security/register";
    }

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            RedirectAttributes model,
            Authentication authentication) {

        // If user is already logged in → redirect to dashboard
        if (authentication != null && authentication.isAuthenticated()
                && authentication instanceof CustomUserDetails) {
            return "redirect:/forward";
        }

        // Logout confirmation
        if (logout != null) {
            model.addFlashAttribute("message", "You have been logged out.");
        }

        return "security/login"; // Return login view
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String userProfile(@AuthenticationPrincipal CustomUserDetails auth, Model model) {
        if (auth == null) {
            model.addAttribute("error", "User not authenticated, login to proceed.");
            return login;
        }
        //model.addAttribute("profile", profileService.checkProfile(auth.getId()));
        // Add user info to model (for Thymeleaf dashboard pages)
        model.addAttribute("user", userService.findById(auth.getId()));

        return "security/profile";
    }

    @GetMapping("/logout")
    public String logout(RedirectAttributes model) {
        model.addFlashAttribute("message", "You have been logged out successfully");
        return "redirect:/login?logout";
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

    @GetMapping("/resend")
    public String resendForm() {
        return "security/resend";
    }

    @PostMapping("/resend")
    public String resendSubmit(@RequestParam("email") String email, Model model) {
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("error", "No account with that email.");
            return "security/resend";
        }

        if (user.isEnabled()) {
            model.addAttribute("message", "Email already verified. You can login.");
            return msg;
        }
        userService.createAndSendToken(user);
        model.addAttribute("message", "Verification email resent. Check your inbox.");
        return msg;
    }

    // Forgot/reset endpoints
    @GetMapping("/forgot")
    public String forgotForm() {
        return "security/forgot";
    }

    @PostMapping("/forgot")
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
    public String resetPasswordSubmit(@RequestParam String token, @RequestParam String password, Model model) {
        var optUser = userService.getUserByPasswordResetToken(token);
        if (optUser.isEmpty()) {
            model.addAttribute("error", "Invalid token.");
            return msg;
        }
        userService.changePassword(optUser.get(), password);
        model.addAttribute("message", "Password changed. You can now login.");
        return msg;
    }

    // logged user change password
    // Reset user password
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/reset")
    public String resetPassword(Authentication auth, @RequestParam String password, @RequestParam String NP,
            RedirectAttributes red) {
        CustomUserDetails u = (CustomUserDetails) auth.getPrincipal();
        User user = userService.findById(u.getId());
        if (!NP.equals(password) || NP.isEmpty() || password.isEmpty()) {
            red.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/profile";
        }

        if (user != null) {
            user.setPassword(passwordEncoder.encode(password));
            userService.save(user);
            auditService.record("reset_password", "user id=" + user.getId() + "Reset password");
            red.addFlashAttribute("message", "Password reset successful");
            return "redirect:/profile";
        } else {
            red.addFlashAttribute("error", "Failed to reset password");
            return "redirect:/profile";
        }
    }
}
