package luftfahrt;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import luftfahrt.model.Airline;
import luftfahrt.model.Flughafen;

/**
 * Verwaltung der Airlines.
 * Eine Airline hat einen Basis-Flughafen (Fremdschlüssel), darum bekommt diese
 * Klasse auch den Flughafen-DAO, um den Basis-Flughafen auswählen zu können.
 */
public class AirlineVerwaltung {

    private final Dao<Airline, Integer> dao;
    private final Dao<Flughafen, Integer> flughafenDao;

    public AirlineVerwaltung(Dao<Airline, Integer> dao, Dao<Flughafen, Integer> flughafenDao) {
        this.dao = dao;
        this.flughafenDao = flughafenDao;
    }

    public void menue() {
        boolean weiter = true;
        while (weiter) {
            System.out.println("\n--- Airlines ---");
            System.out.println(" [1] Anzeigen");
            System.out.println(" [2] Hinzufügen");
            System.out.println(" [3] Ändern");
            System.out.println(" [4] Löschen");
            System.out.println(" [5] Suchen (Name)");
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
        List<Airline> liste = dao.queryForAll();
        System.out.println("\nAirlines (" + liste.size() + "):");
        if (liste.isEmpty()) {
            System.out.println("  (keine Einträge)");
        }
        for (Airline a : liste) {
            System.out.println("  " + a);
        }
    }

    public void hinzufuegen() throws SQLException {
        System.out.println("\nNeue Airline anlegen:");
        Flughafen basis = waehleFlughafen();
        if (basis == null) {
            System.out.println("Abgebrochen: zuerst einen Flughafen anlegen.");
            return;
        }
        String name = Eingabe.text("Name: ");
        String icao = Eingabe.text("ICAO-Code: ");
        String iata = Eingabe.text("IATA-Code: ");
        String hauptsitz = Eingabe.text("Hauptsitz: ");
        int jahr = Eingabe.zahl("Gründungsjahr: ");

        Airline a = new Airline(basis, name, icao, iata, hauptsitz, jahr);
        dao.create(a);
        System.out.println("Angelegt mit ID " + a.getId());
    }

    public void aendern() throws SQLException {
        int id = Eingabe.zahl("ID der zu ändernden Airline: ");
        Airline a = dao.queryForId(id);
        if (a == null) {
            System.out.println("Keine Airline mit ID " + id + ".");
            return;
        }
        System.out.println("Aktuell: " + a);
        a.setName(Eingabe.text("Neuer Name: "));
        a.setIcaoCode(Eingabe.text("Neuer ICAO-Code: "));
        a.setIataCode(Eingabe.text("Neuer IATA-Code: "));
        a.setHauptsitz(Eingabe.text("Neuer Hauptsitz: "));
        a.setGruendungsjahr(Eingabe.zahl("Neues Gründungsjahr: "));
        if (Eingabe.jaNein("Basis-Flughafen ändern?")) {
            Flughafen basis = waehleFlughafen();
            if (basis != null) {
                a.setBasisFlughafen(basis);
            }
        }
        dao.update(a);
        System.out.println("Geändert.");
    }

    public void loeschen() throws SQLException {
        int id = Eingabe.zahl("ID der zu löschenden Airline: ");
        int anzahl = dao.deleteById(id);
        System.out.println(anzahl + " Eintrag/Einträge gelöscht.");
    }

    public void suchen() throws SQLException {
        String name = Eingabe.text("Name: ");
        List<Airline> liste = dao.queryForEq("name", name);
        System.out.println("\nGefunden (" + liste.size() + "):");
        for (Airline a : liste) {
            System.out.println("  " + a);
        }
    }

    // Zeigt alle Flughäfen und lässt einen per ID auswählen
    private Flughafen waehleFlughafen() throws SQLException {
        List<Flughafen> liste = flughafenDao.queryForAll();
        if (liste.isEmpty()) {
            return null;
        }
        System.out.println("Verfügbare Flughäfen:");
        for (Flughafen f : liste) {
            System.out.println("  " + f);
        }
        int id = Eingabe.zahl("Flughafen-ID wählen: ");
        return flughafenDao.queryForId(id);
    }
}
