package infi_sqlite;

import java.sql.*;
import java.util.Random;

public class SQLiteManager {

    // 💾 Pfad zur SQLite-Datenbankdatei (wird automatisch erstellt, falls nicht vorhanden)
    private static final String DB_URL = "jdbc:sqlite:database2.db";

    public static void main(String[] args) {
        try {
            createTable();           // 1️⃣ Tabelle anlegen
            insertRandomData(20);    // 2️⃣ Zufallsdaten einfügen
            countAndPrintValues();   // 3️⃣ Auswerten
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🔗 Verbindung zur Datenbank
    private static Connection connect() throws SQLException {
        // Wichtig: Der SQLite-JDBC-Treiber muss eingebunden sein (z. B. sqlite-jdbc-3.43.0.0.jar)
        return DriverManager.getConnection(DB_URL);
    }

    // 🏗 Tabelle erstellen, falls sie noch nicht existiert
    public static void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS random_values (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                value INTEGER NOT NULL,
                value2 INTEGER NOT NULL
            );s
            """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ Tabelle 'random_values' wurde erstellt oder existiert bereits.");
        }
    }

    // 🎲 Zufallsdaten einfügen
    public static void insertRandomData(int numberOfRows) throws SQLException {
        String clearTableSql = "DELETE FROM random_values;";
        String insertSql = "INSERT INTO random_values(value, value2) VALUES(?, ?);";

        try (Connection conn = connect();
             Statement clearStmt = conn.createStatement();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {

            // Alte Daten löschen
            clearStmt.execute(clearTableSql);

            Random random = new Random();
            System.out.println("📥 Füge " + numberOfRows + " neue Zeilen ein...");

            for (int i = 0; i < numberOfRows; i++) {
                int randomValue = random.nextInt(10) + 1; // 1–10
                int moduloValue = randomValue % 2;        // 0 oder 1

                pstmt.setInt(1, randomValue);
                pstmt.setInt(2, moduloValue);
                pstmt.executeUpdate();
            }

            System.out.println("✅ " + numberOfRows + " Zeilen erfolgreich eingefügt.");
        }
    }

    // 📊 Zählen und Ausgabe der geraden/ungeraden Werte
    public static void countAndPrintValues() throws SQLException {
        String sql = "SELECT value FROM random_values;";
        int gerade = 0;
        int ungerade = 0;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int value = rs.getInt("value");
                if (value % 2 == 0)
                    gerade++;
                else
                    ungerade++;
            }

            
            System.out.println("Gerade Werte:  " + gerade);
            System.out.println("Ungerade Werte: " + ungerade);
            
        }
    }
}
