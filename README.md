# ✈️ Flugzeug-Verwaltungssystem

> Java-basiertes Verwaltungssystem für Flugzeuge, Airlines, Flughäfen und Wartung – powered by ORMLite & MySQL

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![ORMLite](https://img.shields.io/badge/ORMLite-ORM-6DB33F?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![License](https://img.shields.io/badge/Lizenz-MIT-blue?style=for-the-badge)

---

## 📋 Inhaltsverzeichnis

- [Projektbeschreibung](#projektbeschreibung)
- [Pflichtenheft](#pflichtenheft)
  - [Zielbestimmung](#zielbestimmung)
  - [Produkteinsatz](#produkteinsatz)
  - [Funktionale Anforderungen](#funktionale-anforderungen)
  - [Nicht-funktionale Anforderungen](#nicht-funktionale-anforderungen)
  - [Datenbankstruktur](#datenbankstruktur)
- [Technologiestack](#technologiestack)
- [Projektstruktur](#projektstruktur)
- [Installation & Setup](#installation--setup)
- [Datenbankeinrichtung](#datenbankeinrichtung)
- [Verwendung](#verwendung)
- [Klassenübersicht](#klassenübersicht)
- [Bekannte Einschränkungen](#bekannte-einschränkungen)
- [Autor](#autor)

---

## Projektbeschreibung

Das **Flugzeug-Verwaltungssystem** ist eine Java-Desktopanwendung zur strukturierten Verwaltung von Luftfahrtdaten. Es ermöglicht die vollständige Pflege von Flugzeugen, Airlines, Flughäfen sowie Wartungsaufträgen über eine relationale MySQL-Datenbank, die mithilfe von **ORMLite** als ORM-Framework angesprochen wird.

Das Projekt entstand im Rahmen des Unterrichts an der **HTL Innsbruck** als praktische Anwendung von Datenbankanbindung, objektorientiertem Design und Java-Entwicklung.

---

## Pflichtenheft

### Zielbestimmung

#### Mussziele (Must-Have)
| ID | Anforderung |
|----|-------------|
| M01 | Verwaltung von Flugzeugen (CRUD: Erstellen, Lesen, Aktualisieren, Löschen) |
| M02 | Verwaltung von Airlines mit Zuordnung zu Flugzeugen |
| M03 | Verwaltung von Flughäfen inkl. IATA-Code, Stadt und Land |
| M04 | Erfassung und Verwaltung von Wartungsaufträgen pro Flugzeug |
| M05 | Persistente Datenspeicherung in einer MySQL-Datenbank |
| M06 | ORM-Datenbankanbindung über ORMLite |
| M07 | Konsolenbasierte Benutzerinteraktion (Menüführung) |

#### Wunschziele (Nice-to-Have)
| ID | Anforderung |
|----|-------------|
| W01 | Grafische Benutzeroberfläche (Java Swing / JavaFX) |
| W02 | Filterung und Suche von Datensätzen |
| W03 | Export von Daten als CSV oder PDF |
| W04 | Wartungshistorie mit Statusverfolgung (offen / abgeschlossen) |
| W05 | Logging von Datenbankoperationen |

#### Abgrenzungskriterien (Out of Scope)
- Keine Echtzeit-Flugverfolgung oder Live-Daten
- Keine Netzwerkkommunikation / REST-API
- Keine Benutzerauthentifizierung / Rechteverwaltung

---

### Produkteinsatz

| Merkmal | Beschreibung |
|---------|-------------|
| **Zielgruppe** | Schüler, Entwickler, HTL-Unterricht |
| **Betriebssystem** | Windows / Linux / macOS |
| **Umgebung** | Lokale Entwicklungsumgebung (IDE oder Terminal) |
| **Voraussetzungen** | Java 17+, MySQL 8.0+, Maven |

---

### Funktionale Anforderungen

#### FA-01: Flugzeugverwaltung
- Anlegen eines neuen Flugzeugs mit: Kennzeichen, Typ/Modell, Baujahr, Sitzanzahl, Airline-Zuordnung
- Bearbeiten und Löschen vorhandener Flugzeuge
- Anzeige aller Flugzeuge als Liste

#### FA-02: Airline-Verwaltung
- Anlegen einer Airline mit: Name, ICAO-Code, Hauptsitz, Gründungsjahr
- Zuordnung von Flugzeugen zu einer Airline (1:N-Beziehung)
- Bearbeiten und Löschen von Airlines

#### FA-03: Flughafenverwaltung
- Anlegen eines Flughafens mit: Name, IATA-Code (3-stellig), Stadt, Land, Anzahl Terminals
- Bearbeiten und Löschen von Flughäfen

#### FA-04: Wartungsverwaltung
- Anlegen eines Wartungsauftrags für ein bestimmtes Flugzeug
- Felder: Datum, Beschreibung, Techniker, Status (OFFEN / ABGESCHLOSSEN), Kosten
- Anzeige aller Wartungsaufträge je Flugzeug

#### FA-05: Datenbankanbindung via ORMLite
- Automatische Tabellenerstellung beim Start (falls nicht vorhanden)
- Alle CRUD-Operationen über ORMLite-DAOs
- Transaktionssichere Operationen

---

### Nicht-funktionale Anforderungen

| Kategorie | Anforderung |
|-----------|-------------|
| **Wartbarkeit** | Klare Trennung in Model, DAO und Main (MVC-ähnlich) |
| **Lesbarkeit** | Javadoc-Kommentare bei allen öffentlichen Klassen und Methoden |
| **Robustheit** | Fehlerbehandlung bei DB-Verbindungsfehlern und ungültigen Eingaben |
| **Performance** | Antwortzeiten unter 1 Sekunde bei typischen Abfragen |
| **Portabilität** | Konfigurierbare DB-Verbindung über `config.properties` |

---

### Datenbankstruktur

```
┌──────────────┐       ┌──────────────────┐       ┌───────────────┐
│   airline    │       │    flugzeug      │       │   wartung     │
│──────────────│       │──────────────────│       │───────────────│
│ id (PK)      │──┐    │ id (PK)          │──┐    │ id (PK)       │
│ name         │  └───>│ airline_id (FK)  │  └───>│ flugzeug_id   │
│ icao_code    │       │ kennzeichen      │       │ datum         │
│ hauptsitz    │       │ modell           │       │ beschreibung  │
│ gruendung    │       │ baujahr          │       │ techniker     │
└──────────────┘       │ sitzanzahl       │       │ status        │
                       └──────────────────┘       │ kosten        │
                                                   └───────────────┘
┌──────────────────┐
│   flughafen      │
│──────────────────│
│ id (PK)          │
│ name             │
│ iata_code        │
│ stadt            │
│ land             │
│ terminals        │
└──────────────────┘
```

---

## Technologiestack

| Technologie | Version | Zweck |
|-------------|---------|-------|
| Java | 17+ | Programmiersprache |
| Maven | 3.8+ | Build-Tool & Abhängigkeitsverwaltung |
| MySQL | 8.0+ | Relationale Datenbank |
| ORMLite | 6.1 | ORM-Framework (Datenbankabstraktion) |
| MySQL Connector/J | 8.x | JDBC-Treiber |

### `pom.xml` – Abhängigkeiten

```xml
<dependencies>
    <!-- ORMLite Core -->
    <dependency>
        <groupId>com.j256.ormlite</groupId>
        <artifactId>ormlite-core</artifactId>
        <version>6.1</version>
    </dependency>

    <!-- ORMLite JDBC -->
    <dependency>
        <groupId>com.j256.ormlite</groupId>
        <artifactId>ormlite-jdbc</artifactId>
        <version>6.1</version>
    </dependency>

    <!-- MySQL JDBC Connector -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>8.3.0</version>
    </dependency>
</dependencies>
```

---

## Projektstruktur

```
flugzeug-verwaltung/
├── src/
│   └── main/
│       └── java/
│           └── at/htl/flugzeug/
│               ├── Main.java                  # Einstiegspunkt & Menüsteuerung
│               ├── DatabaseHelper.java        # ORMLite Verbindung & Setup
│               ├── model/
│               │   ├── Airline.java
│               │   ├── Flugzeug.java
│               │   ├── Flughafen.java
│               │   └── Wartung.java
│               └── dao/
│                   ├── AirlineDao.java
│                   ├── FlugzeugDao.java
│                   ├── FlughafenDao.java
│                   └── WartungDao.java
├── src/
│   └── main/
│       └── resources/
│           └── config.properties              # DB-Verbindungskonfiguration
├── pom.xml
└── README.md
```

---

## Installation & Setup

### 1. Repository klonen

```bash
git clone https://github.com/dein-username/flugzeug-verwaltung.git
cd flugzeug-verwaltung
```

### 2. Konfigurationsdatei anpassen

Datei: `src/main/resources/config.properties`

```properties
db.url=jdbc:mysql://localhost:3306/flugzeug_db
db.user=root
db.password=deinPasswort
```

### 3. Projekt bauen

```bash
mvn clean install
```

### 4. Anwendung starten

```bash
mvn exec:java -Dexec.mainClass="at.htl.flugzeug.Main"
```

---

## Datenbankeinrichtung

MySQL-Datenbank manuell anlegen (Tabellen werden automatisch von ORMLite erstellt):

```sql
CREATE DATABASE flugzeug_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

> ✅ ORMLite erstellt alle Tabellen beim ersten Start automatisch anhand der Model-Annotationen.

---

## Verwendung

Nach dem Start erscheint ein Konsolenmenü:

```
========================================
   ✈  FLUGZEUG-VERWALTUNGSSYSTEM  ✈
========================================
 [1] Flugzeuge verwalten
 [2] Airlines verwalten
 [3] Flughäfen verwalten
 [4] Wartungsaufträge verwalten
 [0] Beenden
========================================
Auswahl:
```

Jeder Menüpunkt öffnet ein Untermenü mit CRUD-Optionen (Anzeigen, Hinzufügen, Bearbeiten, Löschen).

---

## Klassenübersicht

### `Flugzeug.java` (Model-Beispiel)

```java
@DatabaseTable(tableName = "flugzeug")
public class Flugzeug {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "kennzeichen", unique = true, canBeNull = false)
    private String kennzeichen;

    @DatabaseField(columnName = "modell")
    private String modell;

    @DatabaseField(columnName = "baujahr")
    private int baujahr;

    @DatabaseField(columnName = "sitzanzahl")
    private int sitzanzahl;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Airline airline;

    // Getter & Setter ...
}
```

---

## Bekannte Einschränkungen

- Keine grafische Oberfläche (nur Konsolenbetrieb)
- Keine Benutzeranmeldung / Rechtemanagement
- Keine Unit-Tests in der aktuellen Version
- Datenbankverbindung muss manuell in `config.properties` konfiguriert werden

