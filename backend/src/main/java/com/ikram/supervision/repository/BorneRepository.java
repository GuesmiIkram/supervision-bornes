package com.ikram.supervision.repository;

import com.ikram.supervision.model.Borne;
import com.ikram.supervision.model.StatutBorne;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorneRepository extends JpaRepository<Borne, Long> {
    List<Borne> findByStatut(StatutBorne statut);
    long countByStatut(StatutBorne statut);
}
