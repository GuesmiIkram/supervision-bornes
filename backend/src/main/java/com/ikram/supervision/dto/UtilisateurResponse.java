package com.ikram.supervision.dto;

import com.ikram.supervision.model.Role;
import com.ikram.supervision.model.Utilisateur;

public record UtilisateurResponse(Long id, String username, Role role) {
    public static UtilisateurResponse from(Utilisateur u) {
        return new UtilisateurResponse(u.getId(), u.getUsername(), u.getRole());
    }
}
