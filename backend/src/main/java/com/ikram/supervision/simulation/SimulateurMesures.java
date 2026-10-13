package com.ikram.supervision.simulation;

import com.ikram.supervision.model.*;
import com.ikram.supervision.repository.AlarmeRepository;
import com.ikram.supervision.repository.BorneRepository;
import com.ikram.supervision.repository.MesureRepository;
import com.ikram.supervision.service.AnalyseurSeuils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Simule les bornes physiques : toutes les N secondes, chaque borne en service
 * envoie une mesure, puis les seuils d'alarme sont vérifiés.
 * Désactivable avec app.simulation.enabled=false.
 */
@Component
@ConditionalOnProperty(name = "app.simulation.enabled", havingValue = "true", matchIfMissing = true)
public class SimulateurMesures {

    private final BorneRepository bornes;
    private final MesureRepository mesures;
    private final AlarmeRepository alarmes;
    private final Random random = new Random();

    public SimulateurMesures(BorneRepository bornes, MesureRepository mesures, AlarmeRepository alarmes) {
        this.bornes = bornes;
        this.mesures = mesures;
        this.alarmes = alarmes;
    }

    @Scheduled(fixedRateString = "${app.simulation.intervalle-ms:10000}")
    @Transactional
    public void genererMesures() {
        LocalDateTime maintenant = LocalDateTime.now();
        for (Borne borne : bornes.findByStatut(StatutBorne.EN_SERVICE)) {
            Mesure mesure = new Mesure(borne, maintenant,
                    puissance(borne), productionSolaire(borne, maintenant), temperature());
            mesures.save(mesure);
            List<Alarme> nouvelles = AnalyseurSeuils.analyser(borne, mesure);
            alarmes.saveAll(nouvelles);
        }
    }

    private double puissance(Borne borne) {
        double total = 0;
        for (Prise prise : borne.getPrises()) {
            if (prise.isActive() && random.nextDouble() < 0.6) {
                total += 3 + random.nextDouble() * 4;   // véhicule branché : 3 à 7 kW
            }
        }
        if (random.nextDouble() < 0.03) {
            total += 10;                                // pic anormal occasionnel
        }
        return arrondir(total);
    }

    private double productionSolaire(Borne borne, LocalDateTime instant) {
        if (borne.isPanneauEnSecurite()) {
            return 0;
        }
        int heure = instant.getHour();
        double ensoleillement = Math.max(0, Math.sin(Math.PI * (heure - 6) / 14.0)); // nul la nuit
        double orientation = Math.cos(Math.toRadians(Math.abs(borne.getAnglePanneau() - 35)));
        return arrondir(5 * ensoleillement * orientation * (0.8 + random.nextDouble() * 0.2));
    }

    private double temperature() {
        double t = 25 + random.nextDouble() * 20;
        if (random.nextDouble() < 0.02) {
            t += 30;                                    // surchauffe occasionnelle
        }
        return arrondir(t);
    }

    private static double arrondir(double v) {
        return Math.round(v * 100) / 100.0;
    }
}
