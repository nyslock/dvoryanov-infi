package luftfahrt.model;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// Ein konkreter Flug: wird von einem Flugzeug durchgeführt und folgt einer Route.
@DatabaseTable(tableName = "flug")
public class Flug {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "flugzeug_id", foreign = true, foreignAutoRefresh = true)
    private Flugzeug flugzeug;

    @DatabaseField(columnName = "route_id", foreign = true, foreignAutoRefresh = true)
    private Route route;

    @DatabaseField(columnName = "abflug_flughafen_id", foreign = true, foreignAutoRefresh = true)
    private Flughafen abflugFlughafen;

    @DatabaseField(columnName = "ankunft_flughafen_id", foreign = true, foreignAutoRefresh = true)
    private Flughafen ankunftFlughafen;

    @DatabaseField(unique = true)
    private String flugnummer;

    // Datumswerte werden als Text in der DB gespeichert (gut lesbar)
    @DatabaseField(columnName = "geplanter_abflug", dataType = DataType.DATE_STRING)
    private Date geplanterAbflug;

    @DatabaseField(columnName = "geplante_ankunft", dataType = DataType.DATE_STRING)
    private Date geplanteAnkunft;

    @DatabaseField(columnName = "tatsaechlicher_abflug", dataType = DataType.DATE_STRING)
    private Date tatsaechlicherAbflug;

    @DatabaseField(columnName = "tatsaechliche_ankunft", dataType = DataType.DATE_STRING)
    private Date tatsaechlicheAnkunft;

    @DatabaseField
    private String status;

    public Flug() {
    }

    public Flug(Flugzeug flugzeug, Route route, Flughafen abflugFlughafen, Flughafen ankunftFlughafen,
                String flugnummer, Date geplanterAbflug, Date geplanteAnkunft, String status) {
        this.flugzeug = flugzeug;
        this.route = route;
        this.abflugFlughafen = abflugFlughafen;
        this.ankunftFlughafen = ankunftFlughafen;
        this.flugnummer = flugnummer;
        this.geplanterAbflug = geplanterAbflug;
        this.geplanteAnkunft = geplanteAnkunft;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Flugzeug getFlugzeug() { return flugzeug; }
    public void setFlugzeug(Flugzeug flugzeug) { this.flugzeug = flugzeug; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public Flughafen getAbflugFlughafen() { return abflugFlughafen; }
    public void setAbflugFlughafen(Flughafen abflugFlughafen) { this.abflugFlughafen = abflugFlughafen; }

    public Flughafen getAnkunftFlughafen() { return ankunftFlughafen; }
    public void setAnkunftFlughafen(Flughafen ankunftFlughafen) { this.ankunftFlughafen = ankunftFlughafen; }

    public String getFlugnummer() { return flugnummer; }
    public void setFlugnummer(String flugnummer) { this.flugnummer = flugnummer; }

    public Date getGeplanterAbflug() { return geplanterAbflug; }
    public void setGeplanterAbflug(Date geplanterAbflug) { this.geplanterAbflug = geplanterAbflug; }

    public Date getGeplanteAnkunft() { return geplanteAnkunft; }
    public void setGeplanteAnkunft(Date geplanteAnkunft) { this.geplanteAnkunft = geplanteAnkunft; }

    public Date getTatsaechlicherAbflug() { return tatsaechlicherAbflug; }
    public void setTatsaechlicherAbflug(Date tatsaechlicherAbflug) { this.tatsaechlicherAbflug = tatsaechlicherAbflug; }

    public Date getTatsaechlicheAnkunft() { return tatsaechlicheAnkunft; }
    public void setTatsaechlicheAnkunft(Date tatsaechlicheAnkunft) { this.tatsaechlicheAnkunft = tatsaechlicheAnkunft; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        String fz = (flugzeug == null) ? "-" : flugzeug.getKennzeichen();
        String von = (abflugFlughafen == null) ? "?" : abflugFlughafen.getIataCode();
        String nach = (ankunftFlughafen == null) ? "?" : ankunftFlughafen.getIataCode();
        return id + " | " + flugnummer + " | " + von + " -> " + nach
                + " | Flugzeug: " + fz + " | ab: " + geplanterAbflug
                + " | an: " + geplanteAnkunft + " | Status: " + status;
    }
}
