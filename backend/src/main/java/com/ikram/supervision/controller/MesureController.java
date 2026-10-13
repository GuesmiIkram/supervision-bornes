package com.ikram.supervision.controller;

import com.ikram.supervision.model.Mesure;
import com.ikram.supervision.service.MesureService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/mesures")
public class MesureController {

    private final MesureService service;

    public MesureController(MesureService service) {
        this.service = service;
    }

    @GetMapping
    public List<Mesure> historique(
            @RequestParam(required = false) Long borneId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return service.historique(borneId, debut, fin);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exporterCsv(
            @RequestParam(required = false) Long borneId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        byte[] contenu = service.exporterCsv(borneId, debut, fin).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"mesures.csv\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(contenu);
    }
}
