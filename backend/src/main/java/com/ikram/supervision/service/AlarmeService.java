package com.ikram.supervision.service;

import com.ikram.supervision.model.Alarme;
import com.ikram.supervision.repository.AlarmeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AlarmeService {

    private final AlarmeRepository alarmes;

    public AlarmeService(AlarmeRepository alarmes) {
        this.alarmes = alarmes;
    }

    @Transactional(readOnly = true)
    public List<Alarme> lister(boolean seulementActives) {
        return seulementActives
                ? alarmes.findByAcquitteeFalseOrderByHorodatageDesc()
                : alarmes.findAllByOrderByHorodatageDesc();
    }

    public Alarme acquitter(Long id, String utilisateur) {
        Alarme alarme = alarmes.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Alarme " + id + " introuvable"));
        if (alarme.isAcquittee()) {
            throw new IllegalStateException("Cette alarme est déjà acquittée");
        }
        alarme.acquitter(utilisateur);
        return alarme;
    }
}
