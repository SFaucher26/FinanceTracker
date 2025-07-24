package com.simplon.FinanceTracker.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.simplon.FinanceTracker.Configuration.JwtUtils;
import com.simplon.FinanceTracker.Dto.AuthenticatedDto;
import com.simplon.FinanceTracker.Dto.LoginDto;
import com.simplon.FinanceTracker.Models.User;
import com.simplon.FinanceTracker.Repositories.UserRepository;

import jakarta.persistence.EntityExistsException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
            JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody LoginDto loginDto) {
        boolean userExist = userRepository.findByUsername(loginDto.getUsername()).isPresent();
        if (userExist) {
            throw new EntityExistsException("Username already exists in database");
        }
        User user = userRepository.saveAndFlush(User.builder()
                .username(loginDto.getUsername())
                .name(loginDto.getUsername())
                .password(passwordEncoder.encode(loginDto.getPassword()))
                .build()
        );
        // renvoie un message de succès
        return ResponseEntity.ok("successful");
    }

    @PostMapping(value ="/authenticate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticatedDto> authenticate(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails.getUsername());

        // récupère l'utilisateur dans la base
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ResponseEntity.ok(new AuthenticatedDto(jwt, user.getId(), user.getUsername()));
    }
}
