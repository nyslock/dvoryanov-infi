package infi_sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

public class TestDB {
    private Connection conn;
    private final String dbName;

    public TestDB(String dbName) {
        this.dbName = dbName;
    }

    // Einheitliche Fehlerausgabe mit Zeitstempel
    private void logError(String msg, Exception e) {
        System.err.println("[" + LocalDateTime.now() + "] ❌ " + msg);
        System.err.println("   → " + e.getClass().getSimpleName() + ": " + e.getMessage());
    }

    public void connect() {
        try {
            String url = "jdbc:sqlite:" + dbName;
            conn = DriverManager.getConnection(url);
            System.out.println("✅ Verbindung hergestellt zu " + dbName);
        } catch (SQLException e) {
            logError("Fehler beim Verbinden zur Datenbank.", e);
        }
    }

    public void create() {
        if (conn == null) connect();

        String sqlCreate = """
                CREATE TABLE IF NOT EXISTS schueler (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    nachname TEXT NOT NULL,
                    age INTEGER NOT NULL,
                    klasse TEXT NOT NULL,
                    wohnort TEXT NOT NULL,
                    punkte INTEGER,
                    kommentar TEXT,
                    geburtsdatum DATE,
                    anmeldezeit TIME,
                    letzter_login DATETIME
                )
                """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sqlCreate);
            System.out.println("🧱 Tabelle 'schueler' erstellt oder bereits vorhanden.");
        } catch (SQLException e) {
            logError("Fehler beim Erstellen der Tabelle.", e);
        }
    }

    public void addSchueler(String name, String nachname, int age, String klasse, String wohnort) {
        String sqlInsert = "INSERT INTO schueler(name, nachname, age, klasse, wohnort) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
            pstmt.setString(1, name);
            pstmt.setString(2, nachname);
            pstmt.setInt(3, age);
            pstmt.setString(4, klasse);
            pstmt.setString(5, wohnort);
            pstmt.executeUpdate();
            System.out.println("➕ Schüler hinzugefügt: " + name + " " + nachname);
        } catch (SQLException e) {
            logError("Fehler beim Hinzufügen des Schülers.", e);
        } catch (NullPointerException e) {
            logError("Verbindung zur Datenbank fehlt. Bitte zuerst connect() oder create() aufrufen.", e);
        }
    }

    private void setWert(String spaltenname, int id, Object wert) {
        String sql = "UPDATE schueler SET " + spaltenname + " = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, wert);
            pstmt.setInt(2, id);
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) {
                System.out.println("⚠️ Kein Eintrag mit ID " + id + " gefunden.");
            } else {
                System.out.println("✏️ Updated: " + spaltenname + " für ID " + id + " → " + wert);
            }
        } catch (SQLException e) {
            logError("Fehler beim Aktualisieren von '" + spaltenname + "'", e);
        }
    }

    public void setName(int id, String neuerName) { setWert("name", id, neuerName); }
    public void setNachname(int id, String neuerNachname) { setWert("nachname", id, neuerNachname); }
    public void setAge(int id, int neuesAge) { setWert("age", id, neuesAge); }
    public void setKlasse(int id, String neueKlasse) { setWert("klasse", id, neueKlasse); }
    public void setWohnort(int id, String neuerWohnort) { setWert("wohnort", id, neuerWohnort); }

    public void showAll() {
        String sql = "SELECT * FROM schueler";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%2d | %-10s %-12s | %2d | %-5s | %-10s%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("nachname"),
                        rs.getInt("age"),
                        rs.getString("klasse"),
                        rs.getString("wohnort"));
            }
            if (!found) System.out.println("ℹ️ Keine Schüler gefunden.");
        } catch (SQLException e) {
            logError("Fehler beim Abrufen der Schülerdaten.", e);
        } catch (NullPointerException e) {
            logError("Verbindung zur Datenbank fehlt. Bitte zuerst connect() aufrufen.", e);
        }
    }

    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("🔒 Datenbankverbindung geschlossen.");
            }
        } catch (SQLException e) {
            logError("Fehler beim Schließen der Datenbankverbindung.", e);
        }
    }

    public static void main(String[] args) {
        TestDB db = new TestDB("schule1.db");

        db.create();

        db.addSchueler("Max", "Mustermann", 16, "10a", "Berlin");
        db.addSchueler("Anna", "Musterfrau", 17, "11b", "Hamburg");

        db.showAll();

        db.setName(1, "Maximilian");
        db.setWohnort(1, "München");
        db.setAge(1, 17);

        db.showAll();

        db.close();
    }
}
