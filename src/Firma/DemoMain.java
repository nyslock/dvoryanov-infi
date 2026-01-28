package Firma;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DemoMain {

    // ✅ поменяй базу/логин/пароль при необходимости
    private static final String URL = "jdbc:mysql://localhost:3306/firma_gmbh";
    private static final String USER = "root";
    private static final String PASS = "124595761234";

    public static void main(String[] args) {
        System.out.println("=== DEMO START ===");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.println("CONNECTED: " + URL);

            // 1) Create managers
            KundenVerwaltung kunden = new KundenVerwaltung(conn);
            ArtikelVerwaltung artikel = new ArtikelVerwaltung(conn);
            BestellungVerwaltung bestellung = new BestellungVerwaltung(conn, artikel);
            DataTransfer io = new DataTransfer(conn);

            // 2) Create tables
            kunden.createTable();
            artikel.createTable();
            bestellung.createTable();
            System.out.println("TABLES OK");

            // 3) Insert demo data only if empty
            ensureDemoData(conn, kunden, artikel);

            // 4) Show current data
            kunden.listKunden();
            artikel.listArtikel();

            // 5) Demo orders (safe, respects stock)
            int aliceId = findKundeIdByEmail(conn, "alice@mail.com");
            int bobId = findKundeIdByEmail(conn, "bob@mail.com");
            int kaffeeId = findArtikelIdByBez(conn, "Kaffee");
            int keksId = findArtikelIdByBez(conn, "Keks");

            System.out.println("\n--- DEMO BESTELLUNGEN ---");
            bestellung.bestelle(aliceId, kaffeeId, 2);
            bestellung.bestelle(aliceId, keksId, 5);
            bestellung.bestelle(bobId, kaffeeId, 1);

            // 6) Show orders
            bestellung.showBestellungenVonKunde(aliceId);
            bestellung.showBestellungenVonKunde(bobId);

            // 7) Interesting queries
            bestellung.showLowStock(5);
            bestellung.showUmsatzProArtikel();
            bestellung.showTopKunden();

            // 8) Export demo (files appear in project root folder)
            System.out.println("\n--- EXPORT ---");
            io.exportKundenCSV("kunden.csv");
            io.exportArtikelCSV("artikel.csv");
            io.exportBestellungenCSV("bestellungen.csv");

            io.exportKundenJSON("kunden.json");
            io.exportArtikelJSON("artikel.json");
            io.exportBestellungenJSON("bestellungen.json");

            System.out.println("\n✅ Export done: kunden.csv / artikel.csv / bestellungen.csv");
            System.out.println("✅ Export done: kunden.json / artikel.json / bestellungen.json");

            // 9) OPTIONAL: Import back (uncomment when you want)
            // System.out.println("\n--- IMPORT (optional) ---");
            // io.importKundenCSV("kunden.csv");
            // io.importArtikelCSV("artikel.csv");
            // io.importBestellungenCSV("bestellungen.csv");
            // io.importKundenJSON("kunden.json");
            // io.importArtikelJSON("artikel.json");
            // io.importBestellungenJSON("bestellungen.json");

            System.out.println("\n=== DEMO END ===");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------- helpers for DemoMain --------------------

    private static void ensureDemoData(Connection conn,
                                       KundenVerwaltung kunden,
                                       ArtikelVerwaltung artikel) throws Exception {

        if (count(conn, "kunde") == 0) {
            System.out.println("\n--- INSERT DEMO KUNDEN ---");
            kunden.insertKunde("Alice", "alice@mail.com");
            kunden.insertKunde("Bob", "bob@mail.com");
        } else {
            System.out.println("\nKunden already exist -> skip insert.");
        }

        if (count(conn, "artikel") == 0) {
            System.out.println("\n--- INSERT DEMO ARTIKEL ---");
            artikel.insertArtikel("Kaffee", 3.50, 10);
            artikel.insertArtikel("Keks", 1.20, 20);
        } else {
            System.out.println("\nArtikel already exist -> skip insert.");
        }
    }

    private static int count(Connection conn, String table) throws Exception {
        String sql = "SELECT COUNT(*) FROM " + table;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private static int findKundeIdByEmail(Connection conn, String email) throws Exception {
        String sql = "SELECT id FROM kunde WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new RuntimeException("Kunde not found by email: " + email);
    }

    private static int findArtikelIdByBez(Connection conn, String bez) throws Exception {
        String sql = "SELECT id FROM artikel WHERE bezeichnung = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bez);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new RuntimeException("Artikel not found by bezeichnung: " + bez);
    }
}
