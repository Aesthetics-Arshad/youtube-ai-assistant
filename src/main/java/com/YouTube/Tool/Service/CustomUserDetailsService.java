package com.YouTube.Tool.Service;

import com.YouTube.Tool.Repository.UserRepository;
import com.YouTube.Tool.Entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service // Spring ko batata hai ki yeh ek Service Bean hai
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // UserRepository ko inject kiya
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // UserRepository ka use karke database se user ko dhoondho
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Spring Security ke User object mein apne user ki details daalo
        // Yahan Spring Security tumhare diye gaye password aur database ke hashed password ko compare karega
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>() // User roles/authorities yahan aate hain, abhi ke liye khali
        );
    }
}