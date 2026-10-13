package com.ikram.supervision.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "prises")
public class Prise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int numero;

    private boolean active = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "borne_id")
    @JsonBackReference
    private Borne borne;

    protected Prise() {
    }

    public Prise(int numero) {
        this.numero = numero;
    }

    public Long getId() { return id; }
    public int getNumero() { return numero; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Borne getBorne() { return borne; }
    void setBorne(Borne borne) { this.borne = borne; }
}
