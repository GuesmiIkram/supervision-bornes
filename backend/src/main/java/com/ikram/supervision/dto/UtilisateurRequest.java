package com.ikram.supervision.dto;

import com.ikram.supervision.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UtilisateurRequest(
        @NotBlank String username,
        @NotBlank @Size(min = 6) String password,
        @NotNull Role role) {
}
