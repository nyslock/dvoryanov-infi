package Firma;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataTransfer {

    private final Connection conn;
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String SEP = ";";
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public DataTransfer(Connection conn) {
        this.conn = conn;
    }

    // -------------------- CSV EXPORT --------------------

    public void exportKundenCSV(String file) throws Exception {
        String sql = "SELECT id, name, email, erstellt_am FROM kunde ORDER BY id";
        List<String> out = new ArrayList<>();
        out.add("id;name;email;erstellt_am");

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(rs.getInt("id") + SEP +
                        safe(rs.getString("name")) + SEP +
                        safe(rs.getString("email")) + SEP +
                        safeDate(rs.getDate("erstellt_am")));
            }
        }

        Files.write(Path.of(file), out);
        System.out.println("Export OK: " + file);
    }

    public void exportArtikelCSV(String file) throws Exception {
        String sql = "SELECT id, bezeichnung, preis, lagerbestand FROM artikel ORDER BY id";
        List<String> out = new ArrayList<>();
        out.add("id;bezeichnung;preis;lagerbestand");

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(rs.getInt("id") + SEP +
                        safe(rs.getString("bezeichnung")) + SEP +
                        rs.getDouble("preis") + SEP +
                        rs.getInt("lagerbestand"));
            }
        }

        Files.write(Path.of(file), out);
        System.out.println("Export OK: " + file);
    }

    public void exportBestellungenCSV(String file) throws Exception {
        String sql = "SELECT id, kunden_id, artikel_id, anzahl, bestellt_am FROM bestellung ORDER BY id";
        List<String> out = new ArrayList<>();
        out.add("id;kunden_id;artikel_id;anzahl;bestellt_am");

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("bestellt_am");
                String dt = (ts == null) ? "" : ts.toLocalDateTime().format(DT);

                out.add(rs.getInt("id") + SEP +
                        rs.getInt("kunden_id") + SEP +
                        rs.getInt("artikel_id") + SEP +
                        rs.getInt("anzahl") + SEP +
                        dt);
            }
        }

        Files.write(Path.of(file), out);
        System.out.println("Export OK: " + file);
    }

    // -------------------- CSV IMPORT --------------------

    public void importKundenCSV(String file) throws Exception {
        List<String> lines = Files.readAllLines(Path.of(file));
        if (lines.size() <= 1) return;

        String sql = """
            INSERT INTO kunde (id, name, email, erstellt_am)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
              name=VALUES(name),
              email=VALUES(email),
              erstellt_am=VALUES(erstellt_am)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i < lines.size(); i++) {
                String[] p = lines.get(i).split(SEP, -1);
                ps.setInt(1, Integer.parseInt(p[0].trim()));
                ps.setString(2, p[1].trim());
                ps.setString(3, emptyToNull(p[2]));
                ps.setDate(4, parseDate(p[3]));
                ps.addBatch();
            }
            ps.executeBatch();
        }

        System.out.println("Import OK: " + file);
    }

    public void importArtikelCSV(String file) throws Exception {
        List<String> lines = Files.readAllLines(Path.of(file));
        if (lines.size() <= 1) return;

        String sql = """
            INSERT INTO artikel (id, bezeichnung, preis, lagerbestand)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
              bezeichnung=VALUES(bezeichnung),
              preis=VALUES(preis),
              lagerbestand=VALUES(lagerbestand)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i < lines.size(); i++) {
                String[] p = lines.get(i).split(SEP, -1);
                ps.setInt(1, Integer.parseInt(p[0].trim()));
                ps.setString(2, p[1].trim());
                ps.setDouble(3, Double.parseDouble(p[2].trim()));
                ps.setInt(4, Integer.parseInt(p[3].trim()));
                ps.addBatch();
            }
            ps.executeBatch();
        }

        System.out.println("Import OK: " + file);
    }

    public void importBestellungenCSV(String file) throws Exception {
        List<String> lines = Files.readAllLines(Path.of(file));
        if (lines.size() <= 1) return;

        String sql = """
            INSERT INTO bestellung (id, kunden_id, artikel_id, anzahl, bestellt_am)
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
              kunden_id=VALUES(kunden_id),
              artikel_id=VALUES(artikel_id),
              anzahl=VALUES(anzahl),
              bestellt_am=VALUES(bestellt_am)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i < lines.size(); i++) {
                String[] p = lines.get(i).split(SEP, -1);
                ps.setInt(1, Integer.parseInt(p[0].trim()));
                ps.setInt(2, Integer.parseInt(p[1].trim()));
                ps.setInt(3, Integer.parseInt(p[2].trim()));
                ps.setInt(4, Integer.parseInt(p[3].trim()));
                ps.setTimestamp(5, parseTimestamp(p[4]));
                ps.addBatch();
            }
            ps.executeBatch();
        }

        System.out.println("Import OK: " + file);
    }

    // -------------------- JSON EXPORT --------------------

    public void exportKundenJSON(String file) throws Exception {
        List<Map<String, Object>> data = new ArrayList<>();
        String sql = "SELECT id, name, email, erstellt_am FROM kunde ORDER BY id";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", rs.getInt("id"));
                m.put("name", rs.getString("name"));
                m.put("email", rs.getString("email"));

                java.sql.Date d = rs.getDate("erstellt_am");
                m.put("erstellt_am", d == null ? null : d.toString());

                data.add(m);
            }
        }

        mapper.writerWithDefaultPrettyPrinter().writeValue(Path.of(file).toFile(), data);
        System.out.println("Export OK: " + file);
    }

    public void exportArtikelJSON(String file) throws Exception {
        List<Map<String, Object>> data = new ArrayList<>();
        String sql = "SELECT id, bezeichnung, preis, lagerbestand FROM artikel ORDER BY id";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", rs.getInt("id"));
                m.put("bezeichnung", rs.getString("bezeichnung"));
                m.put("preis", rs.getDouble("preis"));
                m.put("lagerbestand", rs.getInt("lagerbestand"));
                data.add(m);
            }
        }

        mapper.writerWithDefaultPrettyPrinter().writeValue(Path.of(file).toFile(), data);
        System.out.println("Export OK: " + file);
    }

    public void exportBestellungenJSON(String file) throws Exception {
        List<Map<String, Object>> data = new ArrayList<>();
        String sql = "SELECT id, kunden_id, artikel_id, anzahl, bestellt_am FROM bestellung ORDER BY id";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", rs.getInt("id"));
                m.put("kunden_id", rs.getInt("kunden_id"));
                m.put("artikel_id", rs.getInt("artikel_id"));
                m.put("anzahl", rs.getInt("anzahl"));

                Timestamp ts = rs.getTimestamp("bestellt_am");
                m.put("bestellt_am", ts == null ? null : ts.toLocalDateTime().format(DT));

                data.add(m);
            }
        }

        mapper.writerWithDefaultPrettyPrinter().writeValue(Path.of(file).toFile(), data);
        System.out.println("Export OK: " + file);
    }

    // -------------------- JSON IMPORT --------------------

    public void importKundenJSON(String file) throws Exception {
        List<Map<String, Object>> list = mapper.readValue(
                Path.of(file).toFile(),
                new TypeReference<List<Map<String, Object>>>() {}
        );

        String sql = """
            INSERT INTO kunde (id, name, email, erstellt_am)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
              name=VALUES(name),
              email=VALUES(email),
              erstellt_am=VALUES(erstellt_am)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Map<String, Object> m : list) {
                ps.setInt(1, ((Number) m.get("id")).intValue());
                ps.setString(2, (String) m.get("name"));
                ps.setString(3, (String) m.get("email"));
                ps.setDate(4, parseDate((String) m.get("erstellt_am")));
                ps.addBatch();
            }
            ps.executeBatch();
        }

        System.out.println("Import OK: " + file);
    }

    public void importArtikelJSON(String file) throws Exception {
        List<Map<String, Object>> list = mapper.readValue(
                Path.of(file).toFile(),
                new TypeReference<List<Map<String, Object>>>() {}
        );

        String sql = """
            INSERT INTO artikel (id, bezeichnung, preis, lagerbestand)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
              bezeichnung=VALUES(bezeichnung),
              preis=VALUES(preis),
              lagerbestand=VALUES(lagerbestand)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Map<String, Object> m : list) {
                ps.setInt(1, ((Number) m.get("id")).intValue());
                ps.setString(2, (String) m.get("bezeichnung"));
                ps.setDouble(3, ((Number) m.get("preis")).doubleValue());
                ps.setInt(4, ((Number) m.get("lagerbestand")).intValue());
                ps.addBatch();
            }
            ps.executeBatch();
        }

        System.out.println("Import OK: " + file);
    }

    public void importBestellungenJSON(String file) throws Exception {
        List<Map<String, Object>> list = mapper.readValue(
                Path.of(file).toFile(),
                new TypeReference<List<Map<String, Object>>>() {}
        );

        String sql = """
            INSERT INTO bestellung (id, kunden_id, artikel_id, anzahl, bestellt_am)
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
              kunden_id=VALUES(kunden_id),
              artikel_id=VALUES(artikel_id),
              anzahl=VALUES(anzahl),
              bestellt_am=VALUES(bestellt_am)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Map<String, Object> m : list) {
                ps.setInt(1, ((Number) m.get("id")).intValue());
                ps.setInt(2, ((Number) m.get("kunden_id")).intValue());
                ps.setInt(3, ((Number) m.get("artikel_id")).intValue());
                ps.setInt(4, ((Number) m.get("anzahl")).intValue());
                ps.setTimestamp(5, parseTimestamp((String) m.get("bestellt_am")));
                ps.addBatch();
            }
            ps.executeBatch();
        }

        System.out.println("Import OK: " + file);
    }

    // -------------------- helpers --------------------

    private static String safe(String s) {
        return s == null ? "" : s.replace(";", ",");
    }

    private static String safeDate(java.sql.Date d) {
        return d == null ? "" : d.toString();
    }

    private static String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    private static java.sql.Date parseDate(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        return java.sql.Date.valueOf(s.trim());
    }

    private static Timestamp parseTimestamp(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        LocalDateTime ldt = LocalDateTime.parse(s.trim(), DT);
        return Timestamp.valueOf(ldt);
    }
}
