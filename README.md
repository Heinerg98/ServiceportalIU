# ServiceportalIU - Entwicklungs-Readme

Kurz: Dieses Repository enthält ein kleines Serviceportal (Frontend React + Backend Spring Boot) für Demo-/Studienzwecke.

Wichtige Hinweise (für Abgabe / Prüfung)

- Demo-Daten: Das Backend enthält einen DataLoader, der beim Start Demo-Benutzer, Beispiel-Angebote und Beispiel-Anfragen einfügt. Der DataLoader ist standardmäßig aktiv und läuft bei jedem Start der Anwendung. Wenn du ihn deaktivieren möchtest, entferne oder benenne die Klasse um (z. B. DataLoader.disabled.java) oder entferne die @Component-Annotation.

- Demo-Zugangsdaten (nur Entwicklung):
  - admin / admin (ROLE_ADMIN)
  - user / user (ROLE_USER)
  - alice / alice123 (ROLE_USER)
  - bob / bob123 (ROLE_USER)
  - carla / carla123 (ROLE_USER,ROLE_MANAGER)
  - dave / dave123 (ROLE_USER)

- Sicherheitshinweise:
  - Authentifizierung: In der Demo wird Basic‑Auth verwendet und der Basic‑Token in `localStorage` gespeichert. Das ist nur für die lokale Entwicklung gedacht und **nicht** für Produktion.
  - CSRF: CSRF-Schutz ist für die Entwicklungsumgebung deaktiviert (siehe `SecurityConfig`). In produktiven Systemen muss CSRF aktiviert/korrekt konfiguriert werden.
  - H2 Console: Die H2-Console ist für Entwicklung verfügbar unter `/h2-console`. Nicht in Produktion aktivieren.
  - DataLoader: Standardmäßig aktiv. Entfernen oder schützen vor produktivem Einsatz.


## Schnellstart (Entwicklung)

Voraussetzungen
- Java 17
- Maven
- Node.js + npm

Backend starten (mit Demo-Daten)

```bash
# im Repo-Root
cd backend
mvn clean package -DskipTests
mvn spring-boot:run
```

- H2-Konsole: http://localhost:8080/h2-console (JDBC URL z.B. `jdbc:h2:file:./backend/data/serviceportal`, Benutzer `sa`, Passwort leer)
- API: Offers: http://localhost:8080/api/offers
- Admin-APIs erfordern Admin-Credentials (z.B. admin/admin)

Frontend starten

```bash
cd frontend
npm install
npm run dev
# öffne http://localhost:5173
```


