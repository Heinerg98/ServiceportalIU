package com.serviceportal.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.serviceportal.model.ServiceOffer;
import com.serviceportal.repository.ServiceOfferRepository;

@RestController
@RequestMapping("/api/offers")
public class OfferController {
    private final ServiceOfferRepository repo;
    public OfferController(ServiceOfferRepository repo) { this.repo = repo; }

    @GetMapping
    public List<ServiceOffer> list() { return repo.findAll(); }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ServiceOffer create(@RequestBody ServiceOffer offer) { return repo.save(offer); }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceOffer> update(@PathVariable Long id, @RequestBody ServiceOffer offer) {
        return repo.findById(id).map(o -> {
            o.setTitle(offer.getTitle()); o.setDescription(offer.getDescription());
            repo.save(o); return ResponseEntity.ok(o);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id); return ResponseEntity.noContent().build();
    }
}
