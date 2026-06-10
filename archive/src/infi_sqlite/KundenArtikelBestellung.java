package infi_sqlite;

import java.sql.*;

public class KundenArtikelBestellung {

    private Connection conn;

    // 1) Verbindung herstellen
    private void connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/firma";
        String user = "root";       // <--- dein Benutzername
        String password = "124595761234";    // <--- dein Passwort

        conn = DriverManager.getConnection(url, user, password);
        System.out.println("Mit MySQL verbunden.");
    }

    // 2) Tabellen erstellen
    private void createTables() throws SQLException {

        String sqlKunde = """
            CREATE TABLE IF NOT EXISTS kunde (
                id INT PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(100) NOT NULL
            );
        """;

        String sqlArtikel = """
            CREATE TABLE IF NOT EXISTS artikel (
                id INT PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(100) NOT NULL,
                preis DOUBLE NOT NULL
            );
        """;

        String sqlBestellung = """
            CREATE TABLE IF NOT EXISTS bestellung (
                id INT PRIMARY KEY AUTO_INCREMENT,
                kunde_id INT NOT NULL,
                artikel_id INT NOT NULL,
                menge INT NOT NULL,
                FOREIGN KEY (kunde_id) REFERENCES kunde(id),
                FOREIGN KEY (artikel_id) REFERENCES artikel(id)
            );
        """;

        try (PreparedStatement ps1 = conn.prepareStatement(sqlKunde);
             PreparedStatement ps2 = conn.prepareStatement(sqlArtikel);
             PreparedStatement ps3 = conn.prepareStatement(sqlBestellung)) {

            ps1.execute();
            ps2.execute();
            ps3.execute();
        }

        System.out.println("Tabellen erstellt (kunde, artikel, bestellung).");
    }

    // 3) Kunde einfügen — kurze Version
    private int insertKunde(String name) throws SQLException {
        String sql = "INSERT INTO kunde (name) VALUES (?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("Kunde eingefügt: " + name + " (id=" + id + ")");
                    return id;
                }
            }
        }
        return -1;
    }

    // 4) Artikel einfügen — kurze Version
    private int insertArtikel(String name, double preis) throws SQLException {
        String sql = "INSERT INTO artikel (name, preis) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setDouble(2, preis);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("Artikel eingefügt: " + name +
                            " (id=" + id + ", preis=" + preis + ")");
                    return id;
                }
            }
        }
        return -1;
    }

    // 5) Bestellung einfügen — kurze Version
    private int insertBestellung(int kundeId, int artikelId, int menge) throws SQLException {
        String sql = "INSERT INTO bestellung (kunde_id, artikel_id, menge) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, kundeId);
            ps.setInt(2, artikelId);
            ps.setInt(3, menge);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("Bestellung eingefügt: Kunde=" + kundeId +
                            ", Artikel=" + artikelId + ", Menge=" + menge + " (id=" + id + ")");
                    return id;
                }
            }
        }
        return -1;
    }

    // 6) Bestellungen anzeigen
    private void showBestellungen() throws SQLException {

        String sql = """
            SELECT 
                b.id,
                k.name AS kunde_name,
                a.name AS artikel_name,
                a.preis,
                b.menge
            FROM bestellung b
            JOIN kunde k ON b.kunde_id = k.id
            JOIN artikel a ON b.artikel_id = a.id;
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\nBestellungen:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String kundeName = rs.getString("kunde_name");
                String artikelName = rs.getString("artikel_name");
                double preis = rs.getDouble("preis");
                int menge = rs.getInt("menge");

                double gesamt = preis * menge;

                System.out.println("Bestellung " + id + ": "
                        + kundeName + " bestellt "
                        + menge + "x " + artikelName
                        + " (Einzelpreis: " + preis
                        + ", Gesamt: " + gesamt + ")");
            }
        }
    }

    // 7) Verbindung schließen
    private void close() throws SQLException {
        if (conn != null) {
            conn.close();
            System.out.println("Verbindung geschlossen.");
        }
    }

    // 8) main – Testen
    public static void main(String[] args) {
        KundenArtikelBestellung app = new KundenArtikelBestellung();

        try {
            app.connect();
            app.createTables();

            int k1 = app.insertKunde("Musterkunde");
            int k2 = app.insertKunde("Dvoryanov");

            int a1 = app.insertArtikel("Kaffee", 3.50);
            int a2 = app.insertArtikel("Keks", 1.20);
            System.out.println("Alles gut");
            app.insertBestellung(k1, a1, 2);
            app.insertBestellung(k1, a2, 5);
            app.insertBestellung(k2, a1, 1);

            app.showBestellungen();
            app.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
