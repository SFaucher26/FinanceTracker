package com.simplon.FinanceTracker.Controllers;

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
        // Vérifie si un utilisateur avec ce nom existe déjà dans la base
        boolean userExist = userRepository.findByUsername(loginDto.getUsername()).isPresent();

        // Si oui, on lève une exception pour dire que le nom est déjà pris
        if (userExist) {
            throw new EntityExistsException("Username already exists in database");
        }

        // Sinon, on crée un nouvel utilisateur avec les infos reçues
        User user = userRepository.saveAndFlush(User.builder()
                .username(loginDto.getUsername()) // on met le username
                .name(loginDto.getName())     // le name
                .password(passwordEncoder.encode(loginDto.getPassword())) // on encode le mot de passe
                .build()
        );

        // On renvoie un message pour dire que l'inscription a réussi
        return ResponseEntity.ok("successful");
    }

    @PostMapping(value ="/authenticate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticatedDto> authenticate(@RequestBody LoginDto loginDto) {
        // On vérifie si le nom d'utilisateur et le mot de passe sont corrects
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword())
        );

        // Si c'est bon, on récupère les infos de l'utilisateur connecté
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // On crée un token JWT pour cet utilisateur (pour garder la session)
        String jwt = jwtUtils.generateToken(userDetails.getUsername());

        // On cherche l'utilisateur dans la base pour récupérer son ID et son nom
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // On renvoie le token et les infos utilisateur au client
        return ResponseEntity.ok(new AuthenticatedDto(jwt, user.getId(), user.getUsername()));
    }
}
