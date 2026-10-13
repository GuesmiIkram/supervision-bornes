package com.ikram.supervision.controller;

import com.ikram.supervision.dto.LoginRequest;
import com.ikram.supervision.dto.LoginResponse;
import com.ikram.supervision.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest requete) {
        return authService.authentifier(requete);
    }
}
