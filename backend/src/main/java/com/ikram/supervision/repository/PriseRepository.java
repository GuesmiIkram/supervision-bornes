package com.ikram.supervision.repository;

import com.ikram.supervision.model.Prise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriseRepository extends JpaRepository<Prise, Long> {
    long countByActiveTrue();
}
