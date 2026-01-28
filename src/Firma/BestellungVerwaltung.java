package Firma;

import java.sql.*;

public class BestellungVerwaltung {
    private final Connection conn;
    private final ArtikelVerwaltung artikel;

    public BestellungVerwaltung(Connection conn, ArtikelVerwaltung artikel) {
        this.conn = conn;
        this.artikel = artikel;
    }

    public void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS bestellung (
                id INT AUTO_INCREMENT PRIMARY KEY,
                kunden_id INT NOT NULL,
                artikel_id INT NOT NULL,
                anzahl INT NOT NULL,
                bestellt_am DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (kunden_id) REFERENCES kunde(id),
                FOREIGN KEY (artikel_id) REFERENCES artikel(id)
            )
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.execute();
        }
    }

    // Bestellen mit Lager-Check + Transaktion
    public void bestelle(int kundenId, int artikelId, int anzahl) throws SQLException {
        conn.setAutoCommit(false);
        try {
            int lager = artikel.getLagerbestand(artikelId);
            if (lager < anzahl) {
                throw new SQLException("Nicht genug Lager! Lager=" + lager + ", benötigt=" + anzahl);
            }

            String sql = "INSERT INTO bestellung (kunden_id, artikel_id, anzahl) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, kundenId);
                ps.setInt(2, artikelId);
                ps.setInt(3, anzahl);
                ps.executeUpdate();
            }

            artikel.changeLagerbestand(artikelId, -anzahl);
            conn.commit();
            System.out.println("Bestellung OK. Lager wurde reduziert.");
        } catch (SQLException e) {
            conn.rollback();
            System.out.println("Bestellung FEHLER: " + e.getMessage());
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public void updateBestellungAnzahl(int bestellId, int neueAnzahl) throws SQLException {
        String sql = "UPDATE bestellung SET anzahl=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, neueAnzahl);
            ps.setInt(2, bestellId);
            System.out.println("Bestellung updated: " + ps.executeUpdate() + " Zeile(n)");
        }
    }

    public void deleteBestellung(int bestellId) throws SQLException {
        String sql = "DELETE FROM bestellung WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bestellId);
            System.out.println("Bestellung gelöscht: " + ps.executeUpdate() + " Zeile(n)");
        }
    }

    public void showBestellungenVonKunde(int kundenId) throws SQLException {
        String sql = """
            SELECT b.id, b.bestellt_am, a.bezeichnung, a.preis, b.anzahl,
                   (a.preis * b.anzahl) AS gesamt
            FROM bestellung b
            JOIN artikel a ON b.artikel_id = a.id
            WHERE b.kunden_id = ?
            ORDER BY b.bestellt_am DESC
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, kundenId);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\nBestellungen von Kunde " + kundenId + ":");
                while (rs.next()) {
                    System.out.println(
                            "BestellID " + rs.getInt("id") +
                            " | " + rs.getTimestamp("bestellt_am") +
                            " | " + rs.getInt("anzahl") + "x " + rs.getString("bezeichnung") +
                            " | Gesamt=" + rs.getDouble("gesamt")
                    );
                }
            }
        }
    }

    // Abfragen
    public void showLowStock(int grenze) throws SQLException {
        String sql = "SELECT id, bezeichnung, lagerbestand FROM artikel WHERE lagerbestand <= ? ORDER BY lagerbestand";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, grenze);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\nLow-Stock (<= " + grenze + "):");
                while (rs.next()) {
                    System.out.println(rs.getInt("id") + " | " + rs.getString("bezeichnung") +
                            " | Lager=" + rs.getInt("lagerbestand"));
                }
            }
        }
    }

    public void showUmsatzProArtikel() throws SQLException {
        String sql = """
            SELECT a.id, a.bezeichnung,
                   SUM(b.anzahl) AS verkauft,
                   SUM(b.anzahl * a.preis) AS umsatz
            FROM bestellung b
            JOIN artikel a ON b.artikel_id = a.id
            GROUP BY a.id, a.bezeichnung
            ORDER BY umsatz DESC
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("\nUmsatz pro Artikel:");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " + rs.getString("bezeichnung") +
                        " | verkauft=" + rs.getInt("verkauft") +
                        " | umsatz=" + rs.getDouble("umsatz"));
            }
        }
    }

    public void showTopKunden() throws SQLException {
        String sql = """
            SELECT k.id, k.name,
                   SUM(b.anzahl * a.preis) AS umsatz
            FROM bestellung b
            JOIN kunde k ON b.kunden_id = k.id
            JOIN artikel a ON b.artikel_id = a.id
            GROUP BY k.id, k.name
            ORDER BY umsatz DESC
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("\nTop-Kunden nach Umsatz:");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " + rs.getString("name") +
                        " | umsatz=" + rs.getDouble("umsatz"));
            }
        }
    }
}
