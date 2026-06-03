package luftfahrt;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import luftfahrt.model.Airline;
import luftfahrt.model.Flughafen;
import luftfahrt.model.Route;

/**
 * Verwaltung der Routen.
 * Eine Route gehört einer Airline und verbindet einen Abflug- mit einem Ziel-Flughafen.
 */
public class RouteVerwaltung {

    private final Dao<Route, Integer> dao;
    private final Dao<Airline, Integer> airlineDao;
    private final Dao<Flughafen, Integer> flughafenDao;

    public RouteVerwaltung(Dao<Route, Integer> dao,
                           Dao<Airline, Integer> airlineDao,
                           Dao<Flughafen, Integer> flughafenDao) {
        this.dao = dao;
        this.airlineDao = airlineDao;
        this.flughafenDao = flughafenDao;
    }

    public void menue() {
        boolean weiter = true;
        while (weiter) {
            System.out.println("\n--- Routen ---");
            System.out.println(" [1] Anzeigen");
            System.out.println(" [2] Hinzufügen");
            System.out.println(" [3] Ändern");
            System.out.println(" [4] Löschen");
            System.out.println(" [5] Suchen (Routennummer)");
            System.out.println(" [0] Zurück");
            int wahl = Eingabe.zahl("Auswahl: ");
            try {
                switch (wahl) {
                    case 1 -> anzeigen();
                    case 2 -> hinzufuegen();
                    case 3 -> aendern();
                    case 4 -> loeschen();
                    case 5 -> suchen();
                    case 0 -> weiter = false;
                    default -> System.out.println("Ungültige Auswahl.");
                }
            } catch (SQLException e) {
                System.out.println("Datenbankfehler: " + e.getMessage());
            }
        }
    }

    public void anzeigen() throws SQLException {
        List<Route> liste = dao.queryForAll();
        System.out.println("\nRouten (" + liste.size() + "):");
        if (liste.isEmpty()) {
            System.out.println("  (keine Einträge)");
        }
        for (Route r : liste) {
            System.out.println("  " + r);
        }
    }

    public void hinzufuegen() throws SQLException {
        System.out.println("\nNeue Route anlegen:");
        Airline airline = waehleAirline();
        if (airline == null) {
            System.out.println("Abgebrochen: zuerst eine Airline anlegen.");
            return;
        }
        Flughafen ab = waehleFlughafen("Abflug-Flughafen");
        Flughafen ziel = waehleFlughafen("Ziel-Flughafen");
        if (ab == null || ziel == null) {
            System.out.println("Abgebrochen: zuerst Flughäfen anlegen.");
            return;
        }
        String nummer = Eingabe.text("Routennummer: ");
        int distanz = Eingabe.zahl("Distanz (km): ");
        boolean aktiv = Eingabe.jaNein("Aktiv?");

        Route r = new Route(airline, ab, ziel, nummer, distanz, aktiv);
        dao.create(r);
        System.out.println("Angelegt mit ID " + r.getId());
    }

    public void aendern() throws SQLException {
        int id = Eingabe.zahl("ID der zu ändernden Route: ");
        Route r = dao.queryForId(id);
        if (r == null) {
            System.out.println("Keine Route mit ID " + id + ".");
            return;
        }
        System.out.println("Aktuell: " + r);
        r.setRoutennummer(Eingabe.text("Neue Routennummer: "));
        r.setDistanzKm(Eingabe.zahl("Neue Distanz (km): "));
        r.setAktiv(Eingabe.jaNein("Aktiv?"));
        if (Eingabe.jaNein("Airline ändern?")) {
            Airline a = waehleAirline();
            if (a != null) {
                r.setAirline(a);
            }
        }
        if (Eingabe.jaNein("Abflug-Flughafen ändern?")) {
            Flughafen f = waehleFlughafen("Abflug-Flughafen");
            if (f != null) {
                r.setAbflugFlughafen(f);
            }
        }
        if (Eingabe.jaNein("Ziel-Flughafen ändern?")) {
            Flughafen f = waehleFlughafen("Ziel-Flughafen");
            if (f != null) {
                r.setZielFlughafen(f);
            }
        }
        dao.update(r);
        System.out.println("Geändert.");
    }

    public void loeschen() throws SQLException {
        int id = Eingabe.zahl("ID der zu löschenden Route: ");
        int anzahl = dao.deleteById(id);
        System.out.println(anzahl + " Eintrag/Einträge gelöscht.");
    }

    public void suchen() throws SQLException {
        String nummer = Eingabe.text("Routennummer: ");
        List<Route> liste = dao.queryForEq("routennummer", nummer);
        System.out.println("\nGefunden (" + liste.size() + "):");
        for (Route r : liste) {
            System.out.println("  " + r);
        }
    }

    private Airline waehleAirline() throws SQLException {
        List<Airline> liste = airlineDao.queryForAll();
        if (liste.isEmpty()) {
            return null;
        }
        System.out.println("Verfügbare Airlines:");
        for (Airline a : liste) {
            System.out.println("  " + a);
        }
        int id = Eingabe.zahl("Airline-ID wählen: ");
        return airlineDao.queryForId(id);
    }

    private Flughafen waehleFlughafen(String titel) throws SQLException {
        List<Flughafen> liste = flughafenDao.queryForAll();
        if (liste.isEmpty()) {
            return null;
        }
        System.out.println("Verfügbare Flughäfen (" + titel + "):");
        for (Flughafen f : liste) {
            System.out.println("  " + f);
        }
        int id = Eingabe.zahl(titel + " - Flughafen-ID wählen: ");
        return flughafenDao.queryForId(id);
    }
}
