package com.mfano.mcfs.auth.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mfano.mcfs.dtos.UserDto;
import com.mfano.mcfs.auth.models.User;
import com.mfano.mcfs.auth.models.Role;
import com.mfano.mcfs.auth.models.VerificationToken;
import com.mfano.mcfs.auth.repositories.TokenRepositories;
import com.mfano.mcfs.auth.repositories.UserRepository;
import com.mfano.mcfs.auth.services.BranchService;
import com.mfano.mcfs.utils.mail.MailService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    // private final ProfileService profileService;
    private final BranchService branchService;
    private final RoleService roleService;

    private final TokenRepositories tokenRepository;
    private final MailService emailService;

    @Value("${app.base-url}")
    private String appBaseUrl;

    @Transactional
    public void registerUser(UserDto userDto) {
        if (findByEmail(userDto.getEmail()) != null) {
            throw new RuntimeException("Email already in use");
        }
        if (userDto.getRole() == null) {
            throw new RuntimeException("At least one role is required");
        }
        if (userDto.getBranch() == null) {
            throw new RuntimeException("Branch can not be empty.");
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setFin(userDto.getFin());
        user.setLan(userDto.getLan());             
        user.setGender(userDto.getGender());  

        user.setBranch(branchService.findById(userDto.getBranch()));
        // Role
        Role role = roleService.findById(userDto.getRole());
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        save(user);
        createAndSendToken(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void update(Long id, UserDto userDto){
        User existing = findById(id);

        existing.setFin(userDto.getFin());
        existing.setLan(userDto.getLan());                     
        existing.setGender(userDto.getGender());  

        existing.setPassword(passwordEncoder.encode(userDto.getPassword()));
        existing.setBranch(branchService.findById(userDto.getBranch()));
        save(existing);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public void toggleActive(Long id) {
        User existing = findById(id);
        existing.setEnabled(!Boolean.TRUE.equals(existing.isEnabled()));
        save(existing);
    }

    // Get User By Id
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findByBranch_Id(Long storeId) {
        return userRepository.findByBranch_Id(storeId);
    }

    public void createAndSendToken(User user) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(24);
        VerificationToken vt = new VerificationToken();
        vt.setToken(token);
        vt.setExpiryDate(expiry);
        vt.setUser(user);
        tokenRepository.save(vt);
        String link = appBaseUrl + "/verify?token=" + token;
        String subject = "Please verify your email";
        String body = "Hi " + (user.getEmail() == null ? "" : user.getEmail()) + "\n\n"
                + "Please click the link to verify your email:\n" + link + "\n\n"
                + "This link will expire in 24 hours.\n\nThanks!";
        emailService.sendSimpleMessage(user.getEmail(), subject, body);
    }

    public String validateVerificationToken(String token) {
        Optional<VerificationToken> opt = tokenRepository.findByToken(token);
        if (opt.isEmpty())
            return "invalid";
        VerificationToken vt = opt.get();
        if (vt.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "expired";
        }
        User user = vt.getUser();
        user.setEnabled(true);
        save(user);
        tokenRepository.delete(vt);
        return "valid";
    }

    // Password reset flow
    public void createPasswordResetToken(String email) {
        User user = findByEmail(email);
        if (user == null) {
            throw new RuntimeException("No user with the email provided");
        }

        tokenRepository.deleteByUserId(user.getId());
        String token = UUID.randomUUID().toString();
        VerificationToken prt = new VerificationToken();
        prt.setToken(token);
        prt.setExpiryDate(LocalDateTime.now().plusHours(2));
        prt.setUser(user);
        tokenRepository.save(prt);
        String link = appBaseUrl + "/reset-password?token=" + token;
        String subject = "Password reset request";
        String body = "Hi " + (user.getEmail() == null ? "" : user.getEmail()) + "\n\n"
                + "Click the link to reset your password: \n" + link + "\n\n"
                + "This link expires in 2 hours.\n\nIf you did not request this, ignore this email.";
        emailService.sendSimpleMessage(user.getEmail(), subject, body);
    }

    public String validatePasswordResetToken(String token) {
        var opt = tokenRepository.findByToken(token);
        if (opt.isEmpty())
            return "invalid";
        var prt = opt.get();
        if (prt.getExpiryDate().isBefore(LocalDateTime.now()))
            return "expired";
        return "valid";
    }

    public Optional<User> getUserByPasswordResetToken(String token) {
        return tokenRepository.findByToken(token).map(VerificationToken::getUser);
    }

    @Transactional
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        save(user);
        tokenRepository.deleteByUserId(user.getId());
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    //To Do
    public void assignRoleToUser(Long userId, Long roleId) {
        User user = findById(userId);
        Role role = roleService.findById(roleId);
        user.getRoles().add(role);
        save(user);
    }
    //To Do
     public void removeRoleFromUser(Long userId, Long roleId) {
        User user = findById(userId);
        Role role = roleService.findById(roleId);
        user.getRoles().removeIf(r->r.getId().equals(role.getId()));
        save(user);
    }
}
