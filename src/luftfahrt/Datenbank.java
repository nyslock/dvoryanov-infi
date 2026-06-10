package luftfahrt;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import luftfahrt.model.Airline;
import luftfahrt.model.Flug;
import luftfahrt.model.Flughafen;
import luftfahrt.model.Flugzeug;
import luftfahrt.model.Route;
import luftfahrt.model.Wartung;

/**
 * Zentrale Datenbank-Klasse.
 * Baut die ORMLite-Verbindung (ConnectionSource) auf, erstellt für jede Tabelle
 * einen DAO ("Data Access Object") und legt die Tabellen an.
 *
 * Es wird eine dateibasierte SQLite-Datenbank verwendet: kein Server, keine
 * Installation und kein Passwort nötig. Die Datei "luftfahrt.db" wird beim
 * ersten Start automatisch im Projektordner angelegt.
 */
public class Datenbank {

    // Dateibasierte SQLite-Datenbank: die Datei wird automatisch erstellt.
    private static final String URL = "jdbc:sqlite:luftfahrt.db";

    private ConnectionSource connectionSource;

    // Ein DAO pro Entität
    private Dao<Flughafen, Integer> flughafenDao;
    private Dao<Airline, Integer> airlineDao;
    private Dao<Flugzeug, Integer> flugzeugDao;
    private Dao<Route, Integer> routeDao;
    private Dao<Flug, Integer> flugDao;
    private Dao<Wartung, Integer> wartungDao;

    // Verbindung aufbauen, DAOs erzeugen und Tabellen anlegen
    public void verbinden() throws SQLException {
        connectionSource = new JdbcConnectionSource(URL);
        System.out.println("Mit SQLite verbunden: " + URL);

        flughafenDao = DaoManager.createDao(connectionSource, Flughafen.class);
        airlineDao = DaoManager.createDao(connectionSource, Airline.class);
        flugzeugDao = DaoManager.createDao(connectionSource, Flugzeug.class);
        routeDao = DaoManager.createDao(connectionSource, Route.class);
        flugDao = DaoManager.createDao(connectionSource, Flug.class);
        wartungDao = DaoManager.createDao(connectionSource, Wartung.class);

        // Reihenfolge wichtig: zuerst Tabellen ohne Fremdschlüssel
        TableUtils.createTableIfNotExists(connectionSource, Flughafen.class);
        TableUtils.createTableIfNotExists(connectionSource, Airline.class);
        TableUtils.createTableIfNotExists(connectionSource, Flugzeug.class);
        TableUtils.createTableIfNotExists(connectionSource, Route.class);
        TableUtils.createTableIfNotExists(connectionSource, Flug.class);
        TableUtils.createTableIfNotExists(connectionSource, Wartung.class);
        System.out.println("Tabellen bereit.");
    }

    public void schliessen() throws Exception {
        if (connectionSource != null) {
            connectionSource.close();
            System.out.println("Datenbankverbindung geschlossen.");
        }
    }

    public Dao<Flughafen, Integer> getFlughafenDao() { return flughafenDao; }
    public Dao<Airline, Integer> getAirlineDao() { return airlineDao; }
    public Dao<Flugzeug, Integer> getFlugzeugDao() { return flugzeugDao; }
    public Dao<Route, Integer> getRouteDao() { return routeDao; }
    public Dao<Flug, Integer> getFlugDao() { return flugDao; }
    public Dao<Wartung, Integer> getWartungDao() { return wartungDao; }
}
