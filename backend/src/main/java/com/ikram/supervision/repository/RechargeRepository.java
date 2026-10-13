package com.ikram.supervision.repository;

import com.ikram.supervision.model.Recharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface RechargeRepository extends JpaRepository<Recharge, Long> {
    List<Recharge> findAllByOrderByDebutDesc();

    @Query("select coalesce(sum(r.energieKwh), 0) from Recharge r where r.debut >= :depuis")
    double energieTotaleDepuis(LocalDateTime depuis);
}
