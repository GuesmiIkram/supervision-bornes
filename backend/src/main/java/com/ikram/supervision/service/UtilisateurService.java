package com.ikram.supervision.service;

import com.ikram.supervision.dto.UtilisateurRequest;
import com.ikram.supervision.dto.UtilisateurResponse;
import com.ikram.supervision.model.Utilisateur;
import com.ikram.supervision.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UtilisateurService {

    private final UtilisateurRepository utilisateurs;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository utilisateurs, PasswordEncoder passwordEncoder) {
        this.utilisateurs = utilisateurs;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UtilisateurResponse> lister() {
        return utilisateurs.findAll().stream().map(UtilisateurResponse::from).toList();
    }

    public UtilisateurResponse creer(UtilisateurRequest requete) {
        if (utilisateurs.existsByUsername(requete.username())) {
            throw new IllegalArgumentException("Le nom d'utilisateur " + requete.username() + " existe déjà");
        }
        Utilisateur u = new Utilisateur(requete.username(), passwordEncoder.encode(requete.password()), requete.role());
        return UtilisateurResponse.from(utilisateurs.save(u));
    }

    public void supprimer(Long id, String utilisateurConnecte) {
        Utilisateur u = utilisateurs.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur " + id + " introuvable"));
        if (u.getUsername().equals(utilisateurConnecte)) {
            throw new IllegalStateException("Vous ne pouvez pas supprimer votre propre compte");
        }
        utilisateurs.delete(u);
    }
}
