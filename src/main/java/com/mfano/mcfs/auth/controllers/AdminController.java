package com.mfano.mcfs.controllers;

import java.io.IOException;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
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
import com.mfano.mcfs.auth.models.Profile;
import com.mfano.mcfs.auth.models.User;
import com.mfano.mcfs.auth.services.BranchService;
import com.mfano.mcfs.auth.services.AuditService;
import com.mfano.mcfs.auth.services.RoleService;
import com.mfano.mcfs.auth.services.UserService;
import com.mfano.mcfs.auth.services.ProfileService;

import com.mfano.mcfs.utils.documents.services.DocumentTypeService;
import com.mfano.mcfs.utils.documents.services.DocumentClassService;
import com.mfano.mcfs.utils.documents.services.DocumentStatusService;
import com.mfano.mcfs.utils.documents.models.DocumentType;
import com.mfano.mcfs.utils.documents.models.DocumentClass;
import com.mfano.mcfs.utils.documents.models.DocumentStatus;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    // private final PostService postService;
    private final ProfileService profileService;
    private final AuditService auditService;
    private final RoleService roleService;
    private final BranchService storeService;
    private final DocumentTypeService typeService;
    private final DocumentClassService classService;
    private final DocumentStatusService statusService;

     // manage /GET/*  module
    @GetMapping("/{option}")
    public String getAll(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable String option, Model red) {
        red.addAttribute("profile", profileService.checkProfile(auth.getId()));

        String dir = "redirect";
        switch (option) {
            case "dashboard":
                red.addAttribute("users", userService.findAll());
                red.addAttribute("stores", storeService.findAll());
                red.addAttribute("roles", roleService.findAll());
                red.addAttribute("audits", auditService.findAll());
                dir = "admin/index";
                break;

            case "users":
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
                dir = "admin/users";
                break;

            case "branches":
                red.addAttribute("users", userService.findAll());
                red.addAttribute("branches", storeService.findAll());
                dir = "admin/branches";
                break;

            case "roles":
                red.addAttribute("roles", roleService.findAll());
                red.addAttribute("stores", storeService.findAll());
                dir = "admin/roles";
                break;

            case "types":
                red.addAttribute("types", typeService.findAll());
                dir = "admin/types";
                break;  

            case "classes":
                red.addAttribute("classes", classService.findAll());
                dir = "admin/classes";
                break;  

            case "statuses":
                red.addAttribute("statuses", statusService.findAll());
                dir = "admin/statuses";
                break;      
        }

        return dir;
    }

    //update user
    @PostMapping("/users/update/{id}")
    public String updateUser(@AuthenticationPrincipal CustomUserDetails auth,
            @PathVariable("id") Long id,
            @Valid @ModelAttribute UserDto userDto,
            RedirectAttributes redirectAttributes){

        userService.update(id, userDto);
        auditService.record("UPDATE_USER", auth.getEmail() + " Updated user: " + userDto.getEmail());
        redirectAttributes.addFlashAttribute(
                "message",
                "User updated successfully."
        );

        return "redirect:/admin/manage/user/{id}";
    }
    //Role update
    @PostMapping("/roles/update/{id}")
    public String updateRole(
            @PathVariable Long id,
            @Valid Role role,
            RedirectAttributes redirectAttributes){

        roleService.update(id, role);

        redirectAttributes.addFlashAttribute(
                "message",
                "Role updated successfully."
        );

        return "redirect:/admin/roles";
    }
    //Type update
    @PostMapping("/types/update/{id}")
    public String updateType(
            @PathVariable Long id,
            @Valid DocumentType dote,
            RedirectAttributes redirectAttributes){

        typeService.update(id, dote);

        redirectAttributes.addFlashAttribute(
                "message",
                "Type updated successfully."
        );

        return "redirect:/admin/types";
    }
    //Status update
    @PostMapping("/statuses/update/{id}")
    public String updateRole(
            @PathVariable Long id,
            @Valid DocumentStatus dosa,
            RedirectAttributes redirectAttributes){

        statusService.update(id, dosa);

        redirectAttributes.addFlashAttribute(
                "message",
                "Status updated successfully."
        );

        return "redirect:/admin/statuses";
    }
    //Class update
    @PostMapping("/classes/update/{id}")
    public String updateRole(
            @PathVariable Long id,
            @Valid DocumentClass doca,
            RedirectAttributes redirectAttributes){

        classService.update(id, doca);

        redirectAttributes.addFlashAttribute(
                "message",
                "Class updated successfully."
        );

        return "redirect:/admin/classes";
    }

     // manage /user route
    @PostMapping("/users/{option}/{id}")
    public String manageGetter(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable String option, @PathVariable Long id, 
        Model model, RedirectAttributes red) {

        User user = userService.findById(id);
        model.addAttribute("user", user);

        String dir = "redirect";
        switch (option) {

            case "profile":
                // Add user info to model (for Thymeleaf dashboard pages)
                model.addAttribute("profile", profileService.checkProfile(auth.getId()));
                model.addAttribute("profile1", profileService.checkProfile(user.getId()));
                dir = "admin/user-profile";
                break;

            // Resend verification link
            case "resend":
                if (user != null && !user.isEnabled()) {
                    userService.createAndSendToken(user);
                    auditService.record("RESEND_VERIFICATION", auth.getEmail() + " Resent token to user id=" + id);
                    red.addFlashAttribute("message", "Link resent to user");
                    dir = "redirect:/admin/users";
                }
                else{
                    red.addFlashAttribute("error", "Sorry! Failed to send link.");
                    dir = "redirect:/admin/users";
                }
                break;
        }

        return dir;
    }

    // profile/update @PreAuthorize("isAuthenticated()")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/profile/update/{userid}")
    public String userProfileUpdate(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable Long userid,
            @ModelAttribute("profile") Profile profile, RedirectAttributes red) {
        red.addAttribute("profile", profileService.checkProfile(auth.getId()));

        profileService.update(userid, profile);
        auditService.record("UPDATE_PROFILE", auth.getEmail() + " Updated the profile of user id=" + userid);
        red.addFlashAttribute("message", "Profile updated successfully");
        return "redirect:/admin/profile/{userid}";
    }

    // Update profile image
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/image/update/{userid}")
    public String imageUpdate(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable Long userid,
            @RequestParam("image") MultipartFile file, RedirectAttributes red) {
        red.addAttribute("profile", profileService.checkProfile(auth.getId()));
        try {
            profileService.updateProfileImage(userid, file, red);
            auditService.record("UPDATE_IMAGE", auth.getEmail() + " Updated the profile image  of user id=" + userid);
            red.addFlashAttribute("message", "Image updated successfully");
        } catch (IOException e) {
            red.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/profile/{userid}";
    }

    // Delete profile image
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/image/delete/{userid}")
    public String imageDelete(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable Long userid, RedirectAttributes red) {
        
        red.addAttribute("profile", profileService.checkProfile(auth.getId()));
        try {
            profileService.deleteProfileImage(userid, red);
            auditService.record("DELETE_IMAGE", auth.getEmail() + " Deleted the profile image of user id=" + userid);
            red.addFlashAttribute("message", "Image deleted successfully");
        } catch (IOException e) {
            red.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/profile/{userid}";
    }

    // Assign Remove roles
    @PostMapping("/{option}/{userId}/{roleId}")
    public String userRole(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable Long userId, @PathVariable String option,
            @PathVariable Long roleId, RedirectAttributes red) {
        red.addAttribute("profile", profileService.checkProfile(auth.getId()));

        switch (option) {
            case "assign":
                try {
                    User user = userService.findById(userId);
                    Role role = roleService.findById(roleId);

                    if (user == null) {
                        throw new RuntimeException("User not found.");
                    }
                    if (role == null) {
                       throw new RuntimeException("Role not found.");
                    }

                    // Prevent duplicate role assignment
                    if (user.getRoles().contains(role)) {
                        red.addFlashAttribute(
                            "error",
                            "Role is already assigned to this user."
                        );
                    }

                    userService.assignRoleToUser(userId, roleId);
                    auditService.record("ASSIGN_ROLE", auth.getEmail() + " Assigned user id=" + userId + " role id=" + roleId);

                    red.addFlashAttribute(
                        "success",
                        "Role '" + role.getName() + "' assigned successfully."
                    );

                } catch (RuntimeException e) {
                    red.addFlashAttribute("error", e.getMessage());
                } catch (Exception e) {
                    red.addFlashAttribute(
                        "error",
                        "Unable to assign role."
                    );
                }
                break;

            case "remove":
                userService.removeRoleFromUser(userId, roleId);
                auditService.record("REMOVE_ROLE", auth.getEmail() + " Revoked role id=" + roleId + " from user id=" + userId);
                red.addFlashAttribute("message", "Action successful");
                break;

            default:
                red.addFlashAttribute("error", "Action failed");
                break;
        }

        return "redirect:/admin/manage/user/{userId}";
    }

    //Role saving
    @PostMapping("/roles/save")
    public String saveRole(@AuthenticationPrincipal CustomUserDetails auth, @ModelAttribute Role role, RedirectAttributes red) {
        try {
            roleService.save(role);
            auditService.record("CREATE_ROLE", auth.getEmail() + " Created role: " + role.getName());
            red.addFlashAttribute("message", "Role created successfully!");
        } catch (Exception e) {
            red.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/roles";
    }
    //save user
    @PostMapping("/users/save")
    public String saveUser(@AuthenticationPrincipal CustomUserDetails auth, 
        @ModelAttribute UserDto userDto, RedirectAttributes red) {
        try {
            userService.registerUser(userDto);
            auditService.record("CREATE_USER", auth.getEmail() + " Created user: " + userDto.getEmail());
            red.addFlashAttribute("message", "User created successfully!");
        } catch (RuntimeException e) {
                    red.addFlashAttribute("error", e.getMessage());
                } catch (Exception e) {
                    red.addFlashAttribute(
                        "error",
                        "Unable to create user."
                    );
                }
        return "redirect:/admin/users";
    }
    //Classes, Types and Statuses
     @PostMapping("/types/save")
    public String saveType(@AuthenticationPrincipal CustomUserDetails auth, @ModelAttribute DocumentType dote, RedirectAttributes red) {
        try {
            typeService.save(dote);
            auditService.record("CREATE_TYPE", auth.getEmail() + " Created type: " + dote.getName());
            red.addFlashAttribute("message", "Type created successfully!");
        } catch (Exception e) {
            red.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/types";
    } 
    @PostMapping("/classes/save")
    public String saveClass(@AuthenticationPrincipal CustomUserDetails auth, @ModelAttribute DocumentClass doca, RedirectAttributes red) {
        try {
            classService.save(doca);
            auditService.record("CREATE_CLASS", auth.getEmail() + " Created class: " + doca.getName());
            red.addFlashAttribute("message", "Class created successfully!");
        } catch (Exception e) {
            red.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/classes";
    } 
    @PostMapping("/statuses/save")
    public String saveStatus(@AuthenticationPrincipal CustomUserDetails auth, @ModelAttribute DocumentStatus dosa, RedirectAttributes red) {
        try {
            statusService.save(dosa);
            auditService.record("CREATE_STATUS", auth.getEmail() + " Created status: " + dosa.getName());
            red.addFlashAttribute("message", "Status created successfully!");
        } catch (Exception e) {
            red.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/status";
    }

    //Manage All models
    @GetMapping("/manage/{option}/{id}")
    public String manageAll(@AuthenticationPrincipal CustomUserDetails auth,
         @PathVariable String option, @PathVariable Long id, Model model) {

        model.addAttribute("profile", profileService.checkProfile(auth.getId()));
        String dir = "redirect";
        switch (option) {

            // Get user
            case "user":
                User user = userService.findById(id);
                model.addAttribute("user", user);
                model.addAttribute("userDto", new UserDto());
                model.addAttribute("stores", storeService.findAll());
                model.addAttribute("userRoles", roleService.getUserRoles(user));
                model.addAttribute("userNotRoles", roleService.getUserNotRoles(user));
                dir = "admin/user-roles";
                break;

            // Get role
            case "role":
                Role role = roleService.findById(id);
                model.addAttribute("role", role);
                model.addAttribute("url", "roles");
                model.addAttribute("rame", "Role");
                dir = "admin/edits";
                break;

            // Get type
            case "type":
                DocumentType dote = typeService.findById(id);
                model.addAttribute("role", dote);
                model.addAttribute("url", "types");
                model.addAttribute("rame", "Type");
                dir = "admin/edits";
                break;

            // Get class
            case "class":
                DocumentClass doca = classService.findById(id);
                model.addAttribute("role", doca);
                model.addAttribute("url", "classes");
                model.addAttribute("rame", "Class");
                dir = "admin/edits";
                break;

            // Get status
            case "status":
                DocumentStatus dosa = statusService.findById(id);
                model.addAttribute("role", dosa);
                model.addAttribute("url", "statuses");
                model.addAttribute("rame", "Status");
                dir = "admin/edits";
                break;
                   
        }

        return dir;
    }

     // Toggle /classes/statuses/types/*  module **All
    @PostMapping("/{option}/toggle/{id}")
    public String toggleAll(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable String option, 
            @PathVariable Long id,
            RedirectAttributes red) {

        String dir = "redirect";
        switch (option) {

            // Toggle class
            case "classes":
                try {
                classService.toggleActive(id);
                auditService.record("TOGGLE_CLASS", auth.getEmail() + " Toggled class id=" + id);
                red.addFlashAttribute(
                    "message",
                    "Class toggled successfully."
                 );
                dir = "redirect:/admin/classes";
                    
                } catch (Exception e) {
                    red.addFlashAttribute("error", "Sorry! Failed to toggle status");
                    dir = "redirect:/admin/statuses";
                }
                break;

            // Toggle status
            case "statuses":
                try {
                statusService.toggleActive(id);
                auditService.record("TOGGLE_STATUS", auth.getEmail() + " Toggled status id=" + id);
                red.addFlashAttribute(
                    "message",
                    "Status toggled successfully."
                );
                dir = "redirect:/admin/statuses";
                    
                } catch (Exception e) {
                    red.addFlashAttribute("error", "Sorry! Failed to toggle status");
                    dir = "redirect:/admin/statuses";
                }
                break; 

            // Toggle type
            case "types":
                try {
                typeService.toggleActive(id);
                auditService.record("TOGGLE_TYPE", auth.getEmail() + " toggled type id=" + id);
                red.addFlashAttribute(
                    "message",
                    "Type toggled successfully."
                );
                dir = "redirect:/admin/types";
                    
                } catch (Exception e) {
                    red.addFlashAttribute("error", "Sorry! Failed to toggle type");
                    dir = "redirect:/admin/types";
                }
                break;

            // Toggle role
            case "roles":
                try {
                roleService.toggleActive(id);
                auditService.record("TOGGLE_ROLE", auth.getEmail() + " toggled role id=" + id);
                red.addFlashAttribute(
                    "message",
                    "Role toggled successfully."
                );
                dir = "redirect:/admin/roles";
                    
                } catch (Exception e) {
                    red.addFlashAttribute("error", "Sorry! Failed to toggle role");
                    dir = "redirect:/admin/roles";
                }
                break;

            // Toggle user
            case "users":
                User user = userService.findById(id);
                try {
                    userService.toggleActive(id);
                    auditService.record("TOGGLE_USER",auth.getEmail() + " Toggled user: " + user.getEmail() + " to enabled=" + user.isEnabled());
                    red.addFlashAttribute("message", "Toggled user successfully");
                    dir = "redirect:/admin/users";
                    
                } catch (Exception e) {
                    red.addFlashAttribute("error", "Sorry! Failed to toggle user");
                    dir = "redirect:/admin/users";
                }
                break;   
        }

        return dir;
    }

    // manage /user route
    @PostMapping("/{option}/delete/{id}")
    public String deleteAll(@AuthenticationPrincipal CustomUserDetails auth, 
        @PathVariable String option, @PathVariable Long id, RedirectAttributes red) {

        String dir = "redirect";
        switch (option) {
            //Delete role
            case "roles":
                try {
                roleService.delete(id);
                auditService.record("DELETE_ROLE", auth.getEmail() + " Deleted role id=" + id);
                red.addFlashAttribute(
                    "message",
                    "Role deleted successfully."
                 );
                dir = "redirect:/admin/roles";
                    
                } catch (Exception e) {
                    red.addFlashAttribute("error", "Sorry! Failed to delete role.");
                    dir = "redirect:/admin/roles";
                }
                break;

            //Delete class
            case "classes":
                try {
                    classService.delete(id);
                    auditService.record("DELETE_CLASS", auth.getEmail() + " Deleted class id=" + id);
                    red.addFlashAttribute("message", "Class successfully deleted.");
                    dir = "redirect:/admin/classes";
                    
                } catch (Exception e) {
                    red.addFlashAttribute("error", "Sorry! Failed to delete class.");
                    dir = "redirect:/admin/classes";
                }
                break;

            // Delete type
            case "types":
                try {
                    typeService.delete(id);
                    auditService.record("DELETE_TYPE", auth.getEmail() + " Deleted type id=" + id);
                    red.addFlashAttribute("message", "Type successfully deleted.");
                    dir = "redirect:/admin/types";
                } catch (Exception e) {
                    red.addFlashAttribute("error", "Sorry! Failed to delete user");
                    dir = "redirect:/admin/types";
                }
                break;

            // Delete status
            case "statuses":
                try {
                    statusService.delete(id);
                    auditService.record("DELETE_STATUS", auth.getEmail() + " Deleted status id=" + id);
                    red.addFlashAttribute("message", "Status successfully deleted");
                    dir = "redirect:/admin/statuses";
                } catch (Exception e) {
                    red.addFlashAttribute("error", "Sorry! Failed to delete status");
                    dir = "redirect:/admin/statuses";
                }
                break;

            // Delete user
            case "users":
                try {
                    userService.deleteById(id);
                    auditService.record("DELETE_USER", auth.getEmail() + " Deleted user id=" + id);
                    red.addFlashAttribute("message", "user successfully deleted");
                    dir = "redirect:/admin/users";
                } catch (Exception e) {
                    red.addFlashAttribute("error", "Sorry! Failed to delete user");
                    dir = "redirect:/admin/users";
                }
                break;
        }

        return dir;
    }
}