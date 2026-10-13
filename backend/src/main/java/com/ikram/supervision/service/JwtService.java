package com.ikram.supervision.service;

import com.ikram.supervision.model.Utilisateur;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final long dureeHeures;

    public JwtService(JwtEncoder encoder, @Value("${app.jwt.duree-heures:8}") long dureeHeures) {
        this.encoder = encoder;
        this.dureeHeures = dureeHeures;
    }

    public String genererToken(Utilisateur utilisateur) {
        Instant maintenant = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("supervision-bornes")
                .issuedAt(maintenant)
                .expiresAt(maintenant.plus(dureeHeures, ChronoUnit.HOURS))
                .subject(utilisateur.getUsername())
                .claim("roles", List.of(utilisateur.getRole().name()))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
