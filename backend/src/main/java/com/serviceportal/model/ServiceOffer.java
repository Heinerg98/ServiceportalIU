package com.serviceportal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entity: ServiceOffer
 *
 * Beschreibung (Deutsch):
 * - Repräsentiert ein Angebot im Serviceportal (Titel, Beschreibung)
 * - Diese Entität wird in der Datenbanktabelle für Angebote persistiert.
 */
@Entity
public class ServiceOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    public ServiceOffer() {}

    public ServiceOffer(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    // Getter/Setter (Standard)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
