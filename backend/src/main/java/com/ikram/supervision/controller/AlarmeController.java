package com.ikram.supervision.controller;

import com.ikram.supervision.model.Alarme;
import com.ikram.supervision.service.AlarmeService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alarmes")
public class AlarmeController {

    private final AlarmeService service;

    public AlarmeController(AlarmeService service) {
        this.service = service;
    }

    @GetMapping
    public List<Alarme> lister(@RequestParam(defaultValue = "false") boolean actives) {
        return service.lister(actives);
    }

    @PostMapping("/{id}/acquitter")
    public Alarme acquitter(@PathVariable Long id, Authentication authentication) {
        return service.acquitter(id, authentication.getName());
    }
}
