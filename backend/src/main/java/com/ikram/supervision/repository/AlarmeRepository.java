package com.ikram.supervision.repository;

import com.ikram.supervision.model.Alarme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmeRepository extends JpaRepository<Alarme, Long> {
    List<Alarme> findAllByOrderByHorodatageDesc();
    List<Alarme> findByAcquitteeFalseOrderByHorodatageDesc();
    long countByAcquitteeFalse();
}
