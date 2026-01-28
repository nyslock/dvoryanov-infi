package Firma;

import java.sql.*;

public class KundenVerwaltung {
    private final Connection conn;

    public KundenVerwaltung(Connection conn) {
        this.conn = conn;
    }

    public void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS kunde (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                email VARCHAR(100),
                erstellt_am DATE,
                UNIQUE KEY uq_kunde_email (email)
            )
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.execute();
        }
    }

    // Insert: erstellt_am = CURDATE()
    public int insertKunde(String name, String email) throws SQLException {
        String sql = "INSERT INTO kunde (name, email, erstellt_am) VALUES (?, ?, CURDATE())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, emptyToNull(email));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public void updateKunde(int id, String name, String email) throws SQLException {
        String sql = "UPDATE kunde SET name=?, email=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, emptyToNull(email));
            ps.setInt(3, id);
            System.out.println("Kunde updated: " + ps.executeUpdate() + " Zeile(n)");
        }
    }

    public void deleteKunde(int id) throws SQLException {
        String sql = "DELETE FROM kunde WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            System.out.println("Kunde gelöscht: " + ps.executeUpdate() + " Zeile(n)");
        }
    }

    public void listKunden() throws SQLException {
        String sql = "SELECT id, name, email, erstellt_am FROM kunde ORDER BY id";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\nKunden:");
            while (rs.next()) {
                System.out.println(
                        rs.getInt("id") + " | " +
                        rs.getString("name") + " | " +
                        rs.getString("email") + " | seit: " +
                        rs.getDate("erstellt_am")
                );
            }
        }
    }

    private static String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}
