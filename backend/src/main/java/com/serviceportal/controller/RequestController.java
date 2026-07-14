package com.serviceportal.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.serviceportal.model.ServiceRequest;
import com.serviceportal.repository.ServiceRequestRepository;

@RestController
@RequestMapping("/api/requests")
public class RequestController {
    private final ServiceRequestRepository repo;
    public RequestController(ServiceRequestRepository repo) { this.repo = repo; }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ServiceRequest req) {
        req.setStatus("RECEIVED");
        req.setCreatedAt(LocalDateTime.now());
        ServiceRequest saved = repo.save(req);
        // Simulate confirmation (e.g., email) by returning structured response
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ServiceRequest> list() { return repo.findAll(); }

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
