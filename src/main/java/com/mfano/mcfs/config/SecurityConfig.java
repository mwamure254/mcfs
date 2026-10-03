package com.mfano.mcfs.config;

import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

import com.mfano.mcfs.auth.services.CustomDetailService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {
    private final CustomDetailService customDetailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthHandler auth;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(auth -> auth
                // Public pages
                .requestMatchers(
                        "/",
                        "/landing",
                        "/forgot",
                        "/error/**",
                        "/login",
                        "/css/**",
                        "/js/**",
                        "/fonts/**",
                        "/scss/**",
                        "/vendor/**",
                        "/images/**")
                .permitAll()

                // Protected pages
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "CEO")
                .requestMatchers("/ict/**").hasAnyRole("ICT", "CEO")
                .requestMatchers("/procurment/**").hasAnyRole("PMO", "CEO")
                .requestMatchers("/hr/**").hasAnyRole("HRO", "CEO")
                .requestMatchers("/accounts/**").hasAnyRole("ACO", "CEO")
                .requestMatchers("/records/**").hasAnyRole("RMO", "CEO")
                .requestMatchers("/executive/**").hasAnyRole("CEO", "BDM", "CMM")

                .requestMatchers("/profile", "/reports", "/documents/**", "/tasks/**")
                .hasAnyRole("ADMIN", "ICT", "CEO", "PMO", "BDM", "CMM", "RMO", "HRO", "ACO")
                .requestMatchers("/audits", "/logs").hasAnyRole("ADMIN", "CEO", "ICT", "ACO")
                .requestMatchers("/budgets").hasAnyRole("CEO", "BDM", "CMM", "ACO")
                .requestMatchers("/assets/**").hasAnyRole("PMO", "CEO", "BDM", "ACO", "ICT")
                .requestMatchers("/applications/**").hasAnyRole("HRO", "CEO", "ICT")

                .anyRequest().authenticated())

                // 403 Access Denied
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendRedirect("/error/403");
                        }))

                .formLogin(form -> form
                        // If you want a default /lnding page or /login
                        .loginPage("/login")
                        .loginProcessingUrl("/login") //open when /landing is login page
                        .successHandler(auth)
                        .failureHandler(auth)
                        .permitAll())

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler(auth)
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());

        return http.build();
    }

    // =====================================================
    // AUTHENTICATION PROVIDER
    // =====================================================
    @Bean
    AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customDetailService);

        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    // =====================================================
    // AUTHENTICATION MANAGER
    // =====================================================
    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    // IMAGE ROUTE
    @Value("${app.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        Path profilePath = Paths.get(uploadDir, "images", "profile")
                .toAbsolutePath()
                .normalize();

        Path documentPath = Paths.get(uploadDir, "documents")
                .toAbsolutePath()
                .normalize();

        registry.addResourceHandler("/images/profile/**", "/documents/**")
                .addResourceLocations(profilePath.toUri().toString(), documentPath.toUri().toString());
    }
}
