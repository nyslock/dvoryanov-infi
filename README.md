1. Ausgangspunkt: von null


2. Ziel: strukturierte Verwaltung von Luftfahrtdaten und Flughafen Information (Flugzeuge, Flughäfen)


3. Pflichten: 

- Verwaltung von Flugzeugen (Home-Airport, Flüge, Airline)
- Zuordnung von Airlines zu Flugzeugen
- inkl. Flughäfen (mit IATA-Codes, Stadt, Land, Zeitliche Verwaltung (Abflugszeiten/Ankunftszeiten), Auslastung)

- Verwendung MySQL
- ORM Datenbankverbindung über ORMLite
- Konsole als Menüführung


4. Wünsche

- Grafische Benutzeroberfläche 
- Suche 
- Wartungsverlauf
- Logging???


5. Abgrenzungen

- keine Live Daten
- keine API oder ähnl. Netzwerkkommunikation
- keine Benutzerauthentifizierung


6. Einsatz

- Zielgruppe: Sabo Rubner (ggf. andere Schüler)
- Betriebsystem: Windows
- Versionen: Java 17+, MySQL 8.0+,


7. Datenbankstruktur

FLUGHAFEN
  id, name, iata_code, stadt, land, terminals, zeitzone

AIRLINE
  id, basis_flughafen_id (FK → FLUGHAFEN), name, icao_code, iata_code, hauptsitz, gruendungsjahr

FLUGZEUG
  id, airline_id (FK → AIRLINE), heimat_flughafen_id (FK → FLUGHAFEN),
  kennzeichen, modell, hersteller, baujahr, sitzanzahl, flugstunden_gesamt

ROUTE
  id, airline_id (FK → AIRLINE), abflug_flughafen_id (FK → FLUGHAFEN),
  ziel_flughafen_id (FK → FLUGHAFEN), routennummer, distanz_km, aktiv

FLUG
  id, flugzeug_id (FK → FLUGZEUG), route_id (FK → ROUTE),
  abflug_flughafen_id (FK → FLUGHAFEN), ankunft_flughafen_id (FK → FLUGHAFEN),
  flugnummer, geplanter_abflug, geplante_ankunft,
  tatsaechlicher_abflug, tatsaechliche_ankunft, status

WARTUNG
  id, flugzeug_id (FK → FLUGZEUG), flughafen_id (FK → FLUGHAFEN),
  datum, typ, beschreibung, techniker, status, kosten


Beziehungen

- Eine AIRLINE hat einen Basis-FLUGHAFEN (z.B. Wizz Air → Belgrad)
- Ein FLUGZEUG gehört einer AIRLINE und hat einen Heimat-FLUGHAFEN
- Eine ROUTE verbindet zwei FLUGHAFENs und gehört einer AIRLINE
- Ein FLUG wird von einem FLUGZEUG durchgeführt, folgt einer ROUTE,
  und hat einen Abflug- sowie einen Ankunfts-FLUGHAFEN
- Ein FLUGZEUG kann beliebig viele FLÜGe machen (Hin- und Rückflüge)
- Eine WARTUNG gehört einem FLUGZEUG und findet an einem FLUGHAFEN statt



8. Konsolenmenü

 [1] Flugzeuge verwalten
 [2] Airlines verwalten
 [3] Flughäfen verwalten
 [4] Wartungsaufträge verwalten
 [0] Beenden
========================================
Auswahl:
