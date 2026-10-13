package com.ikram.supervision.service;

import com.ikram.supervision.dto.BorneRequest;
import com.ikram.supervision.dto.SeuilsRequest;
import com.ikram.supervision.model.*;
import com.ikram.supervision.repository.AlarmeRepository;
import com.ikram.supervision.repository.BorneRepository;
import com.ikram.supervision.repository.PriseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BorneService {

    private final BorneRepository bornes;
    private final PriseRepository prises;
    private final AlarmeRepository alarmes;

    public BorneService(BorneRepository bornes, PriseRepository prises, AlarmeRepository alarmes) {
        this.bornes = bornes;
        this.prises = prises;
        this.alarmes = alarmes;
    }

    @Transactional(readOnly = true)
    public List<Borne> lister() {
        return bornes.findAll();
    }

    @Transactional(readOnly = true)
    public Borne trouver(Long id) {
        return bornes.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Borne " + id + " introuvable"));
    }

    public Borne creer(BorneRequest requete) {
        Borne borne = new Borne(requete.nom(), requete.localisation());
        for (int i = 1; i <= requete.nombrePrises(); i++) {
            borne.ajouterPrise(new Prise(i));
        }
        return bornes.save(borne);
    }

    public Borne modifier(Long id, BorneRequest requete) {
        Borne borne = trouver(id);
        borne.setNom(requete.nom());
        borne.setLocalisation(requete.localisation());
        return borne;
    }

    public void supprimer(Long id) {
        bornes.delete(trouver(id));
    }

    public Borne changerModeMaintenance(Long id, boolean maintenance) {
        Borne borne = trouver(id);
        borne.setStatut(maintenance ? StatutBorne.MAINTENANCE : StatutBorne.EN_SERVICE);
        alarmes.save(new Alarme(borne, NiveauAlarme.INFO,
                maintenance ? "Borne passée en mode maintenance" : "Borne remise en service"));
        return borne;
    }

    public Borne configurerSeuils(Long id, SeuilsRequest requete) {
        Borne borne = trouver(id);
        borne.setSeuilTemperatureMax(requete.seuilTemperatureMax());
        borne.setSeuilPuissanceMax(requete.seuilPuissanceMax());
        return borne;
    }

    public Borne commanderPanneau(Long id, int angle) {
        Borne borne = trouver(id);
        if (borne.getStatut() == StatutBorne.HORS_LIGNE) {
            throw new IllegalStateException("Impossible de commander le panneau d'une borne hors ligne");
        }
        borne.setAnglePanneau(angle);
        borne.setPanneauEnSecurite(false);
        return borne;
    }

    public Borne mettrePanneauEnSecurite(Long id) {
        Borne borne = trouver(id);
        borne.setAnglePanneau(0);
        borne.setPanneauEnSecurite(true);
        alarmes.save(new Alarme(borne, NiveauAlarme.AVERTISSEMENT, "Panneau solaire mis en position de sécurité"));
        return borne;
    }

    public Prise changerEtatPrise(Long priseId, boolean active) {
        Prise prise = prises.findById(priseId)
                .orElseThrow(() -> new RessourceIntrouvableException("Prise " + priseId + " introuvable"));
        prise.setActive(active);
        return prise;
    }
}
