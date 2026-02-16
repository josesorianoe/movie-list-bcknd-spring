package com.pelisbcknd.movielist.auth.service;

import com.pelisbcknd.movielist.auth.dto.LoginRequest;
import com.pelisbcknd.movielist.auth.dto.RegisterRequest;
import com.pelisbcknd.movielist.common.error.ConflictException;
import com.pelisbcknd.movielist.security.jwt.JwtService;
import com.pelisbcknd.movielist.users.model.User;
import com.pelisbcknd.movielist.users.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public void register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ConflictException("Email ya en uso");
        }

        User u = new User();
        u.setEmail(req.getEmail().toLowerCase().trim());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setRoles(List.of("USER"));

        userRepository.save(u);
    }

    public String login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        return jwtService.generateToken(req.getEmail().toLowerCase().trim());
    }
}