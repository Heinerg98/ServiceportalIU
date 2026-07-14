package com.serviceportal.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * DataLoader-Komponente (Entwicklung)
 *
 * Beschreibung (Deutsch):
 * - Beim Start der Anwendung prüft dieser CommandLineRunner, ob bestimmte Tabellen
 *   (app_user, offers, requests) vorhanden sind und fügt bei Bedarf Demo-Daten ein.
 * - Die Komponente ist bewusst robust ausgelegt: sie prüft mehrere mögliche Tabellennamen
 *   und verschiedene Spalten-Varianten, um in unterschiedlichen Schema-Varianten zu funktionieren.
 * - Achtung: Diese Komponente ist nur für Entwicklungszwecke gedacht. Sie legt Demo-Benutzer
 *   mit einfachen Passwörtern an (admin/admin, user/user, alice/alice123, ...). Sie ist
 *   deshalb auf das Spring-Profile "dev" beschränkt. Starte das Backend mit dem dev-Profile,
 *   um die Demo-Daten zu erzeugen:
 *     mvn spring-boot:run -Dspring-boot.run.profiles=dev
 */
@Component("demoDataLoader")
@Profile("dev")
public class DataLoader implements CommandLineRunner {

    private final JdbcTemplate jdbc;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(JdbcTemplate jdbc, PasswordEncoder passwordEncoder) {
        this.jdbc = jdbc;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Alle Tabellen der PUBLIC-Schema abfragen
        List<String> tables = jdbc.queryForList("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'", String.class);
        var lowerTables = tables.stream().map(String::toLowerCase).toList();

        // 1) Demo-Benutzer einfügen (wenn app_user existiert)
        if (lowerTables.contains("app_user")) {
            try {
                Integer cAdmin = jdbc.queryForObject("SELECT COUNT(*) FROM app_user WHERE username='admin'", Integer.class);
                if (cAdmin == null || cAdmin == 0) {
                    String enc = passwordEncoder.encode("admin");
                    jdbc.update("INSERT INTO app_user(username,password,roles) VALUES (?,?,?)", "admin", enc, "ROLE_ADMIN");
                    System.out.println("Inserted demo admin/admin user into app_user");
                }
            } catch (Exception e) {
                System.out.println("Could not check/insert admin: " + e.getMessage());
            }
            // Zusätzliche Demo-Benutzer (Passwörter sind Beispielpasswörter für die DEV-Umgebung)
            String[][] demoUsers = new String[][]{
                {"user","user","ROLE_USER"},
                {"alice","alice123","ROLE_USER"},
                {"bob","bob123","ROLE_USER"},
                {"carla","carla123","ROLE_USER,ROLE_MANAGER"},
                {"dave","dave123","ROLE_USER"}
            };
            for (String[] u : demoUsers) {
                try {
                    Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM app_user WHERE username=?", Integer.class, u[0]);
                    if (cnt == null || cnt == 0) {
                        String enc = passwordEncoder.encode(u[1]);
                        jdbc.update("INSERT INTO app_user(username,password,roles) VALUES (?,?,?)", u[0], enc, u[2]);
                        System.out.println("Inserted demo user: " + u[0]);
                    }
                } catch (Exception ex) {
                    System.out.println("Skipping insert for user " + u[0] + " - " + ex.getMessage());
                }
            }
        } else {
            System.out.println("app_user table not present; skipping demo user insertion");
        }

        // 2) Beispiel-Angebote einfügen (verschiedene mögliche Tabellennamen prüfen)
        String offersTable = null;
        for (String t : lowerTables) {
            if (t.equals("offer") || t.equals("offers") || t.equals("service_offer") || t.equals("service_offers") || t.equals("serviceportal_offer")) {
                offersTable = t;
                break;
            }
        }
        if (offersTable != null) {
            try {
                Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM " + offersTable, Integer.class);
                if (c == null || c == 0) {
                    jdbc.update("INSERT INTO " + offersTable + "(title, description) VALUES (?,?)", "Beratungsangebot", "Beratung zu digitalen Diensten und Anträgen");
                    jdbc.update("INSERT INTO " + offersTable + "(title, description) VALUES (?,?)", "Hilfe bei Formularen", "Unterstützung beim Ausfüllen komplexer Formulare");
                    jdbc.update("INSERT INTO " + offersTable + "(title, description) VALUES (?,?)", "Unterstützung Barrierefreiheit", "Beratung und Hilfestellung für barrierefreie Zugänge");
                    System.out.println("Inserted sample offers into " + offersTable);
                }
            } catch (Exception ex) {
                System.out.println("Could not insert offers into " + offersTable + " - " + ex.getMessage());
            }
        } else {
            System.out.println("No offers table found; skipping sample offers insertion");
        }

        // 3) Beispiel-Anfragen einfügen; versucht mehrere Spaltenvarianten für maximale Kompatibilität
        String requestsTable = null;
        for (String t : lowerTables) {
            if (t.equals("request") || t.equals("requests") || t.equals("service_request") || t.equals("service_requests") || t.equals("serviceportal_request")) {
                requestsTable = t;
                break;
            }
        }
        if (requestsTable != null) {
            try {
                Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM " + requestsTable, Integer.class);
                if (c == null || c == 0) {
                    // Versuche zuerst die häufige requester_name/requster_email-Variante
                    boolean inserted = false;
                    try {
                        jdbc.update("INSERT INTO " + requestsTable + "(requester_name, requester_email, message, status) VALUES (?,?,?,?)", "Marta Meier", "marta@example.org", "Ich benötige Unterstützung bei der Beantragung von Leistungen.", "RECEIVED");
                        jdbc.update("INSERT INTO " + requestsTable + "(requester_name, requester_email, message, status) VALUES (?,?,?,?)", "Tom Bauer", "tom@example.org", "Frage zur Dokumentenanforderung für Antrag X.", "RECEIVED");
                        jdbc.update("INSERT INTO " + requestsTable + "(requester_name, requester_email, message, status) VALUES (?,?,?,?)", "Alice König", "alice@example.org", "Gibt es Unterstützungsangebote für Senior:innen?", "RECEIVED");
                        inserted = true;
                        System.out.println("Inserted sample requests into " + requestsTable + " (requester_name variant)");
                    } catch (Exception ex) {
                        // Fallback: andere mögliche Spaltennamen ausprobieren
                        try {
                            jdbc.update("INSERT INTO " + requestsTable + "(requesterName, requesterEmail, message, status) VALUES (?,?,?,?)", "Marta Meier", "marta@example.org", "Ich benötige Unterstützung bei der Beantragung von Leistungen.", "RECEIVED");
                            jdbc.update("INSERT INTO " + requestsTable + "(requesterName, requesterEmail, message, status) VALUES (?,?,?,?)", "Tom Bauer", "tom@example.org", "Frage zur Dokumentenanforderung für Antrag X.", "RECEIVED");
                            jdbc.update("INSERT INTO " + requestsTable + "(requesterName, requesterEmail, message, status) VALUES (?,?,?,?)", "Alice König", "alice@example.org", "Gibt es Unterstützungsangebote für Senior:innen?", "RECEIVED");
                            inserted = true;
                            System.out.println("Inserted sample requests into " + requestsTable + " (requesterName variant)");
                        } catch (Exception ex2) {
                            try {
                                jdbc.update("INSERT INTO " + requestsTable + "(requester, email, message, status) VALUES (?,?,?,?)", "Marta Meier", "marta@example.org", "Ich benötige Unterstützung bei der Beantragung von Leistungen.", "RECEIVED");
                                jdbc.update("INSERT INTO " + requestsTable + "(requester, email, message, status) VALUES (?,?,?,?)", "Tom Bauer", "tom@example.org", "Frage zur Dokumentenanforderung für Antrag X.", "RECEIVED");
                                jdbc.update("INSERT INTO " + requestsTable + "(requester, email, message, status) VALUES (?,?,?,?)", "Alice König", "alice@example.org", "Gibt es Unterstützungsangebote für Senior:innen?", "RECEIVED");
                                inserted = true;
                                System.out.println("Inserted sample requests into " + requestsTable + " (requester/email variant)");
                            } catch (Exception ex3) {
                                System.out.println("Could not insert sample requests into " + requestsTable + " - column mismatch: " + ex3.getMessage());
                            }
                        }
                    }

                    // Falls alle Insert-Versuche scheitern: Minimal-Fallback nur mit message/status
                    if (!inserted) {
                        try {
                            jdbc.update("INSERT INTO " + requestsTable + "(message, status) VALUES (?,?)", "Allgemeine Anfrage: Gibt es Hilfen?", "RECEIVED");
                            jdbc.update("INSERT INTO " + requestsTable + "(message, status) VALUES (?,?)", "Zweite Anfrage: Bitte um Rückruf.", "RECEIVED");
                            System.out.println("Inserted fallback sample requests into " + requestsTable + " (message/status only)");
                        } catch (Exception ex4) {
                            System.out.println("Could not insert fallback requests into " + requestsTable + " - " + ex4.getMessage());
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println("Could not insert requests into " + requestsTable + " - " + ex.getMessage());
            }
        } else {
            System.out.println("No requests table found; skipping sample requests insertion");
        }

        System.out.println("DataLoader finished");
    }
}
