package com.serviceportal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.serviceportal.model.ServiceOffer;
import com.serviceportal.model.User;
import com.serviceportal.repository.ServiceOfferRepository;
import com.serviceportal.repository.UserRepository;

/**
 * Hauptklasse der Spring Boot Anwendung.
 *
 * Hinweise (Deutsch):
 * - Enthält die main-Methode zum Starten der Anwendung.
 * - Definiert einen PasswordEncoder-Bean (BCrypt) für die Passwortverschlüsselung.
 * - Fügt einen einfachen CommandLineRunner (dataLoader) hinzu, der beim ersten Start
 *   Beispielbenutzer und Beispielangebote in die Datenbank schreibt, falls diese noch leer sind.
 *
 * Anmerkung: Der eingebettete dataLoader hier ist für Entwicklungszwecke praktisch. In
 * Produktionsumgebungen sollten solche Initialdaten kontrolliert bzw. über Migrations-/Seed-Skripte
 * (Flyway, Liquibase) gehandhabt werden.
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * PasswordEncoder-Bean, die in der Anwendung für das Hashen von Passwörtern verwendet wird.
     * BCrypt ist ein gängiger, sicherer Algorithmus für Passwort-Hashes.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Einfacher Data-Loader als CommandLineRunner.
     *
     * Beschreibung:
     * - Wenn die Benutzer- und Angebots-Repositorys noch leer sind, werden Standarddaten
     *   (admin/user und einige Beispielangebote) angelegt.
     * - Dieses Verhalten erleichtert das lokale Entwickeln und Testen.
     *
     * Sicherheitshinweis: Die Standardpasswörter (z. B. admin/admin) sind nur für die lokale
     * Entwicklung gedacht und dürfen nicht in Produktionsumgebungen verwendet werden.
     */
    @Bean
    public CommandLineRunner dataLoader(UserRepository userRepo, ServiceOfferRepository offerRepo, PasswordEncoder encoder) {
        return args -> {
            if (userRepo.count() == 0) {
                User admin = new User(null, "admin", encoder.encode("admin"), "ROLE_ADMIN,ROLE_USER");
                User user = new User(null, "user", encoder.encode("user"), "ROLE_USER");
                userRepo.save(admin);
                userRepo.save(user);
            }
            if (offerRepo.count() == 0) {
                offerRepo.save(new ServiceOffer(null, "Personalausweis beantragen", "Beantragen Sie einen neuen Personalausweis."));
                offerRepo.save(new ServiceOffer(null, "KFZ-Anmeldung", "Fahrzeug anmelden oder ummelden."));
            }
        };
    }
}
