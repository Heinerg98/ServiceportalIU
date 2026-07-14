# ServiceportalIU - Entwicklungs-Readme

Kurz: Dieses Repository enthält ein kleines Serviceportal (Frontend React + Backend Spring Boot) für Demo-/Studienzwecke.

Wichtige Hinweise (für Abgabe / Prüfung)

- Demo-Daten: Das Backend enthält einen DataLoader, der beim Start Demo-Benutzer, Beispiel-Angebote und Beispiel-Anfragen einfügt. Aus Sicherheitsgründen läuft dieser Loader jetzt nur, wenn das Spring‑Profile `dev` aktiv ist. Starte das Backend mit dem Profile `dev`, um die Demo‑Daten zu erzeugen (Anleitung weiter unten).

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
  - DataLoader: Nur im `dev`-Profile aktiv. Entfernen oder schützen vor produktivem Einsatz.


## Schnellstart (Entwicklung)

Voraussetzungen
- Java 17
- Maven
- Node.js + npm

Backend starten (mit Demo-Daten)

```bash
# im Repo-Root
cd backend
# Demo-DataLoader nur aktiv, wenn Profile=dev
mvn clean package -DskipTests
mvn spring-boot:run -Dspring-boot.run.profiles=dev
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

Fehlerbehebung / Checks
- Logs: Prüfe Backend-Logs auf `Inserted demo user` oder `DataLoader finished` beim Start (falls du das dev-profil benutzt hast).
- Browser Console: keine roten Errors
- Prüfe font-size Debug (falls nötig):
  document.documentElement.style.getPropertyValue('--root-font-size')
  window.getComputedStyle(document.documentElement).fontSize
  localStorage.getItem('fontSize')


## Hinweise für die Abgabe
- Entferne vor einem produktiven oder öffentlichen Release alle Demo-Passwörter und den DataLoader oder aktiviere ihn nur lokal (wie jetzt über `dev`-Profile).
- Dokumentiere in deiner Abgabe, dass Auth-Mechanismus, CSRF und H2-Console für Entwicklung konfiguriert sind.


## Dateien mit Kommentaren
- Viele Backend- und Frontend-Dateien enthalten deutschsprachige Inline-Kommentare, die die Architektur und wichtige Entscheidungen erklären. Schau dir insbesondere `backend/src/main/java/com/serviceportal` und `frontend/src` an.


Wenn du möchtest, passe ich den DataLoader so an, dass er stattdessen ein SQL‑Seed‑Skript erzeugt oder nur Offers/Requests legt (keine Benutzer). Sag kurz Bescheid, falls du das bevorzugst.
