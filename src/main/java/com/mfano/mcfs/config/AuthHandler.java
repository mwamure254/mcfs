package com.mfano.mcfs.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.mfano.mcfs.auth.services.AuditService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthHandler implements AuthenticationSuccessHandler,
        AuthenticationFailureHandler, LogoutHandler {

    private final AuditService auditService;
    String message = "Invalid username or password";

    private String msg(String ms) {
        return "/login?error=true&message=" +
                URLEncoder.encode(ms, StandardCharsets.UTF_8);
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        // log activity info
        if (authentication != null &&
                authentication.getPrincipal() instanceof CustomUserDetails user) {
            response.sendRedirect("/");
            return;
        }
        response.sendRedirect("/login");
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException {

        if (exception instanceof DisabledException) {
            message = "Your account is disabled";

            response.sendRedirect(
                    msg(message));
            return;
        } else if (exception instanceof LockedException) {
            message = "Your account is locked";
            response.sendRedirect(
                    msg(message));
            return;
        } else if (exception instanceof CredentialsExpiredException) {
            message = "Your password has expired";
            response.sendRedirect(
                    msg(message));
            return;
        } else {
            message = "Invalid user, check your credentials and try again.";
            response.sendRedirect(
                    msg(message));
            return;
        }
    }

    @Override
    public void logout(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {

        if (authentication != null &&
                authentication.getPrincipal() instanceof CustomUserDetails user) {

            auditService.record(
                    "user_logout",
                    "User " + user.getUsername() + " logged out");
        }
    }
}
