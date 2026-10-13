package com.ikram.supervision.service;

import com.ikram.supervision.model.Alarme;
import com.ikram.supervision.model.Borne;
import com.ikram.supervision.model.Mesure;
import com.ikram.supervision.model.NiveauAlarme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AnalyseurSeuilsTest {

    private Borne borne;

    @BeforeEach
    void setUp() {
        borne = new Borne("TEST-01", "Test");
        borne.setSeuilTemperatureMax(60);
        borne.setSeuilPuissanceMax(20);
    }

    private Mesure mesure(double puissance, double temperature) {
        return new Mesure(borne, LocalDateTime.now(), puissance, 0, temperature);
    }

    @Test
    void aucuneAlarmeQuandLesValeursSontNormales() {
        assertThat(AnalyseurSeuils.analyser(borne, mesure(10, 40))).isEmpty();
    }

    @Test
    void avertissementQuandLaTemperatureDepasseLegerementLeSeuil() {
        List<Alarme> alarmes = AnalyseurSeuils.analyser(borne, mesure(10, 62));
        assertThat(alarmes).hasSize(1);
        assertThat(alarmes.get(0).getNiveau()).isEqualTo(NiveauAlarme.AVERTISSEMENT);
    }

    @Test
    void critiqueQuandLaTemperatureDepasseFortementLeSeuil() {
        List<Alarme> alarmes = AnalyseurSeuils.analyser(borne, mesure(10, 80));
        assertThat(alarmes).extracting(Alarme::getNiveau).containsExactly(NiveauAlarme.CRITIQUE);
    }

    @Test
    void deuxAlarmesQuandLesDeuxSeuilsSontDepasses() {
        assertThat(AnalyseurSeuils.analyser(borne, mesure(25, 65))).hasSize(2);
    }
}
