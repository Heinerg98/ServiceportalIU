# ServiceportalIU

Dieses Repository enthält ein Minimalprojekt für ein barrierefreies Serviceportal (Frontend: React + TypeScript, Backend: Spring Boot + JPA + H2).

Starten:

Backend:
- Wechsel in backend/
- mvn spring-boot:run
- API läuft auf http://localhost:8080

Frontend:
- Wechsel in frontend/
- npm install
- npm run dev
- Frontend läuft standardmäßig auf http://localhost:5173

Demo-Accounts:
- admin / admin (ROLE_ADMIN)
- user / user (ROLE_USER)

Die Implementierung ist minimal und bietet grundlegende Endpunkte für Angebote und Anfragen sowie Accessibility-Features im Frontend (Schriftgröße, Kontrastmodus, Tastaturzugänglichkeit).
