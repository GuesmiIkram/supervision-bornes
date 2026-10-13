package com.ikram.supervision.service;

import com.ikram.supervision.model.Mesure;
import com.ikram.supervision.repository.MesureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class MesureService {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final MesureRepository mesures;

    public MesureService(MesureRepository mesures) {
        this.mesures = mesures;
    }

    public List<Mesure> historique(Long borneId, LocalDateTime debut, LocalDateTime fin) {
        LocalDateTime de = debut != null ? debut : LocalDateTime.now().minusHours(24);
        LocalDateTime a = fin != null ? fin : LocalDateTime.now();
        if (de.isAfter(a)) {
            throw new IllegalArgumentException("La date de début doit précéder la date de fin");
        }
        return borneId != null
                ? mesures.findByBorneIdAndHorodatageBetweenOrderByHorodatageAsc(borneId, de, a)
                : mesures.findByHorodatageBetweenOrderByHorodatageAsc(de, a);
    }

    /** Export de l'historique au format CSV (séparateur ;, lisible directement dans Excel). */
    public String exporterCsv(Long borneId, LocalDateTime debut, LocalDateTime fin) {
        StringBuilder csv = new StringBuilder("borne_id;horodatage;puissance_kw;production_solaire_kw;temperature_c\n");
        for (Mesure m : historique(borneId, debut, fin)) {
            csv.append(m.getBorneId()).append(';')
               .append(m.getHorodatage().format(FORMAT)).append(';')
               .append(String.format(Locale.FRANCE, "%.2f;%.2f;%.1f",
                       m.getPuissanceKw(), m.getProductionSolaireKw(), m.getTemperature()))
               .append('\n');
        }
        return csv.toString();
    }
}
