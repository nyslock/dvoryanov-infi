package luftfahrt;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import luftfahrt.model.Flug;
import luftfahrt.model.Flughafen;
import luftfahrt.model.Flugzeug;
import luftfahrt.model.Route;

/**
 * Verwaltung der Flüge.
 * Ein Flug wird von einem Flugzeug durchgeführt, folgt einer Route und hat
 * einen Abflug- und einen Ankunfts-Flughafen.
 */
public class FlugVerwaltung {

    private final Dao<Flug, Integer> dao;
    private final Dao<Flugzeug, Integer> flugzeugDao;
    private final Dao<Route, Integer> routeDao;
    private final Dao<Flughafen, Integer> flughafenDao;

    public FlugVerwaltung(Dao<Flug, Integer> dao,
                          Dao<Flugzeug, Integer> flugzeugDao,
                          Dao<Route, Integer> routeDao,
                          Dao<Flughafen, Integer> flughafenDao) {
        this.dao = dao;
        this.flugzeugDao = flugzeugDao;
        this.routeDao = routeDao;
        this.flughafenDao = flughafenDao;
    }

    public void menue() {
        boolean weiter = true;
        while (weiter) {
            System.out.println("\n--- Flüge ---");
            System.out.println(" [1] Anzeigen");
            System.out.println(" [2] Hinzufügen");
            System.out.println(" [3] Ändern");
            System.out.println(" [4] Löschen");
            System.out.println(" [5] Suchen (Flugnummer)");
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
        List<Flug> liste = dao.queryForAll();
        System.out.println("\nFlüge (" + liste.size() + "):");
        if (liste.isEmpty()) {
            System.out.println("  (keine Einträge)");
        }
        for (Flug f : liste) {
            System.out.println("  " + f);
        }
    }

    public void hinzufuegen() throws SQLException {
        System.out.println("\nNeuen Flug anlegen:");
        Flugzeug flugzeug = waehleFlugzeug();
        if (flugzeug == null) {
            System.out.println("Abgebrochen: zuerst ein Flugzeug anlegen.");
            return;
        }
        Route route = waehleRoute();
        if (route == null) {
            System.out.println("Abgebrochen: zuerst eine Route anlegen.");
            return;
        }
        Flughafen ab = waehleFlughafen("Abflug-Flughafen");
        Flughafen an = waehleFlughafen("Ankunfts-Flughafen");
        if (ab == null || an == null) {
            System.out.println("Abgebrochen: zuerst Flughäfen anlegen.");
            return;
        }
        String nummer = Eingabe.text("Flugnummer: ");
        var geplAb = Eingabe.datum("Geplanter Abflug");
        var geplAn = Eingabe.datum("Geplante Ankunft");
        String status = Eingabe.text("Status (z. B. geplant/gestartet/gelandet): ");

        Flug f = new Flug(flugzeug, route, ab, an, nummer, geplAb, geplAn, status);
        dao.create(f);
        System.out.println("Angelegt mit ID " + f.getId());
    }

    public void aendern() throws SQLException {
        int id = Eingabe.zahl("ID des zu ändernden Flugs: ");
        Flug f = dao.queryForId(id);
        if (f == null) {
            System.out.println("Kein Flug mit ID " + id + ".");
            return;
        }
        System.out.println("Aktuell: " + f);
        f.setFlugnummer(Eingabe.text("Neue Flugnummer: "));
        f.setStatus(Eingabe.text("Neuer Status: "));
        if (Eingabe.jaNein("Geplante Zeiten ändern?")) {
            f.setGeplanterAbflug(Eingabe.datum("Geplanter Abflug"));
            f.setGeplanteAnkunft(Eingabe.datum("Geplante Ankunft"));
        }
        if (Eingabe.jaNein("Tatsächliche Zeiten eintragen?")) {
            f.setTatsaechlicherAbflug(Eingabe.datum("Tatsächlicher Abflug"));
            f.setTatsaechlicheAnkunft(Eingabe.datum("Tatsächliche Ankunft"));
        }
        if (Eingabe.jaNein("Flugzeug ändern?")) {
            Flugzeug fz = waehleFlugzeug();
            if (fz != null) {
                f.setFlugzeug(fz);
            }
        }
        if (Eingabe.jaNein("Route ändern?")) {
            Route r = waehleRoute();
            if (r != null) {
                f.setRoute(r);
            }
        }
        dao.update(f);
        System.out.println("Geändert.");
    }

    public void loeschen() throws SQLException {
        int id = Eingabe.zahl("ID des zu löschenden Flugs: ");
        int anzahl = dao.deleteById(id);
        System.out.println(anzahl + " Eintrag/Einträge gelöscht.");
    }

    public void suchen() throws SQLException {
        String nummer = Eingabe.text("Flugnummer: ");
        List<Flug> liste = dao.queryForEq("flugnummer", nummer);
        System.out.println("\nGefunden (" + liste.size() + "):");
        for (Flug f : liste) {
            System.out.println("  " + f);
        }
    }

    private Flugzeug waehleFlugzeug() throws SQLException {
        List<Flugzeug> liste = flugzeugDao.queryForAll();
        if (liste.isEmpty()) {
            return null;
        }
        System.out.println("Verfügbare Flugzeuge:");
        for (Flugzeug f : liste) {
            System.out.println("  " + f);
        }
        int id = Eingabe.zahl("Flugzeug-ID wählen: ");
        return flugzeugDao.queryForId(id);
    }

    private Route waehleRoute() throws SQLException {
        List<Route> liste = routeDao.queryForAll();
        if (liste.isEmpty()) {
            return null;
        }
        System.out.println("Verfügbare Routen:");
        for (Route r : liste) {
            System.out.println("  " + r);
        }
        int id = Eingabe.zahl("Route-ID wählen: ");
        return routeDao.queryForId(id);
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
