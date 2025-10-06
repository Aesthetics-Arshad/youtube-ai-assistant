package com.YouTube.Tool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Yeh Bean batata hai ki passwords ko encrypt karne ke liye BCrypt algorithm use karna hai.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Yeh Bean aapki application ki poori security rules ko define karta hai.
     * Iske upar @Bean hona sabse zaroori hai.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // In URLs ko public rakho (koi bhi access kar sakta hai)
                        .requestMatchers(
                                "/",
                                "/home",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/register",
                                "/login" // Login page ko public karna zaroori hai
                        ).permitAll()
                        // Baaki sabhi URLs ke liye login zaroori hai
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        // Hum Spring ko bata rahe hain ki hamara custom login page is URL par hai
                        .loginPage("/login")
                        .defaultSuccessUrl("/",true)
                        .permitAll()
                )
                .logout(logout -> logout
                        // Logout hone ke baad user ko home page par bhej do
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}