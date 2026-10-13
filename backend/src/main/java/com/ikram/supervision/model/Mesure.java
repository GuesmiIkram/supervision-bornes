package com.ikram.supervision.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

/**
 * Mesure envoyée périodiquement par une borne.
 */
@Entity
@Table(name = "mesures", indexes = @Index(name = "idx_mesure_borne_date", columnList = "borne_id, horodatage"))
public class Mesure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "borne_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Borne borne;

    @Column(nullable = false)
    private LocalDateTime horodatage;

    /** Puissance délivrée en kW. */
    private double puissanceKw;

    /** Production du panneau solaire en kW. */
    private double productionSolaireKw;

    /** Température interne en °C. */
    private double temperature;

    protected Mesure() {
    }

    public Mesure(Borne borne, LocalDateTime horodatage, double puissanceKw,
                  double productionSolaireKw, double temperature) {
        this.borne = borne;
        this.horodatage = horodatage;
        this.puissanceKw = puissanceKw;
        this.productionSolaireKw = productionSolaireKw;
        this.temperature = temperature;
    }

    public Long getId() { return id; }
    public Borne getBorne() { return borne; }
    public Long getBorneId() { return borne.getId(); }
    public LocalDateTime getHorodatage() { return horodatage; }
    public double getPuissanceKw() { return puissanceKw; }
    public double getProductionSolaireKw() { return productionSolaireKw; }
    public double getTemperature() { return temperature; }
}
