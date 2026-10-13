package com.ikram.supervision.controller;

import com.ikram.supervision.dto.DashboardResponse;
import com.ikram.supervision.model.Recharge;
import com.ikram.supervision.repository.RechargeRepository;
import com.ikram.supervision.service.DashboardService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final DashboardService dashboardService;
    private final RechargeRepository recharges;

    public DashboardController(DashboardService dashboardService, RechargeRepository recharges) {
        this.dashboardService = dashboardService;
        this.recharges = recharges;
    }

    @GetMapping("/dashboard")
    public DashboardResponse tableauDeBord() {
        return dashboardService.tableauDeBord();
    }

    @GetMapping("/recharges")
    @Transactional(readOnly = true)
    public List<Recharge> recharges() {
        return recharges.findAllByOrderByDebutDesc();
    }
}
