package com.ikram.supervision.repository;

import com.ikram.supervision.model.Mesure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MesureRepository extends JpaRepository<Mesure, Long> {
    List<Mesure> findByBorne_IdAndHorodatageBetweenOrderByHorodatageAsc(Long borneId, LocalDateTime debut, LocalDateTime fin);
    List<Mesure> findByHorodatageBetweenOrderByHorodatageAsc(LocalDateTime debut, LocalDateTime fin);
    List<Mesure> findTop1ByBorne_IdOrderByHorodatageDesc(Long borneId);
}
