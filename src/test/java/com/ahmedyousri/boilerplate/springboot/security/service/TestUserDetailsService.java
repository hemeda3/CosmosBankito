package com.ahmedyousri.boilerplate.springboot.security.service;

import com.ahmedyousri.boilerplate.springboot.model.UserRole;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Test implementation of UserDetailsService that provides predefined users for testing.
 * This service is only active in the "test" profile and takes precedence over the regular UserDetailsService.
 */
@Service
@Primary
@Profile("test")
public class TestUserDetailsService implements UserDetailsService {

    private static final String USERNAME_OR_PASSWORD_INVALID = "Invalid username or password.";
    private final Map<String, UserDetails> testUsers = new HashMap<>();
    private final BCryptPasswordEncoder passwordEncoder;

    public TestUserDetailsService(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        initializeTestUsers();
    }

    private void initializeTestUsers() {
        // Add test users here
        addTestUser("deposit_user_nomock", "password", UserRole.USER);
        
        // Add more test users as needed
    }

    private void addTestUser(String username, String password, UserRole role) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.name());
        UserDetails userDetails = new User(
                username,
                passwordEncoder.encode(password),
                Collections.singletonList(authority)
        );
        testUsers.put(username, userDetails);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = testUsers.get(username);
        
        if (userDetails == null) {
            throw new UsernameNotFoundException(USERNAME_OR_PASSWORD_INVALID);
        }
        
        return userDetails;
    }
}
