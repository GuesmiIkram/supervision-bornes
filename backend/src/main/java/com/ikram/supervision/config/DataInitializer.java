package com.ikram.supervision.config;

import com.ikram.supervision.model.*;
import com.ikram.supervision.repository.BorneRepository;
import com.ikram.supervision.repository.RechargeRepository;
import com.ikram.supervision.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Jeu de données de démonstration créé au premier démarrage.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UtilisateurRepository utilisateurs;
    private final BorneRepository bornes;
    private final RechargeRepository recharges;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UtilisateurRepository utilisateurs, BorneRepository bornes,
                           RechargeRepository recharges, PasswordEncoder passwordEncoder) {
        this.utilisateurs = utilisateurs;
        this.bornes = bornes;
        this.recharges = recharges;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (utilisateurs.count() > 0) {
            return;
        }
        utilisateurs.save(new Utilisateur("admin", passwordEncoder.encode("admin123"), Role.ADMIN));
        utilisateurs.save(new Utilisateur("technicien", passwordEncoder.encode("tech123"), Role.TECHNICIEN));

        List<Borne> demo = List.of(
                creerBorne("BORNE-VELIZY-01", "Vélizy-Villacoublay - Parking A", 2),
                creerBorne("BORNE-VELIZY-02", "Vélizy-Villacoublay - Parking B", 4),
                creerBorne("BORNE-VERSAILLES-01", "Versailles - Gare Chantiers", 2));
        bornes.saveAll(demo);

        Random random = new Random(42);
        LocalDateTime maintenant = LocalDateTime.now();
        for (int i = 0; i < 30; i++) {
            Borne borne = demo.get(random.nextInt(demo.size()));
            Prise prise = borne.getPrises().get(random.nextInt(borne.getPrises().size()));
            LocalDateTime debut = maintenant.minusHours(random.nextInt(72)).minusMinutes(random.nextInt(60));
            int dureeMinutes = 20 + random.nextInt(100);
            double energie = Math.round(dureeMinutes / 60.0 * (7 + random.nextDouble() * 4) * 10) / 10.0;
            recharges.save(new Recharge(prise, debut, debut.plusMinutes(dureeMinutes), energie));
        }
        log.info("Données de démonstration créées : comptes admin/admin123 et technicien/tech123");
    }

    private Borne creerBorne(String nom, String localisation, int nombrePrises) {
        Borne borne = new Borne(nom, localisation);
        for (int i = 1; i <= nombrePrises; i++) {
            borne.ajouterPrise(new Prise(i));
        }
        return borne;
    }
}
