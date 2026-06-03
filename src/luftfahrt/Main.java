package luftfahrt;

import java.util.Calendar;
import java.util.Date;

import luftfahrt.model.Airline;
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

    // Legt nur dann Beispieldaten an, wenn die Flughafen-Tabelle leer ist
    private static void beispieldaten(Datenbank db) throws Exception {
        if (db.getFlughafenDao().countOf() > 0) {
            return;
        }
        System.out.println("Lege Beispieldaten an...");

        Flughafen wien = new Flughafen("Wien-Schwechat", "VIE", "Wien", "Österreich", 3, "Europe/Vienna");
        Flughafen belgrad = new Flughafen("Belgrad Nikola Tesla", "BEG", "Belgrad", "Serbien", 2, "Europe/Belgrade");
        db.getFlughafenDao().create(wien);
        db.getFlughafenDao().create(belgrad);

        Airline wizz = new Airline(belgrad, "Wizz Air", "WZZ", "W6", "Budapest", 2003);
        db.getAirlineDao().create(wizz);

        Flugzeug a320 = new Flugzeug(wizz, belgrad, "HA-LWA", "A320", "Airbus", 2015, 180, 12000.5);
        db.getFlugzeugDao().create(a320);

        Route r1 = new Route(wizz, belgrad, wien, "W6-1001", 489, true);
        db.getRouteDao().create(r1);

        Wartung w1 = new Wartung(a320, belgrad, datum(2026, 5, 20), "Inspektion",
                "Routinekontrolle Triebwerk", "M. Petrović", "erledigt", 3500.0);
        db.getWartungDao().create(w1);

        System.out.println("Beispieldaten angelegt.");
    }

    // Hilfsmethode: erzeugt ein Datum (Monat 1-12)
    private static Date datum(int jahr, int monat, int tag) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(jahr, monat - 1, tag);
        return c.getTime();
    }
}
