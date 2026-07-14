package com.serviceportal.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.serviceportal.model.ServiceRequest;
import com.serviceportal.repository.ServiceRequestRepository;

/**
 * REST-Controller für Service-Anfragen (/api/requests)
 *
 * Beschreibung (Deutsch):
 * - POST /api/requests: Einreichen einer neuen Anfrage (öffentlich)
 *   Die Anfrage erhält automatisch den Status RECEIVED und einen Zeitstempel.
 * - GET /api/requests: Auflisten aller Anfragen (nur ROLE_ADMIN)
 * - PUT /api/requests/{id}: Status einer Anfrage aktualisieren (nur ROLE_ADMIN)
 *
 * Hinweis: In einer echten Anwendung würde hier zusätzlich ein Bestätigungs-E-Mail-Versand
 * oder ein asynchroner Verarbeitungsprozess stattfinden. Zur Vereinfachung wird hier
 * die Anfrage direkt gespeichert und zurückgegeben.
 */
@RestController
@RequestMapping("/api/requests")
public class RequestController {
    private final ServiceRequestRepository repo;
    public RequestController(ServiceRequestRepository repo) { this.repo = repo; }

    /**
     * Neue Anfrage erstellen (öffentlich)
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ServiceRequest req) {
        req.setStatus("RECEIVED");
        req.setCreatedAt(LocalDateTime.now());
        ServiceRequest saved = repo.save(req);
        // Simulate confirmation (e.g., email) by returning structured response
        return ResponseEntity.ok(saved);
    }

    /**
     * Alle Anfragen auflisten (Admin)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ServiceRequest> list() { return repo.findAll(); }

    /**
     * Status einer Anfrage aktualisieren (Admin)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceRequest> updateStatus(@PathVariable Long id, @RequestBody ServiceRequest update) {
        return repo.findById(id).map(r -> {
            r.setStatus(update.getStatus());
            repo.save(r);
            return ResponseEntity.ok(r);
        }).orElse(ResponseEntity.notFound().build());
    }
}
