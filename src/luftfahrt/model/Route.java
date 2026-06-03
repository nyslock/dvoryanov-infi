package luftfahrt.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// Eine Route verbindet zwei Flughäfen und gehört einer Airline.
@DatabaseTable(tableName = "route")
public class Route {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "airline_id", foreign = true, foreignAutoRefresh = true)
    private Airline airline;

    @DatabaseField(columnName = "abflug_flughafen_id", foreign = true, foreignAutoRefresh = true)
    private Flughafen abflugFlughafen;

    @DatabaseField(columnName = "ziel_flughafen_id", foreign = true, foreignAutoRefresh = true)
    private Flughafen zielFlughafen;

    @DatabaseField(columnName = "routennummer", unique = true)
    private String routennummer;

    @DatabaseField(columnName = "distanz_km")
    private int distanzKm;

    @DatabaseField
    private boolean aktiv;

    public Route() {
    }

    public Route(Airline airline, Flughafen abflugFlughafen, Flughafen zielFlughafen,
                 String routennummer, int distanzKm, boolean aktiv) {
        this.airline = airline;
        this.abflugFlughafen = abflugFlughafen;
        this.zielFlughafen = zielFlughafen;
        this.routennummer = routennummer;
        this.distanzKm = distanzKm;
        this.aktiv = aktiv;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Airline getAirline() { return airline; }
    public void setAirline(Airline airline) { this.airline = airline; }

    public Flughafen getAbflugFlughafen() { return abflugFlughafen; }
    public void setAbflugFlughafen(Flughafen abflugFlughafen) { this.abflugFlughafen = abflugFlughafen; }

    public Flughafen getZielFlughafen() { return zielFlughafen; }
    public void setZielFlughafen(Flughafen zielFlughafen) { this.zielFlughafen = zielFlughafen; }

    public String getRoutennummer() { return routennummer; }
    public void setRoutennummer(String routennummer) { this.routennummer = routennummer; }

    public int getDistanzKm() { return distanzKm; }
    public void setDistanzKm(int distanzKm) { this.distanzKm = distanzKm; }

    public boolean isAktiv() { return aktiv; }
    public void setAktiv(boolean aktiv) { this.aktiv = aktiv; }

    @Override
    public String toString() {
        String al = (airline == null) ? "-" : airline.getName();
        String von = (abflugFlughafen == null) ? "?" : abflugFlughafen.getIataCode();
        String nach = (zielFlughafen == null) ? "?" : zielFlughafen.getIataCode();
        return id + " | " + routennummer + " | " + von + " -> " + nach
                + " | " + al + " | " + distanzKm + " km | " + (aktiv ? "aktiv" : "inaktiv");
    }
}
