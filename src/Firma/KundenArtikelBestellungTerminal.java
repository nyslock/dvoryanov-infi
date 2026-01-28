package Firma;

import java.sql.*;
import java.util.Scanner;

public class KundenArtikelBestellungTerminal {

    private Connection conn;
    private KundenVerwaltung kunden;
    private ArtikelVerwaltung artikel;
    private BestellungVerwaltung bestellung;
    private DataTransfer io;

    private void connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/firma_gmbh";
        String user = "root";
        String password = "124595761234";

        conn = DriverManager.getConnection(url, user, password);

        kunden = new KundenVerwaltung(conn);
        artikel = new ArtikelVerwaltung(conn);
        bestellung = new BestellungVerwaltung(conn, artikel);
        io = new DataTransfer(conn);

        System.out.println("Mit MySQL verbunden (java.exe).");
    }

    private void createTables() throws SQLException {
        kunden.createTable();
        artikel.createTable();
        bestellung.createTable();
        System.out.println("Tabellen bereit.");
    }

    private static int readInt(Scanner sc, String text) {
        System.out.print(text);
        while (!sc.hasNextInt()) { sc.next(); System.out.print("Bitte Zahl: "); }
        int v = sc.nextInt();
        sc.nextLine(); // consume newline
        return v;
    }

    private static double readDouble(Scanner sc, String text) {
        System.out.print(text);
        while (!sc.hasNextDouble()) { sc.next(); System.out.print("Bitte Zahl: "); }
        double v = sc.nextDouble();
        sc.nextLine();
        return v;
    }

    private static String readLine(Scanner sc, String text) {
        System.out.print(text);
        return sc.nextLine();
    }

    public void run() throws Exception {
        try (Scanner sc = new Scanner(System.in)) {
            boolean lauf = true;

            while (lauf) {
                System.out.println("""
                    
                    ===== MENU =====
                    1  Kunden anzeigen
                    2  Kunde hinzufügen
                    3  Kunde ändern
                    4  Kunde löschen

                    5  Artikel anzeigen
                    6  Artikel hinzufügen
                    7  Artikel ändern
                    8  Artikel löschen

                    9  Bestellen (mit Lager)
                    10 Bestellungen von Kunde anzeigen
                    11 Bestellung ändern (Anzahl)
                    12 Bestellung löschen

                    13 Abfrage: Low Stock
                    14 Abfrage: Umsatz pro Artikel
                    15 Abfrage: Top Kunden

                    20 Export CSV (kunden/artikel/bestellungen)
                    21 Import CSV (kunden/artikel/bestellungen)
                    22 Export JSON (kunden/artikel/bestellungen)
                    23 Import JSON (kunden/artikel/bestellungen)

                    0  Ende
                    """);

                int wahl = readInt(sc, "Deine Wahl: ");

                switch (wahl) {
                    case 1 -> kunden.listKunden();

                    case 2 -> {
                        String name = readLine(sc, "Name: ");
                        String email = readLine(sc, "Email: ");
                        int id = kunden.insertKunde(name, email);
                        System.out.println("Neue Kunden-ID: " + id);
                    }

                    case 3 -> {
                        int id = readInt(sc, "Kunden-ID: ");
                        String name = readLine(sc, "Neuer Name: ");
                        String email = readLine(sc, "Neue Email: ");
                        kunden.updateKunde(id, name, email);
                    }

                    case 4 -> {
                        int id = readInt(sc, "Kunden-ID löschen: ");
                        kunden.deleteKunde(id);
                    }

                    case 5 -> artikel.listArtikel();

                    case 6 -> {
                        String bez = readLine(sc, "Bezeichnung: ");
                        double preis = readDouble(sc, "Preis: ");
                        int lager = readInt(sc, "Lagerbestand: ");
                        int id = artikel.insertArtikel(bez, preis, lager);
                        System.out.println("Neue Artikel-ID: " + id);
                    }

                    case 7 -> {
                        int id = readInt(sc, "Artikel-ID: ");
                        String bez = readLine(sc, "Neue Bezeichnung: ");
                        double preis = readDouble(sc, "Neuer Preis: ");
                        int lager = readInt(sc, "Neuer Lagerbestand: ");
                        artikel.updateArtikel(id, bez, preis, lager);
                    }

                    case 8 -> {
                        int id = readInt(sc, "Artikel-ID löschen: ");
                        artikel.deleteArtikel(id);
                    }

                    case 9 -> {
                        int kid = readInt(sc, "Kunden-ID: ");
                        int aid = readInt(sc, "Artikel-ID: ");
                        int anzahl = readInt(sc, "Anzahl: ");
                        bestellung.bestelle(kid, aid, anzahl);
                    }

                    case 10 -> {
                        int kid = readInt(sc, "Kunden-ID: ");
                        bestellung.showBestellungenVonKunde(kid);
                    }

                    case 11 -> {
                        int bid = readInt(sc, "Bestell-ID: ");
                        int neue = readInt(sc, "Neue Anzahl: ");
                        bestellung.updateBestellungAnzahl(bid, neue);
                    }

                    case 12 -> {
                        int bid = readInt(sc, "Bestell-ID löschen: ");
                        bestellung.deleteBestellung(bid);
                    }

                    case 13 -> {
                        int grenze = readInt(sc, "Grenze (z.B. 3): ");
                        bestellung.showLowStock(grenze);
                    }

                    case 14 -> bestellung.showUmsatzProArtikel();

                    case 15 -> bestellung.showTopKunden();

                    case 20 -> {
                        String what = readLine(sc, "Was exportieren (kunden/artikel/bestellungen): ");
                        String file = readLine(sc, "Datei (z.B. kunden.csv): ");
                        if (what.equalsIgnoreCase("kunden")) io.exportKundenCSV(file);
                        else if (what.equalsIgnoreCase("artikel")) io.exportArtikelCSV(file);
                        else io.exportBestellungenCSV(file);
                    }

                    case 21 -> {
                        String what = readLine(sc, "Was importieren (kunden/artikel/bestellungen): ");
                        String file = readLine(sc, "Datei: ");
                        if (what.equalsIgnoreCase("kunden")) io.importKundenCSV(file);
                        else if (what.equalsIgnoreCase("artikel")) io.importArtikelCSV(file);
                        else io.importBestellungenCSV(file);
                    }

                    case 22 -> {
                        String what = readLine(sc, "Was exportieren (kunden/artikel/bestellungen): ");
                        String file = readLine(sc, "Datei (z.B. kunden.json): ");
                        if (what.equalsIgnoreCase("kunden")) io.exportKundenJSON(file);
                        else if (what.equalsIgnoreCase("artikel")) io.exportArtikelJSON(file);
                        else io.exportBestellungenJSON(file);
                    }

                    case 23 -> {
                        String what = readLine(sc, "Was importieren (kunden/artikel/bestellungen): ");
                        String file = readLine(sc, "Datei: ");
                        if (what.equalsIgnoreCase("kunden")) io.importKundenJSON(file);
                        else if (what.equalsIgnoreCase("artikel")) io.importArtikelJSON(file);
                        else io.importBestellungenJSON(file);
                    }

                    case 0 -> lauf = false;

                    default -> System.out.println("Unbekannte Wahl!");
                }
            }
        }
    }

    private void close() {
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }

    public static void main(String[] args) {
        KundenArtikelBestellungTerminal app = new KundenArtikelBestellungTerminal();
        try {
            app.connect();
            app.createTables();
            app.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            app.close();
        }
    }
}
