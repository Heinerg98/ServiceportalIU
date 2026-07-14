package com.serviceportal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.URI;

/**
 * Kleiner Controller, der den Root-Pfad der Anwendung auf die öffentliche Offers-API weiterleitet.
 *
 * Zweck:
 * - Wenn ein Entwickler den Backend-Port im Browser öffnet (z.B. http://localhost:8080/),
 *   soll nicht die Whitelabel-404 erscheinen, sondern eine sinnvolle Weiterleitung zu /api/offers.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Object> root() {
        // Redirect root to the public offers API so a browser visiting the backend root sees useful content
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/api/offers")).build();
    }
}
