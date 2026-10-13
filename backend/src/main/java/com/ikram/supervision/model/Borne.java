package com.ikram.supervision.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Borne de recharge équipée d'un panneau solaire orientable et de plusieurs prises.
 */
@Entity
@Table(name = "bornes")
public class Borne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    private String localisation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutBorne statut = StatutBorne.EN_SERVICE;

    /** Inclinaison du panneau solaire, en degrés (0 à 90). */
    private int anglePanneau = 30;

    /** Panneau replié en position de sécurité (vent fort, orage...). */
    private boolean panneauEnSecurite = false;

    /** Seuils d'alarme configurables par l'administrateur. */
    private double seuilTemperatureMax = 60.0;
    private double seuilPuissanceMax = 22.0;

    @OneToMany(mappedBy = "borne", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("numero ASC")
    @JsonManagedReference
    private List<Prise> prises = new ArrayList<>();

    protected Borne() {
    }

    public Borne(String nom, String localisation) {
        this.nom = nom;
        this.localisation = localisation;
    }

    public void ajouterPrise(Prise prise) {
        prise.setBorne(this);
        prises.add(prise);
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    public StatutBorne getStatut() { return statut; }
    public void setStatut(StatutBorne statut) { this.statut = statut; }
    public int getAnglePanneau() { return anglePanneau; }
    public void setAnglePanneau(int anglePanneau) { this.anglePanneau = anglePanneau; }
    public boolean isPanneauEnSecurite() { return panneauEnSecurite; }
    public void setPanneauEnSecurite(boolean panneauEnSecurite) { this.panneauEnSecurite = panneauEnSecurite; }
    public double getSeuilTemperatureMax() { return seuilTemperatureMax; }
    public void setSeuilTemperatureMax(double seuilTemperatureMax) { this.seuilTemperatureMax = seuilTemperatureMax; }
    public double getSeuilPuissanceMax() { return seuilPuissanceMax; }
    public void setSeuilPuissanceMax(double seuilPuissanceMax) { this.seuilPuissanceMax = seuilPuissanceMax; }
    public List<Prise> getPrises() { return prises; }
}
