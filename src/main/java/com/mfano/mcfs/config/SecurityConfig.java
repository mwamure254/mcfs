package com.mfano.mcfs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

import com.mfano.mcfs.auth.services.CustomDetailService;
import com.mfano.mcfs.config.AuthHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
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
                .requestMatchers("/admin/**").hasAnyRole("ADMIN","CEO")
                .requestMatchers("/ict/**").hasAnyRole("ICT","CEO")
                .requestMatchers("/procurment/**").hasAnyRole("PMO","CEO")
                .requestMatchers("/hr/**").hasAnyRole("HRO", "CEO")
                .requestMatchers("/accounts/**").hasAnyRole("ACO","CEO")
                .requestMatchers("/records/**").hasAnyRole("RMO","CEO")
                .requestMatchers("/executive/**").hasAnyRole("CEO","BDM","CMM")

                .requestMatchers("/profile", "/reports", "/documents/**", "/tasks/**").hasAnyRole("ADMIN","ICT","CEO","PMO","BDM","CMM","RMO","HRO","ACO")
                .requestMatchers("/audits", "/logs").hasAnyRole("ADMIN","CEO","ICT","ACO")
                .requestMatchers("/budgets").hasAnyRole("CEO","BDM","CMM","ACO")
                .requestMatchers("/assets/**").hasAnyRole("PMO","CEO","BDM","ACO","ICT")
                .requestMatchers("/applications/**").hasAnyRole("HRO","CEO","ICT")

                .anyRequest().authenticated())

                 // 403 Access Denied
                .exceptionHandling(exception -> exception
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendRedirect("/error/403");
                     })
                )

                .formLogin(form -> form
                        //If you want a default /lnding page or /login
                        .loginPage("/login")
                        //.loginProcessingUrl("/login") //open when /landing is login page
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

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customDetailService);

        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
