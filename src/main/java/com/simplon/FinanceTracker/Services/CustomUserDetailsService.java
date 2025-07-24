package com.simplon.FinanceTracker.Services;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.simplon.FinanceTracker.Models.User;
import com.simplon.FinanceTracker.Repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

    @Service
    @Slf4j
    public class CustomUserDetailsService implements UserDetailsService {

        private final UserRepository userRepository;

        public CustomUserDetailsService(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new java.util.ArrayList<>());
        }

    }

