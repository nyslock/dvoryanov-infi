package infi_sqlite;

import java.sql.*;
import java.util.Random;

public class Sqlite {

    private static final String DB_URL = "jdbc:sqlite:database2.db";

    public static void main(String[] args) {
        try {
            createTables();
            insertInitialData();
            insertRandomData(20);
            insertInvalidData();
            countAndPrintValues();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public static void createTables() throws SQLException {
        String sqlCategories = """
            CREATE TABLE IF NOT EXISTS categories (
                category_id INTEGER PRIMARY KEY,
                name TEXT NOT NULL
            );
            """;
        String sqlRandomValues = """
            CREATE TABLE IF NOT EXISTS random_values (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                value INTEGER NOT NULL,
                category_id INTEGER,
                FOREIGN KEY(category_id) REFERENCES categories(category_id)
            );
            """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlCategories);
            stmt.execute(sqlRandomValues);
            System.out.println("Tabellen 'categories' und 'random_values' wurden erstellt oder existieren bereits.");
        }
    }
    
    public static void insertInitialData() throws SQLException {
        String clearCategoriesSql = "DELETE FROM categories;";
        String checkDataSql = "SELECT COUNT(*) FROM categories;";
        
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO categories(category_id, name) VALUES(?, ?);")) {
            
            try (ResultSet rs = stmt.executeQuery(checkDataSql)) {
                if (rs.getInt(1) > 0) {
                     System.out.println("'categories' enthält bereits Daten.");
                     return;
                }
            }
            
            stmt.execute(clearCategoriesSql);

            pstmt.setInt(1, 0);
            pstmt.setString(2, "Gerade");
            pstmt.executeUpdate();

            pstmt.setInt(1, 1);
            pstmt.setString(2, "Ungerade");
            pstmt.executeUpdate();
            
            System.out.println("Initialdaten in 'categories' eingefügt (IDs 0 und 1).");
        }
    }

    public static void insertRandomData(int numberOfRows) throws SQLException {
        String clearTableSql = "DELETE FROM random_values;";
        String insertSql = "INSERT INTO random_values(value, category_id) VALUES(?, ?);";

        try (Connection conn = connect();
             Statement clearStmt = conn.createStatement();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {

            clearStmt.execute(clearTableSql);
            Random random = new Random();
            System.out.println("\nFüge " + numberOfRows + " neue Zeilen mit GÜLTIGEN Foreign Keys ein...");

            for (int i = 0; i < numberOfRows; i++) {
                int randomValue = random.nextInt(10) + 1;
                int categoryId = randomValue % 2 == 0 ? 0 : 1; 

                pstmt.setInt(1, randomValue);
                pstmt.setInt(2, categoryId);
                pstmt.executeUpdate();
            }

            System.out.println(numberOfRows + " Zeilen erfolgreich eingefügt.");
        }
    }
    
    public static void insertInvalidData() {
        String insertSql = "INSERT INTO random_values(value, category_id) VALUES(?, ?);";

        System.out.println("\n--- Test: Ungültiger Foreign Key ---");
        System.out.println("Versuche, einen Wert mit category_id=99 einzufügen...");

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            
            pstmt.setInt(1, 42); 
            pstmt.setInt(2, 99); 

            pstmt.executeUpdate();
            
            System.out.println("FEHLER: Die Zeile wurde fälschlicherweise eingefügt! Foreign Key Constraint nicht aktiv.");
            
        } catch (SQLException e) {
            System.out.println("ERFOLG: Die Einfügung wurde durch den Foreign Key abgelehnt.");
            System.out.println("   Datenbankfehler (Constraint Violation): " + e.getMessage());
            System.out.println("   Grund: Die category_id '99' existiert nicht in der Tabelle 'categories'.");
        }
    }

    public static void countAndPrintValues() throws SQLException {
        String sql = """
            SELECT 
                c.name, 
                COUNT(r.id) AS count
            FROM random_values r
            JOIN categories c ON r.category_id = c.category_id
            GROUP BY c.name;
            """;

        System.out.println("\n--- Auswertung ---");
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String name = rs.getString("name");
                int count = rs.getInt("count");
                System.out.println(name + " Werte:  " + count);
            }
        }
    }
}