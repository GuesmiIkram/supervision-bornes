package com.ikram.supervision.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

/**
 * Session de recharge d'un véhicule sur une prise.
 */
@Entity
@Table(name = "recharges")
public class Recharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "prise_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Prise prise;

    private LocalDateTime debut;

    private LocalDateTime fin;

    private double energieKwh;

    protected Recharge() {
    }

    public Recharge(Prise prise, LocalDateTime debut, LocalDateTime fin, double energieKwh) {
        this.prise = prise;
        this.debut = debut;
        this.fin = fin;
        this.energieKwh = energieKwh;
    }

    public Long getId() { return id; }
    public String getBorneNom() { return prise.getBorne().getNom(); }
    public int getNumeroPrise() { return prise.getNumero(); }
    public LocalDateTime getDebut() { return debut; }
    public LocalDateTime getFin() { return fin; }
    public double getEnergieKwh() { return energieKwh; }
}
