package com.mfano.mcfs.config;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mfano.mcfs.auth.models.Branch;
import com.mfano.mcfs.auth.models.Role;
import com.mfano.mcfs.auth.models.User;
import com.mfano.mcfs.auth.repositories.BranchRepository;
import com.mfano.mcfs.auth.repositories.RoleRepository;
import com.mfano.mcfs.auth.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class Initializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final BranchRepository branchRepo;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {

        // =========================
        // Initialize Roles
        // =========================

        List<String> defaultRoles = List.of(
                "USER",
                "ADMIN",
                "MANAGER",
                "CASHIER",
                "PROCUREMENT");

        for (String roleName : defaultRoles) {

            if (roleRepo.findByName(roleName).isEmpty()) {

                Role role = new Role();
                role.setName(roleName);
                role.setCreatedBy("sys");

                roleRepo.save(role);
            }
        }

        // =========================
        // Initialize Branches
        // =========================

        List<String> defaultBranches = List.of(
                "HQ",
                "OTHER");

        for (String branchName : defaultBranches) {

            if (branchRepo.findByName(branchName) == null) {

                Branch branch = new Branch();
                branch.setName(branchName);
                branch.setName("sys");
                branch.setCreatedBy("sys");

                branchRepo.save(branch);
            }
        }

        // =========================
        // Initialize Admin
        // =========================

        if (userRepository.findByEmail(adminEmail) == null) {

            Role adminRole = roleRepo.findByName("ADMIN")
                    .orElseThrow(() -> new IllegalStateException(
                            "ADMIN role was not initialized"));

            Branch hqBranch = branchRepo.findByName("HQ");

            User admin = new User();

            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setEnabled(true);
            admin.setBranch(hqBranch);
            admin.setCreatedBy("sys");
            admin.setRoles(Set.of(adminRole));

            userRepository.save(admin);
        }
    }
}
