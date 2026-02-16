package com.pelisbcknd.movielist.auth.controller;

import com.pelisbcknd.movielist.auth.dto.AuthResponse;
import com.pelisbcknd.movielist.auth.dto.LoginRequest;
import com.pelisbcknd.movielist.auth.dto.RegisterRequest;
import com.pelisbcknd.movielist.auth.service.AuthService;
import com.pelisbcknd.movielist.common.dto.MessageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        return new MessageResponse("Usuario registrado correctamente");
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        String token = authService.login(req);
        return new AuthResponse(token);
    }
}