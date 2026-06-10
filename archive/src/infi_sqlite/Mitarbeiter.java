package infi_sqlite;

import java.sql.*;

public class Mitarbeiter {
    private Connection conn;

    // Подключение к базе
    private void connect() throws SQLException {
        String url = "jdbc:sqlite:firma.db";
        this.conn = DriverManager.getConnection(url);
        System.out.println("Соединение установлено.");
    }

    // Создание таблицы
    public void create_table(String name) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + name + " ("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                   + "nachname TEXT, "
                   + "gehalt DOUBLE"
                   + ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица '" + name + "' создана или уже существует.");
        }
    }

    // Добавление работника
    public void add_worker(String table_name, String nachname, double gehalt) throws SQLException {
        String sql = "INSERT INTO " + table_name + " (nachname, gehalt) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nachname);
            pstmt.setDouble(2, gehalt);
            pstmt.executeUpdate();
            System.out.println("Добавлен работник: " + nachname);
        }
    }

    // Закрытие соединения
    public void close() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    // Главный метод
    public static void main(String[] args) throws SQLException {
        Mitarbeiter GmbH = new Mitarbeiter();

        GmbH.connect();
        GmbH.create_table("firma");
        GmbH.add_worker("firma", "dvoryanov", 5000);
        GmbH.close();

    }
}
