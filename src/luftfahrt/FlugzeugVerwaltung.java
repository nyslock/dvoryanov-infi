package luftfahrt;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import luftfahrt.model.Airline;
import luftfahrt.model.Flughafen;
import luftfahrt.model.Flugzeug;

/**
 * Verwaltung der Flugzeuge.
 * Ein Flugzeug gehört einer Airline und hat einen Heimat-Flughafen (zwei Fremdschlüssel).
 */
public class FlugzeugVerwaltung {

    private final Dao<Flugzeug, Integer> dao;
    private final Dao<Airline, Integer> airlineDao;
    private final Dao<Flughafen, Integer> flughafenDao;

    public FlugzeugVerwaltung(Dao<Flugzeug, Integer> dao,
                              Dao<Airline, Integer> airlineDao,
                              Dao<Flughafen, Integer> flughafenDao) {
        this.dao = dao;
        this.airlineDao = airlineDao;
        this.flughafenDao = flughafenDao;
    }

    public void menue() {
        boolean weiter = true;
        while (weiter) {
            System.out.println("\n--- Flugzeuge ---");
            System.out.println(" [1] Anzeigen");
            System.out.println(" [2] Hinzufügen");
            System.out.println(" [3] Ändern");
            System.out.println(" [4] Löschen");
            System.out.println(" [5] Suchen (Kennzeichen)");
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
        List<Flugzeug> liste = dao.queryForAll();
        System.out.println("\nFlugzeuge (" + liste.size() + "):");
        if (liste.isEmpty()) {
            System.out.println("  (keine Einträge)");
        }
        for (Flugzeug f : liste) {
            System.out.println("  " + f);
        }
    }

    public void hinzufuegen() throws SQLException {
        System.out.println("\nNeues Flugzeug anlegen:");
        Airline airline = waehleAirline();
        if (airline == null) {
            System.out.println("Abgebrochen: zuerst eine Airline anlegen.");
            return;
        }
        Flughafen heimat = waehleFlughafen("Heimat-Flughafen");
        if (heimat == null) {
            System.out.println("Abgebrochen: zuerst einen Flughafen anlegen.");
            return;
        }
        String kennzeichen = Eingabe.text("Kennzeichen: ");
        String modell = Eingabe.text("Modell: ");
        String hersteller = Eingabe.text("Hersteller: ");
        int baujahr = Eingabe.zahl("Baujahr: ");
        int sitze = Eingabe.zahl("Sitzanzahl: ");
        double stunden = Eingabe.kommazahl("Flugstunden gesamt: ");

        Flugzeug f = new Flugzeug(airline, heimat, kennzeichen, modell, hersteller, baujahr, sitze, stunden);
        dao.create(f);
        System.out.println("Angelegt mit ID " + f.getId());
    }

    public void aendern() throws SQLException {
        int id = Eingabe.zahl("ID des zu ändernden Flugzeugs: ");
        Flugzeug f = dao.queryForId(id);
        if (f == null) {
            System.out.println("Kein Flugzeug mit ID " + id + ".");
            return;
        }
        System.out.println("Aktuell: " + f);
        f.setKennzeichen(Eingabe.text("Neues Kennzeichen: "));
        f.setModell(Eingabe.text("Neues Modell: "));
        f.setHersteller(Eingabe.text("Neuer Hersteller: "));
        f.setBaujahr(Eingabe.zahl("Neues Baujahr: "));
        f.setSitzanzahl(Eingabe.zahl("Neue Sitzanzahl: "));
        f.setFlugstundenGesamt(Eingabe.kommazahl("Neue Flugstunden gesamt: "));
        if (Eingabe.jaNein("Airline ändern?")) {
            Airline a = waehleAirline();
            if (a != null) {
                f.setAirline(a);
            }
        }
        if (Eingabe.jaNein("Heimat-Flughafen ändern?")) {
            Flughafen h = waehleFlughafen("Heimat-Flughafen");
            if (h != null) {
                f.setHeimatFlughafen(h);
            }
        }
        dao.update(f);
        System.out.println("Geändert.");
    }

    public void loeschen() throws SQLException {
        int id = Eingabe.zahl("ID des zu löschenden Flugzeugs: ");
        int anzahl = dao.deleteById(id);
        System.out.println(anzahl + " Eintrag/Einträge gelöscht.");
    }

    public void suchen() throws SQLException {
        String kennzeichen = Eingabe.text("Kennzeichen: ");
        List<Flugzeug> liste = dao.queryForEq("kennzeichen", kennzeichen);
        System.out.println("\nGefunden (" + liste.size() + "):");
        for (Flugzeug f : liste) {
            System.out.println("  " + f);
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
