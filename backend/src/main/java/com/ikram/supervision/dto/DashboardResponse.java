package com.ikram.supervision.dto;

import java.util.List;

public record DashboardResponse(
        long nombreBornes,
        long bornesEnService,
        long bornesEnMaintenance,
        long prisesActives,
        long alarmesActives,
        double energieDelivree24hKwh,
        List<EtatBorne> bornes) {

    /** Dernière mesure connue pour chaque borne. */
    public record EtatBorne(
            Long id,
            String nom,
            String statut,
            Double puissanceKw,
            Double productionSolaireKw,
            Double temperature) {
    }
}
