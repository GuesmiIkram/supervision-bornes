package com.ikram.supervision.service;

import com.ikram.supervision.dto.LoginRequest;
import com.ikram.supervision.dto.LoginResponse;
import com.ikram.supervision.model.Utilisateur;
import com.ikram.supervision.repository.UtilisateurRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurs;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UtilisateurRepository utilisateurs, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.utilisateurs = utilisateurs;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse authentifier(LoginRequest requete) {
        Utilisateur utilisateur = utilisateurs.findByUsername(requete.username())
                .filter(u -> passwordEncoder.matches(requete.password(), u.getPassword()))
                .orElseThrow(() -> new BadCredentialsException("Identifiant ou mot de passe incorrect"));
        return new LoginResponse(jwtService.genererToken(utilisateur), utilisateur.getUsername(),
                utilisateur.getRole().name());
    }
}
