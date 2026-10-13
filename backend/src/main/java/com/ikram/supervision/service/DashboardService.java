package com.ikram.supervision.service;

import com.ikram.supervision.dto.DashboardResponse;
import com.ikram.supervision.dto.DashboardResponse.EtatBorne;
import com.ikram.supervision.model.Borne;
import com.ikram.supervision.model.Mesure;
import com.ikram.supervision.model.StatutBorne;
import com.ikram.supervision.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final BorneRepository bornes;
    private final PriseRepository prises;
    private final AlarmeRepository alarmes;
    private final MesureRepository mesures;
    private final RechargeRepository recharges;

    public DashboardService(BorneRepository bornes, PriseRepository prises, AlarmeRepository alarmes,
                            MesureRepository mesures, RechargeRepository recharges) {
        this.bornes = bornes;
        this.prises = prises;
        this.alarmes = alarmes;
        this.mesures = mesures;
        this.recharges = recharges;
    }

    public DashboardResponse tableauDeBord() {
        List<EtatBorne> etats = bornes.findAll().stream().map(this::etat).toList();
        return new DashboardResponse(
                bornes.count(),
                bornes.countByStatut(StatutBorne.EN_SERVICE),
                bornes.countByStatut(StatutBorne.MAINTENANCE),
                prises.countByActiveTrue(),
                alarmes.countByAcquitteeFalse(),
                recharges.energieTotaleDepuis(LocalDateTime.now().minusHours(24)),
                etats);
    }

    private EtatBorne etat(Borne borne) {
        Mesure derniere = mesures.findTop1ByBorneIdOrderByHorodatageDesc(borne.getId())
                .stream().findFirst().orElse(null);
        return new EtatBorne(
                borne.getId(),
                borne.getNom(),
                borne.getStatut().name(),
                derniere != null ? derniere.getPuissanceKw() : null,
                derniere != null ? derniere.getProductionSolaireKw() : null,
                derniere != null ? derniere.getTemperature() : null);
    }
}
