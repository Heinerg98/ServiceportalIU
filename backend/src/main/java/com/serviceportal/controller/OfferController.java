package com.serviceportal.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.serviceportal.model.ServiceOffer;
import com.serviceportal.repository.ServiceOfferRepository;

/**
 * REST-Controller für Angebots-API (/api/offers)
 *
 * Beschreibung (Deutsch):
 * - GET /api/offers: liefert alle verfügbaren Serviceangebote (öffentlich)
 * - POST /api/offers: legt ein neues Angebot an (nur ROLE_ADMIN)
 * - PUT /api/offers/{id}: aktualisiert ein bestehendes Angebot (nur ROLE_ADMIN)
 * - DELETE /api/offers/{id}: löscht ein Angebot (nur ROLE_ADMIN)
 *
 * Sicherheitsaspekte: Schreiboperationen sind mit @PreAuthorize gesichert und erfordern
 * die Rolle ADMIN. Die Lesefunktion ist öffentlich zugänglich, damit das Frontend die
 * Angebote auch ohne Login darstellen kann.
 */
@RestController
@RequestMapping("/api/offers")
public class OfferController {
    private final ServiceOfferRepository repo;
    public OfferController(ServiceOfferRepository repo) { this.repo = repo; }

    /**
     * Alle Angebote zurückgeben.
     */
    @GetMapping
    public List<ServiceOffer> list() { return repo.findAll(); }

    /**
     * Neues Angebot anlegen (Admin).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ServiceOffer create(@RequestBody ServiceOffer offer) { return repo.save(offer); }

    /**
     * Angebot aktualisieren (Admin).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceOffer> update(@PathVariable Long id, @RequestBody ServiceOffer offer) {
        return repo.findById(id).map(o -> {
            o.setTitle(offer.getTitle()); o.setDescription(offer.getDescription());
            repo.save(o); return ResponseEntity.ok(o);
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Angebot löschen (Admin).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id); return ResponseEntity.noContent().build();
    }
}
