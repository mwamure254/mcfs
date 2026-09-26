package com.mfano.mcfs.config;

import java.util.HashSet;
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

import com.mfano.mcfs.utils.documents.models.DocumentClass;
import com.mfano.mcfs.utils.documents.models.DocumentStatus;
import com.mfano.mcfs.utils.documents.models.DocumentType;
import com.mfano.mcfs.utils.documents.repositories.DocumentClassRepository;
import com.mfano.mcfs.utils.documents.repositories.DocumentStatusRepository;
import com.mfano.mcfs.utils.documents.repositories.DocumentTypeRepository;

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

    private final DocumentStatusRepository ds;
    private final DocumentTypeRepository dt;
    private final DocumentClassRepository dc;

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
                "CEO",
                "ADMIN",
                "CMM",
                "BDM",
                "ICT",
                "HRO",
                "ACO",
                "RMO",
                "PMO"
        );

        for (String roleName : defaultRoles) {

            if (roleRepo.findByName(roleName).isEmpty()) {

                Role role = new Role();

                role.setName(roleName);
                role.setDescription(roleName);
                role.setCreatedBy("sys");

                roleRepo.save(role);
            }
        }

        // =========================
        // Initialize Branches
        // =========================
        List<String> defaultBranches = List.of(
                "HQ",
                "OTHER"
        );

        for (String branchName : defaultBranches) {

            if (branchRepo.findByName(branchName).isEmpty()) {

                Branch branch = new Branch();

                branch.setName(branchName);
                branch.setDescription(branchName);
                branch.setCreatedBy("sys");

                branchRepo.save(branch);
            }
        }

        // =========================
        // Initialize Admin
        // =========================
        if (userRepository.findByEmail(adminEmail).isEmpty()) {

            Role adminRole = roleRepo.findByName("ADMIN")
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "ADMIN role was not initialized"));

            Branch hqBranch = branchRepo.findByName("HQ")
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "HQ branch was not initialized"));

            User admin = new User();

            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setEnabled(true);
            admin.setBranch(hqBranch);
            admin.setCreatedBy("sys");
            
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            admin.setRoles(roles);

            userRepository.save(admin);
        }

        // =========================
        // Initialize Document Types
        // =========================
        List<String> documentTypes = List.of(
                "MEMO",
                "LETTER",
                "REPORT",
                "INVOICE"
        );

        for (String name : documentTypes) {

            if (dt.findByName(name).isEmpty()) {

                DocumentType documentType = new DocumentType();

                documentType.setName(name);
                documentType.setDescription(name);
                documentType.setCreatedBy("sys");

                dt.save(documentType);
            }
        }

        // =========================
        // Initialize Document Classes
        // =========================
        List<String> documentClasses = List.of(
                "CONFIDENTIAL",
                "RESTRICTED",
                "PUBLIC",
                "PRIVATE"
        );

        for (String name : documentClasses) {

            // IMPORTANT: use dc, not dt
            if (dc.findByName(name).isEmpty()) {

                DocumentClass documentClass = new DocumentClass();

                documentClass.setName(name);
                documentClass.setDescription(name);
                documentClass.setCreatedBy("sys");

                dc.save(documentClass);
            }
        }

        // =========================
        // Initialize Document Status
        // =========================
        List<String> documentStatuses = List.of(
                "DRAFT",
                "RECEIVED",
                "PENDING",
                "APPROVED",
                "REJECTED",
                "RETURNED",
                "COMPLETED",
                "FILED",
                "ARCHIVED",
                "CLOSED",
                "DISPOSED",
                "RETRIEVED",
                "FORWARDED"
        );

        for (String name : documentStatuses) {

            if (ds.findByName(name).isEmpty()) {

                DocumentStatus status = new DocumentStatus();

                status.setName(name);
                status.setDescription(name);
                status.setCreatedBy("sys");

                ds.save(status);
            }
        }
    }
}
