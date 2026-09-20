package com.mfano.mcfs.controllers;

import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mcfs.config.CustomUserDetails;
import com.mfano.mcfs.dtos.UserDto;
import com.mfano.mcfs.auth.models.Role;
import com.mfano.mcfs.auth.services.BranchService;
import com.mfano.mcfs.auth.services.AuditService;
import com.mfano.mcfs.auth.services.RoleService;
import com.mfano.mcfs.auth.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    // private final PostService postService;
    //private final ProfileService profileService;
    private final AuditService auditService;
    private final RoleService roleService;
    private final BranchService storeService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails auth, RedirectAttributes red) {
        red.addAttribute("users", userService.findAll());
        red.addAttribute("stores", storeService.findAll());
        red.addAttribute("roles", roleService.findAll());
        red.addAttribute("audits", auditService.findAll());
        //red.addFlashAttribute("profile", profileService.checkProfile(auth.getId()));
        return "admin/index";
    }

    @GetMapping("/stores")
    public String stores(@AuthenticationPrincipal CustomUserDetails auth, RedirectAttributes red) {
        //red.addFlashAttribute("profile", profileService.checkProfile(auth.getId()));
        red.addAttribute("stores", storeService.findAll());
        return "admin/stores";
    }

    @GetMapping("/users")
    public String users(@AuthenticationPrincipal CustomUserDetails auth,
            Model red) {
        //red.addAttribute("profile", profileService.checkProfile(auth.getId()));
        red.addAttribute("userDto", new UserDto());
        Long storeId = auth.getBranch().getId();

        if (storeId != null) {
            // red.addAttribute("users", userService.findByBranch_Id(storeId));
            red.addAttribute("users", userService.findAll());

        } else {
            red.addAttribute("users", userService.findAll());
        }
        red.addAttribute("stores", storeService.findAll());
        red.addAttribute("roles", roleService.findAll());
        return "admin/users";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute UserDto userDto, RedirectAttributes red) {
        try {
            userService.registerUser(userDto);
            auditService.record("CREATE_USER", "admin Created user: " + userDto.getEmail());
            red.addFlashAttribute("message", "User created successfully!");
        } catch (Exception e) {
            red.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }


    @PostMapping("/users/update/{id}")
    public String update(
            @PathVariable Long id,
            @Valid UserDto userDto,
            RedirectAttributes redirectAttributes){

        userService.update(id, userDto);

        redirectAttributes.addFlashAttribute(
                "message",
                "User updated successfully."
        );

        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        userService.deleteById(id);

        redirectAttributes.addFlashAttribute(
                "message",
                "User deleted successfully."
        );

        return "redirect:/admin/users";
    }

    @PostMapping("/users/toggle/{id}")
    public String toggle(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        userService.toggleActive(id);

        redirectAttributes.addFlashAttribute(
                "message",
                "User status updated."
        );

        return "redirect:/admin/users";
    }
}