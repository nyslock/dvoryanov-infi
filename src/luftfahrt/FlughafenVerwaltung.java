package luftfahrt;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import luftfahrt.model.Flughafen;

/**
 * Verwaltung der Flughäfen.
 * Aufbau wie ArtikelVerwaltung: der DAO wird im Konstruktor übergeben,
 * dann gibt es Methoden zum Anzeigen / Hinzufügen / Ändern / Löschen / Suchen.
 * Statt SQL benutzen wir den ORMLite-DAO.
 */
public class FlughafenVerwaltung {

    private final Dao<Flughafen, Integer> dao;

    public FlughafenVerwaltung(Dao<Flughafen, Integer> dao) {
        this.dao = dao;
    }

    // Untermenü für diesen Bereich
    public void menue() {
        boolean weiter = true;
        while (weiter) {
            System.out.println("\n--- Flughäfen ---");
            System.out.println(" [1] Anzeigen");
            System.out.println(" [2] Hinzufügen");
            System.out.println(" [3] Ändern");
            System.out.println(" [4] Löschen");
            System.out.println(" [5] Suchen (Stadt)");
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
        List<Flughafen> liste = dao.queryForAll();
        System.out.println("\nFlughäfen (" + liste.size() + "):");
        if (liste.isEmpty()) {
            System.out.println("  (keine Einträge)");
        }
        for (Flughafen f : liste) {
            System.out.println("  " + f);
        }
    }

    public void hinzufuegen() throws SQLException {
        System.out.println("\nNeuen Flughafen anlegen:");
        String name = Eingabe.text("Name: ");
        String iata = Eingabe.text("IATA-Code: ");
        String stadt = Eingabe.text("Stadt: ");
        String land = Eingabe.text("Land: ");
        int terminals = Eingabe.zahl("Terminals: ");
        String zeitzone = Eingabe.text("Zeitzone: ");

        Flughafen f = new Flughafen(name, iata, stadt, land, terminals, zeitzone);
        dao.create(f);
        System.out.println("Angelegt mit ID " + f.getId());
    }

    public void aendern() throws SQLException {
        int id = Eingabe.zahl("ID des zu ändernden Flughafens: ");
        Flughafen f = dao.queryForId(id);
        if (f == null) {
            System.out.println("Kein Flughafen mit ID " + id + ".");
            return;
        }
        System.out.println("Aktuell: " + f);
        f.setName(Eingabe.text("Neuer Name [" + f.getName() + "]: ")); // einfache Variante: immer überschreiben
        f.setIataCode(Eingabe.text("Neuer IATA-Code [" + f.getIataCode() + "]: "));
        f.setStadt(Eingabe.text("Neue Stadt [" + f.getStadt() + "]: "));
        f.setLand(Eingabe.text("Neues Land [" + f.getLand() + "]: "));
        f.setTerminals(Eingabe.zahl("Neue Terminalanzahl [" + f.getTerminals() + "]: "));
        f.setZeitzone(Eingabe.text("Neue Zeitzone [" + f.getZeitzone() + "]: "));
        dao.update(f);
        System.out.println("Geändert.");
    }

    public void loeschen() throws SQLException {
        int id = Eingabe.zahl("ID des zu löschenden Flughafens: ");
        int anzahl = dao.deleteById(id);
        System.out.println(anzahl + " Eintrag/Einträge gelöscht.");
    }

    public void suchen() throws SQLException {
        String stadt = Eingabe.text("Stadt: ");
        List<Flughafen> liste = dao.queryForEq("stadt", stadt);
        System.out.println("\nGefunden (" + liste.size() + "):");
        for (Flughafen f : liste) {
            System.out.println("  " + f);
        }
    }
}
