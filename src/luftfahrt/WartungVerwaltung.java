package luftfahrt;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import luftfahrt.model.Flughafen;
import luftfahrt.model.Flugzeug;
import luftfahrt.model.Wartung;

/**
 * Verwaltung der Wartungsaufträge.
 * Eine Wartung gehört einem Flugzeug und findet an einem Flughafen statt.
 * Über die Suche nach Flugzeug erhält man den "Wartungsverlauf" eines Flugzeugs.
 */
public class WartungVerwaltung {

    private final Dao<Wartung, Integer> dao;
    private final Dao<Flugzeug, Integer> flugzeugDao;
    private final Dao<Flughafen, Integer> flughafenDao;

    public WartungVerwaltung(Dao<Wartung, Integer> dao,
                             Dao<Flugzeug, Integer> flugzeugDao,
                             Dao<Flughafen, Integer> flughafenDao) {
        this.dao = dao;
        this.flugzeugDao = flugzeugDao;
        this.flughafenDao = flughafenDao;
    }

    public void menue() {
        boolean weiter = true;
        while (weiter) {
            System.out.println("\n--- Wartungsaufträge ---");
            System.out.println(" [1] Anzeigen");
            System.out.println(" [2] Hinzufügen");
            System.out.println(" [3] Ändern");
            System.out.println(" [4] Löschen");
            System.out.println(" [5] Wartungsverlauf eines Flugzeugs");
            System.out.println(" [0] Zurück");
            int wahl = Eingabe.zahl("Auswahl: ");
            try {
                switch (wahl) {
                    case 1 -> anzeigen();
                    case 2 -> hinzufuegen();
                    case 3 -> aendern();
                    case 4 -> loeschen();
                    case 5 -> verlauf();
                    case 0 -> weiter = false;
                    default -> System.out.println("Ungültige Auswahl.");
                }
            } catch (SQLException e) {
                System.out.println("Datenbankfehler: " + e.getMessage());
            }
        }
    }

    public void anzeigen() throws SQLException {
        List<Wartung> liste = dao.queryForAll();
        System.out.println("\nWartungsaufträge (" + liste.size() + "):");
        if (liste.isEmpty()) {
            System.out.println("  (keine Einträge)");
        }
        for (Wartung w : liste) {
            System.out.println("  " + w);
        }
    }

    public void hinzufuegen() throws SQLException {
        System.out.println("\nNeuen Wartungsauftrag anlegen:");
        Flugzeug flugzeug = waehleFlugzeug();
        if (flugzeug == null) {
            System.out.println("Abgebrochen: zuerst ein Flugzeug anlegen.");
            return;
        }
        Flughafen flughafen = waehleFlughafen();
        if (flughafen == null) {
            System.out.println("Abgebrochen: zuerst einen Flughafen anlegen.");
            return;
        }
        var datum = Eingabe.datum("Datum");
        String typ = Eingabe.text("Typ (z. B. Inspektion/Reparatur): ");
        String beschreibung = Eingabe.text("Beschreibung: ");
        String techniker = Eingabe.text("Techniker: ");
        String status = Eingabe.text("Status (z. B. offen/erledigt): ");
        double kosten = Eingabe.kommazahl("Kosten (EUR): ");

        Wartung w = new Wartung(flugzeug, flughafen, datum, typ, beschreibung, techniker, status, kosten);
        dao.create(w);
        System.out.println("Angelegt mit ID " + w.getId());
    }

    public void aendern() throws SQLException {
        int id = Eingabe.zahl("ID der zu ändernden Wartung: ");
        Wartung w = dao.queryForId(id);
        if (w == null) {
            System.out.println("Keine Wartung mit ID " + id + ".");
            return;
        }
        System.out.println("Aktuell: " + w);
        if (Eingabe.jaNein("Datum ändern?")) {
            w.setDatum(Eingabe.datum("Datum"));
        }
        w.setTyp(Eingabe.text("Neuer Typ: "));
        w.setBeschreibung(Eingabe.text("Neue Beschreibung: "));
        w.setTechniker(Eingabe.text("Neuer Techniker: "));
        w.setStatus(Eingabe.text("Neuer Status: "));
        w.setKosten(Eingabe.kommazahl("Neue Kosten (EUR): "));
        if (Eingabe.jaNein("Flugzeug ändern?")) {
            Flugzeug fz = waehleFlugzeug();
            if (fz != null) {
                w.setFlugzeug(fz);
            }
        }
        if (Eingabe.jaNein("Flughafen ändern?")) {
            Flughafen fh = waehleFlughafen();
            if (fh != null) {
                w.setFlughafen(fh);
            }
        }
        dao.update(w);
        System.out.println("Geändert.");
    }

    public void loeschen() throws SQLException {
        int id = Eingabe.zahl("ID der zu löschenden Wartung: ");
        int anzahl = dao.deleteById(id);
        System.out.println(anzahl + " Eintrag/Einträge gelöscht.");
    }

    // Wartungsverlauf: alle Wartungen zu einem Flugzeug
    public void verlauf() throws SQLException {
        Flugzeug flugzeug = waehleFlugzeug();
        if (flugzeug == null) {
            System.out.println("Keine Flugzeuge vorhanden.");
            return;
        }
        List<Wartung> liste = dao.queryForEq("flugzeug_id", flugzeug.getId());
        System.out.println("\nWartungsverlauf für " + flugzeug.getKennzeichen() + " (" + liste.size() + "):");
        for (Wartung w : liste) {
            System.out.println("  " + w);
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
