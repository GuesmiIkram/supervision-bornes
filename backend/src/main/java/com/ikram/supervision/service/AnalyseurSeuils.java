package com.ikram.supervision.service;

import com.ikram.supervision.model.Alarme;
import com.ikram.supervision.model.Borne;
import com.ikram.supervision.model.Mesure;
import com.ikram.supervision.model.NiveauAlarme;

import java.util.ArrayList;
import java.util.List;

/**
 * Règles de détection d'alarmes : compare une mesure aux seuils configurés sur la borne.
 * Classe sans dépendance Spring pour être testable unitairement.
 */
public final class AnalyseurSeuils {

    /** Au-delà de seuil * MARGE_CRITIQUE, l'alarme devient critique. */
    static final double MARGE_CRITIQUE = 1.15;

    private AnalyseurSeuils() {
    }

    public static List<Alarme> analyser(Borne borne, Mesure mesure) {
        List<Alarme> alarmes = new ArrayList<>();

        if (mesure.getTemperature() > borne.getSeuilTemperatureMax()) {
            NiveauAlarme niveau = mesure.getTemperature() > borne.getSeuilTemperatureMax() * MARGE_CRITIQUE
                    ? NiveauAlarme.CRITIQUE : NiveauAlarme.AVERTISSEMENT;
            alarmes.add(new Alarme(borne, niveau, String.format(
                    "Température %.1f °C supérieure au seuil de %.1f °C",
                    mesure.getTemperature(), borne.getSeuilTemperatureMax())));
        }

        if (mesure.getPuissanceKw() > borne.getSeuilPuissanceMax()) {
            NiveauAlarme niveau = mesure.getPuissanceKw() > borne.getSeuilPuissanceMax() * MARGE_CRITIQUE
                    ? NiveauAlarme.CRITIQUE : NiveauAlarme.AVERTISSEMENT;
            alarmes.add(new Alarme(borne, niveau, String.format(
                    "Puissance %.1f kW supérieure au seuil de %.1f kW",
                    mesure.getPuissanceKw(), borne.getSeuilPuissanceMax())));
        }
        return alarmes;
    }
}
