package com.ikram.supervision.controller;

import com.ikram.supervision.dto.BorneRequest;
import com.ikram.supervision.dto.EtatRequest;
import com.ikram.supervision.dto.PanneauRequest;
import com.ikram.supervision.dto.SeuilsRequest;
import com.ikram.supervision.model.Borne;
import com.ikram.supervision.model.Prise;
import com.ikram.supervision.service.BorneService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bornes")
public class BorneController {

    private final BorneService service;

    public BorneController(BorneService service) {
        this.service = service;
    }

    @GetMapping
    public List<Borne> lister() {
        return service.lister();
    }

    @GetMapping("/{id}")
    public Borne trouver(@PathVariable Long id) {
        return service.trouver(id);
    }

    // ---- Gestion des bornes : administrateur uniquement ----

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Borne creer(@Valid @RequestBody BorneRequest requete) {
        return service.creer(requete);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Borne modifier(@PathVariable Long id, @Valid @RequestBody BorneRequest requete) {
        return service.modifier(id, requete);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }

    @PutMapping("/{id}/seuils")
    @PreAuthorize("hasRole('ADMIN')")
    public Borne configurerSeuils(@PathVariable Long id, @Valid @RequestBody SeuilsRequest requete) {
        return service.configurerSeuils(id, requete);
    }

    // ---- Commandes : technicien et administrateur ----

    @PatchMapping("/{id}/maintenance")
    public Borne maintenance(@PathVariable Long id, @RequestBody EtatRequest requete) {
        return service.changerModeMaintenance(id, requete.actif());
    }

    @PatchMapping("/{id}/panneau")
    public Borne commanderPanneau(@PathVariable Long id, @Valid @RequestBody PanneauRequest requete) {
        return service.commanderPanneau(id, requete.angle());
    }

    @PostMapping("/{id}/panneau/securite")
    public Borne mettreEnSecurite(@PathVariable Long id) {
        return service.mettrePanneauEnSecurite(id);
    }

    @PatchMapping("/prises/{priseId}")
    public Prise changerEtatPrise(@PathVariable Long priseId, @RequestBody EtatRequest requete) {
        return service.changerEtatPrise(priseId, requete.actif());
    }
}
