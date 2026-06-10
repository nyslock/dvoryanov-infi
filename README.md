# Luftfahrt-Verwaltung

Ein Java-Konsolenprogramm zur strukturierten Verwaltung von Luftfahrtdaten:
**Flughäfen, Airlines, Flugzeuge, Routen, Flüge und Wartungsaufträge.**
Alle Daten werden dauerhaft in einer **SQLite**-Datenbank gespeichert, die
Anbindung erfolgt als ORM über **ORMLite**.

> **Sofort startklar:** SQLite ist dateibasiert – kein Datenbankserver, keine
> Installation, kein Passwort. Beim ersten Start werden Datenbank, Tabellen
> **und Testdaten automatisch angelegt.**

> Das vollständige Pflichtenheft liegt als Textdatei bei: **[`Pflichtenheft.txt`](Pflichtenheft.txt)**.

---

## Inhalt

- [Funktionen](#funktionen)
- [Technik / Voraussetzungen](#technik--voraussetzungen)
- [Schnellstart](#schnellstart)
- [Testdaten](#testdaten)
- [Bedienung (Menü)](#bedienung-menü)
- [Projektstruktur](#projektstruktur)
- [Datenbankstruktur](#datenbankstruktur)
- [Erfüllung des Pflichtenhefts](#erfüllung-des-pflichtenhefts)
- [Archiv](#archiv)

---

## Funktionen

Für jede der sechs Tabellen gibt es eine eigene Verwaltung mit den Aktionen
**Anzeigen, Hinzufügen, Ändern, Löschen und Suchen**:

- **Flughäfen** – Name, IATA-Code, Stadt, Land, Terminals, Zeitzone
- **Airlines** – mit Basis-Flughafen, ICAO-/IATA-Code, Hauptsitz, Gründungsjahr
- **Flugzeuge** – gehören einer Airline und haben einen Heimat-Flughafen
- **Routen** – verbinden zwei Flughäfen, gehören einer Airline
- **Flüge** – konkreter Flug (Flugzeug + Route + Abflug-/Ankunftszeiten + Status)
- **Wartungen** – Wartungsaufträge je Flugzeug, inkl. **Wartungsverlauf**

---

## Technik / Voraussetzungen

| Komponente   | Version / Hinweis                                       |
|--------------|---------------------------------------------------------|
| **Java**     | JDK **17 oder höher** (entwickelt mit **Java 21**)      |
| **SQLite**   | eingebettet über `sqlite-jdbc` – **kein Server nötig**  |
| **ORMLite**  | 6.1 (ORM-Schicht über der Datenbank)                    |
| **Maven**    | 3.9+ (für Build über die Kommandozeile) – optional      |

Die einzige echte Voraussetzung ist ein installiertes **JDK 17+**.
Alle Abhängigkeiten (SQLite-Treiber, ORMLite, slf4j) stehen in der
[`pom.xml`](pom.xml) und werden von Maven automatisch geladen.

---

## Schnellstart

Es gibt **keine** Datenbank-Einrichtung. Einfach das Programm starten – fertig.

**Variante A – in Eclipse**
1. Projekt importieren (`File → Import → Existing Maven Project`).
2. `src/luftfahrt/Main.java` öffnen.
3. Rechtsklick → **Run As → Java Application**.

**Variante B – über die Kommandozeile (Maven)**
```bash
mvn compile
mvn exec:java -Dexec.mainClass=luftfahrt.Main
```

Beim Start erscheint:
```
Mit SQLite verbunden: jdbc:sqlite:luftfahrt.db
Tabellen bereit.
Lege Beispieldaten an...
Beispieldaten angelegt: 5 Flughäfen, 3 Airlines, 4 Flugzeuge, 4 Routen, 3 Flüge, 3 Wartungen.
```

Dabei wird im Projektordner automatisch die Datei **`luftfahrt.db`** angelegt –
das ist die komplette Datenbank.

---

## Testdaten

**Was zuerst ausführen, um Testdaten zu haben?** → Einfach `luftfahrt.Main`
starten. Es ist **kein** zusätzliches Skript und **keine** DB-Einrichtung nötig.

Beim **ersten Start** (solange die Tabelle `flughafen` leer ist) legt das
Programm automatisch einen vollständigen Beispieldatensatz in **allen sechs
Tabellen** an:

- **5 Flughäfen:** Wien (VIE), Belgrad (BEG), Budapest (BUD), München (MUC), Zürich (ZRH)
- **3 Airlines:** Wizz Air, Austrian Airlines, Lufthansa
- **4 Flugzeuge:** Airbus A320 / A321, Boeing B777-200, Airbus A350-900
- **4 Routen:** z. B. BEG→VIE, BUD→VIE, VIE→ZRH, MUC→VIE
- **3 Flüge:** mit geplanten Abflug-/Ankunftszeiten und Status
- **3 Wartungen:** Inspektion, Reparatur und Großcheck

Bei jedem weiteren Start werden **keine** Daten doppelt angelegt
(Prüfung: `if (db.getFlughafenDao().countOf() > 0) return;`).

> **Frische Testdaten gewünscht?** Einfach die Datei `luftfahrt.db` löschen und
> das Programm erneut starten – sie wird mit neuen Testdaten neu erzeugt.

---

## Bedienung (Menü)

```
========================================
 [1] Flugzeuge verwalten
 [2] Airlines verwalten
 [3] Flughäfen verwalten
 [4] Routen verwalten
 [5] Flüge verwalten
 [6] Wartungsaufträge verwalten
 [0] Beenden
========================================
Auswahl:
```

Jeder Punkt öffnet ein Untermenü mit Anzeigen / Hinzufügen / Ändern /
Löschen / Suchen. Bei den Wartungen gibt es zusätzlich den
**Wartungsverlauf** eines Flugzeugs.

---

## Projektstruktur

```
infi_sqlite/
├── src/luftfahrt/
│   ├── model/                 # Datenklassen = Tabellen (ORMLite-Entitäten)
│   │   ├── Flughafen.java
│   │   ├── Airline.java
│   │   ├── Flugzeug.java
│   │   ├── Route.java
│   │   ├── Flug.java
│   │   └── Wartung.java
│   ├── Datenbank.java         # SQLite-Verbindung + DAOs + Tabellen anlegen
│   ├── FlughafenVerwaltung.java
│   ├── AirlineVerwaltung.java
│   ├── FlugzeugVerwaltung.java
│   ├── RouteVerwaltung.java
│   ├── FlugVerwaltung.java
│   ├── WartungVerwaltung.java
│   ├── Eingabe.java           # Helfer für Konsoleneingaben (Scanner)
│   └── Main.java              # Startpunkt: Menü + Beispieldaten
├── pom.xml                    # Maven-Build + Abhängigkeiten
├── Pflichtenheft.txt          # Pflichtenheft (Abgabe)
├── PROJEKT.txt                # Erklärung: Was macht das Programm?
├── STRUKTUR.txt               # Erklärung: Wie ist das Programm aufgebaut?
├── luftfahrt.db               # (wird beim Start automatisch erzeugt)
└── archive/                   # alte Projekte (Firma, infi_sqlite) – nicht Teil der Abgabe
```

Mehr Details zum Aufbau stehen in [`STRUKTUR.txt`](STRUKTUR.txt) und
[`PROJEKT.txt`](PROJEKT.txt).

---

## Datenbankstruktur

Sechs Tabellen, verbunden über Fremdschlüssel (FK):

- **FLUGHAFEN** `(id, name, iata_code, stadt, land, terminals, zeitzone)`
- **AIRLINE** `(id, basis_flughafen_id→FLUGHAFEN, name, icao_code, iata_code, hauptsitz, gruendungsjahr)`
- **FLUGZEUG** `(id, airline_id→AIRLINE, heimat_flughafen_id→FLUGHAFEN, kennzeichen, modell, hersteller, baujahr, sitzanzahl, flugstunden_gesamt)`
- **ROUTE** `(id, airline_id→AIRLINE, abflug_flughafen_id→FLUGHAFEN, ziel_flughafen_id→FLUGHAFEN, routennummer, distanz_km, aktiv)`
- **FLUG** `(id, flugzeug_id→FLUGZEUG, route_id→ROUTE, abflug_flughafen_id→FLUGHAFEN, ankunft_flughafen_id→FLUGHAFEN, flugnummer, geplanter_abflug, geplante_ankunft, tatsaechlicher_abflug, tatsaechliche_ankunft, status)`
- **WARTUNG** `(id, flugzeug_id→FLUGZEUG, flughafen_id→FLUGHAFEN, datum, typ, beschreibung, techniker, status, kosten)`

Die Tabellen werden von ORMLite automatisch aus den Klassen im Paket
`luftfahrt.model` erstellt – es muss kein SQL von Hand geschrieben werden.

---

## Erfüllung des Pflichtenhefts

| Anforderung | Art | Status |
|-------------|-----|--------|
| Verwaltung von Flugzeugen (Airline + Heimat-Flughafen) | Pflicht | ✅ erfüllt |
| Zuordnung von Airlines zu Flugzeugen | Pflicht | ✅ erfüllt |
| Flughäfen (IATA, Stadt, Land, Terminals, Zeiten) | Pflicht | ✅ erfüllt |
| SQLite-Datenbank | Pflicht | ✅ erfüllt |
| ORM über ORMLite | Pflicht | ✅ erfüllt |
| Konsolen-Menüführung | Pflicht | ✅ erfüllt |
| Suche | Wunsch | ✅ erfüllt |
| Wartungsverlauf | Wunsch | ✅ erfüllt |
| Grafische Benutzeroberfläche (GUI) | Wunsch | ⬜ offen |
| Logging | Wunsch | ⬜ offen |

---

## Archiv

Frühere, nicht mehr verwendete Projekte (`Firma`, `infi_sqlite`) und deren
Testdateien (CSV/JSON/`.db`) liegen im Ordner [`archive/`](archive/).
Sie sind **nicht** Teil der Abgabe und werden vom Build nicht kompiliert.
