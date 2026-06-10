package luftfahrt;

import java.util.Calendar;
import java.util.Date;

import luftfahrt.model.Airline;
import luftfahrt.model.Flug;
import luftfahrt.model.Flughafen;
import luftfahrt.model.Flugzeug;
import luftfahrt.model.Route;
import luftfahrt.model.Wartung;

/**
 * Startklasse mit dem Hauptmenü (Konsole).
 * Verbindet die Datenbank, erstellt die Verwaltungen und zeigt das Menü an.
 */
public class Main {

    public static void main(String[] args) {
        Datenbank db = new Datenbank();
        try {
            db.verbinden();

            // Verwaltungen erzeugen (bekommen die benötigten DAOs)
            FlughafenVerwaltung flughaefen = new FlughafenVerwaltung(db.getFlughafenDao());
            AirlineVerwaltung airlines = new AirlineVerwaltung(db.getAirlineDao(), db.getFlughafenDao());
            FlugzeugVerwaltung flugzeuge = new FlugzeugVerwaltung(
                    db.getFlugzeugDao(), db.getAirlineDao(), db.getFlughafenDao());
            RouteVerwaltung routen = new RouteVerwaltung(
                    db.getRouteDao(), db.getAirlineDao(), db.getFlughafenDao());
            FlugVerwaltung fluege = new FlugVerwaltung(
                    db.getFlugDao(), db.getFlugzeugDao(), db.getRouteDao(), db.getFlughafenDao());
            WartungVerwaltung wartungen = new WartungVerwaltung(
                    db.getWartungDao(), db.getFlugzeugDao(), db.getFlughafenDao());

            // Beim ersten Start ein paar Beispieldaten anlegen
            beispieldaten(db);

            boolean laeuft = true;
            while (laeuft) {
                System.out.println("\n========================================");
                System.out.println(" [1] Flugzeuge verwalten");
                System.out.println(" [2] Airlines verwalten");
                System.out.println(" [3] Flughäfen verwalten");
                System.out.println(" [4] Routen verwalten");
                System.out.println(" [5] Flüge verwalten");
                System.out.println(" [6] Wartungsaufträge verwalten");
                System.out.println(" [0] Beenden");
                System.out.println("========================================");
                int wahl = Eingabe.zahl("Auswahl: ");
                switch (wahl) {
                    case 1 -> flugzeuge.menue();
                    case 2 -> airlines.menue();
                    case 3 -> flughaefen.menue();
                    case 4 -> routen.menue();
                    case 5 -> fluege.menue();
                    case 6 -> wartungen.menue();
                    case 0 -> laeuft = false;
                    default -> System.out.println("Ungültige Auswahl.");
                }
            }

            System.out.println("Programm wird beendet.");
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                db.schliessen();
            } catch (Exception e) {
                System.out.println("Fehler beim Schließen: " + e.getMessage());
            }
        }
    }

    // Legt nur dann Beispieldaten an, wenn die Flughafen-Tabelle leer ist.
    // So bekommt jeder neue Nutzer beim ersten Start sofort Testdaten in allen
    // sechs Tabellen (Flughäfen, Airlines, Flugzeuge, Routen, Flüge, Wartungen).
    private static void beispieldaten(Datenbank db) throws Exception {
        if (db.getFlughafenDao().countOf() > 0) {
            return;
        }
        System.out.println("Lege Beispieldaten an...");

        // --- Flughäfen ---
        Flughafen wien = new Flughafen("Wien-Schwechat", "VIE", "Wien", "Österreich", 3, "Europe/Vienna");
        Flughafen belgrad = new Flughafen("Belgrad Nikola Tesla", "BEG", "Belgrad", "Serbien", 2, "Europe/Belgrade");
        Flughafen budapest = new Flughafen("Budapest Ferenc Liszt", "BUD", "Budapest", "Ungarn", 2, "Europe/Budapest");
        Flughafen muenchen = new Flughafen("München Franz Josef Strauß", "MUC", "München", "Deutschland", 2, "Europe/Berlin");
        Flughafen zuerich = new Flughafen("Zürich Kloten", "ZRH", "Zürich", "Schweiz", 3, "Europe/Zurich");
        db.getFlughafenDao().create(wien);
        db.getFlughafenDao().create(belgrad);
        db.getFlughafenDao().create(budapest);
        db.getFlughafenDao().create(muenchen);
        db.getFlughafenDao().create(zuerich);

        // --- Airlines (jede mit Basis-Flughafen) ---
        Airline wizz = new Airline(belgrad, "Wizz Air", "WZZ", "W6", "Budapest", 2003);
        Airline austrian = new Airline(wien, "Austrian Airlines", "AUA", "OS", "Wien", 1957);
        Airline lufthansa = new Airline(muenchen, "Lufthansa", "DLH", "LH", "Köln", 1953);
        db.getAirlineDao().create(wizz);
        db.getAirlineDao().create(austrian);
        db.getAirlineDao().create(lufthansa);

        // --- Flugzeuge (gehören einer Airline + Heimat-Flughafen) ---
        Flugzeug a320 = new Flugzeug(wizz, belgrad, "HA-LWA", "A320", "Airbus", 2015, 180, 12000.5);
        Flugzeug a321 = new Flugzeug(wizz, budapest, "HA-LXK", "A321", "Airbus", 2018, 230, 8200.0);
        Flugzeug b777 = new Flugzeug(austrian, wien, "OE-LPA", "B777-200", "Boeing", 2010, 308, 41000.0);
        Flugzeug a350 = new Flugzeug(lufthansa, muenchen, "D-AIXA", "A350-900", "Airbus", 2017, 293, 22500.75);
        db.getFlugzeugDao().create(a320);
        db.getFlugzeugDao().create(a321);
        db.getFlugzeugDao().create(b777);
        db.getFlugzeugDao().create(a350);

        // --- Routen (verbinden zwei Flughäfen, gehören einer Airline) ---
        Route r1 = new Route(wizz, belgrad, wien, "W6-1001", 489, true);
        Route r2 = new Route(wizz, budapest, wien, "W6-2002", 214, true);
        Route r3 = new Route(austrian, wien, zuerich, "OS-561", 600, true);
        Route r4 = new Route(lufthansa, muenchen, wien, "LH-1882", 355, false);
        db.getRouteDao().create(r1);
        db.getRouteDao().create(r2);
        db.getRouteDao().create(r3);
        db.getRouteDao().create(r4);

        // --- Flüge (konkrete Flüge: Flugzeug folgt einer Route) ---
        Flug f1 = new Flug(a320, r1, belgrad, wien, "W6-1001-0701",
                datumZeit(2026, 7, 1, 8, 30), datumZeit(2026, 7, 1, 9, 45), "geplant");
        Flug f2 = new Flug(a321, r2, budapest, wien, "W6-2002-0701",
                datumZeit(2026, 7, 1, 12, 0), datumZeit(2026, 7, 1, 12, 50), "geplant");
        Flug f3 = new Flug(b777, r3, wien, zuerich, "OS-561-0702",
                datumZeit(2026, 7, 2, 7, 15), datumZeit(2026, 7, 2, 8, 40), "durchgeführt");
        db.getFlugDao().create(f1);
        db.getFlugDao().create(f2);
        db.getFlugDao().create(f3);

        // --- Wartungen (Wartungsverlauf je Flugzeug) ---
        Wartung w1 = new Wartung(a320, belgrad, datum(2026, 5, 20), "Inspektion",
                "Routinekontrolle Triebwerk", "M. Petrović", "erledigt", 3500.0);
        Wartung w2 = new Wartung(a320, wien, datum(2026, 6, 15), "Reparatur",
                "Austausch Bremsbeläge Fahrwerk", "K. Huber", "offen", 1800.0);
        Wartung w3 = new Wartung(b777, wien, datum(2026, 4, 10), "Großcheck",
                "C-Check komplette Zelle", "T. Bauer", "erledigt", 42000.0);
        db.getWartungDao().create(w1);
        db.getWartungDao().create(w2);
        db.getWartungDao().create(w3);

        System.out.println("Beispieldaten angelegt: "
                + "5 Flughäfen, 3 Airlines, 4 Flugzeuge, 4 Routen, 3 Flüge, 3 Wartungen.");
    }

    // Hilfsmethode: erzeugt ein Datum (Monat 1-12)
    private static Date datum(int jahr, int monat, int tag) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(jahr, monat - 1, tag);
        return c.getTime();
    }

    // Hilfsmethode: erzeugt Datum + Uhrzeit (für Abflug-/Ankunftszeiten der Flüge)
    private static Date datumZeit(int jahr, int monat, int tag, int stunde, int minute) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(jahr, monat - 1, tag, stunde, minute);
        return c.getTime();
    }
}
