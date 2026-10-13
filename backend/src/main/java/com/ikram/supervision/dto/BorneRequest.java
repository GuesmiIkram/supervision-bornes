package com.ikram.supervision.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record BorneRequest(
        @NotBlank String nom,
        String localisation,
        @Min(1) @Max(8) int nombrePrises) {
}
