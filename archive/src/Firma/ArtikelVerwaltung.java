package Firma;

import java.sql.*;

public class ArtikelVerwaltung {
    private final Connection conn;

    public ArtikelVerwaltung(Connection conn) {
        this.conn = conn;
    }

    public void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS artikel (
                id INT AUTO_INCREMENT PRIMARY KEY,
                bezeichnung VARCHAR(100) NOT NULL,
                preis DOUBLE NOT NULL,
                lagerbestand INT NOT NULL,
                UNIQUE KEY uq_artikel_bezeichnung (bezeichnung)
            )
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.execute();
        }
    }

    public int insertArtikel(String bez, double preis, int lager) throws SQLException {
        String sql = "INSERT INTO artikel (bezeichnung, preis, lagerbestand) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, bez);
            ps.setDouble(2, preis);
            ps.setInt(3, lager);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public void updateArtikel(int id, String bez, double preis, int lager) throws SQLException {
        String sql = "UPDATE artikel SET bezeichnung=?, preis=?, lagerbestand=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bez);
            ps.setDouble(2, preis);
            ps.setInt(3, lager);
            ps.setInt(4, id);
            System.out.println("Artikel updated: " + ps.executeUpdate() + " Zeile(n)");
        }
    }

    public void deleteArtikel(int id) throws SQLException {
        String sql = "DELETE FROM artikel WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            System.out.println("Artikel gelöscht: " + ps.executeUpdate() + " Zeile(n)");
        }
    }

    public void listArtikel() throws SQLException {
        String sql = "SELECT id, bezeichnung, preis, lagerbestand FROM artikel ORDER BY id";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\nArtikel:");
            while (rs.next()) {
                System.out.println(
                        rs.getInt("id") + " | " +
                        rs.getString("bezeichnung") + " | " +
                        rs.getDouble("preis") + " € | Lager=" +
                        rs.getInt("lagerbestand")
                );
            }
        }
    }

    public int getLagerbestand(int artikelId) throws SQLException {
        String sql = "SELECT lagerbestand FROM artikel WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, artikelId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public void changeLagerbestand(int artikelId, int delta) throws SQLException {
        String sql = "UPDATE artikel SET lagerbestand = lagerbestand + ? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, artikelId);
            ps.executeUpdate();
        }
    }
}
