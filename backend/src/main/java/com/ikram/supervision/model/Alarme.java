package com.ikram.supervision.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "alarmes")
public class Alarme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "borne_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Borne borne;

    @Enumerated(EnumType.STRING)
    private NiveauAlarme niveau;

    private String message;

    private LocalDateTime horodatage;

    private boolean acquittee = false;

    private String acquitteePar;

    private LocalDateTime dateAcquittement;

    protected Alarme() {
    }

    public Alarme(Borne borne, NiveauAlarme niveau, String message) {
        this.borne = borne;
        this.niveau = niveau;
        this.message = message;
        this.horodatage = LocalDateTime.now();
    }

    public void acquitter(String utilisateur) {
        this.acquittee = true;
        this.acquitteePar = utilisateur;
        this.dateAcquittement = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Borne getBorne() { return borne; }
    public Long getBorneId() { return borne.getId(); }
    public String getBorneNom() { return borne.getNom(); }
    public NiveauAlarme getNiveau() { return niveau; }
    public String getMessage() { return message; }
    public LocalDateTime getHorodatage() { return horodatage; }
    public boolean isAcquittee() { return acquittee; }
    public String getAcquitteePar() { return acquitteePar; }
    public LocalDateTime getDateAcquittement() { return dateAcquittement; }
}
