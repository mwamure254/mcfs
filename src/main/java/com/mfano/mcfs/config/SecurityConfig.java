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
                        "/login",
                        "/css/**",
                        "/js/**",
                        "/fonts/**",
                        "/scss/**",
                        "/vendor/**",
                        "/images/**")
                .permitAll()

                // Protected pages
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/manager/**").hasRole("MANAGER")
                .requestMatchers("/cashier/**").hasRole("CASHIER")
                .requestMatchers("/procurement/**").hasRole("PROCUREMENT")

                .anyRequest().authenticated())

                .formLogin(form -> form
                        //If you want a defaultb landing page or /login
                        .loginPage("/landing")
                        .loginProcessingUrl("/login")
                        .successHandler(auth)
                        .failureHandler(auth)
                        //.defaultSuccessUrl("/", true)
                        //.failureUrl("/login?error=true")
                        .permitAll())

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler(auth)
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/landing")
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
