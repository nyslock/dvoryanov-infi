package luftfahrt.model;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// Ein Wartungsauftrag: gehört einem Flugzeug und findet an einem Flughafen statt.
@DatabaseTable(tableName = "wartung")
public class Wartung {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "flugzeug_id", foreign = true, foreignAutoRefresh = true)
    private Flugzeug flugzeug;

    @DatabaseField(columnName = "flughafen_id", foreign = true, foreignAutoRefresh = true)
    private Flughafen flughafen;

    @DatabaseField(dataType = DataType.DATE_STRING)
    private Date datum;

    @DatabaseField
    private String typ;

    @DatabaseField
    private String beschreibung;

    @DatabaseField
    private String techniker;

    @DatabaseField
    private String status;

    @DatabaseField
    private double kosten;

    public Wartung() {
    }

    public Wartung(Flugzeug flugzeug, Flughafen flughafen, Date datum, String typ,
                   String beschreibung, String techniker, String status, double kosten) {
        this.flugzeug = flugzeug;
        this.flughafen = flughafen;
        this.datum = datum;
        this.typ = typ;
        this.beschreibung = beschreibung;
        this.techniker = techniker;
        this.status = status;
        this.kosten = kosten;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Flugzeug getFlugzeug() { return flugzeug; }
    public void setFlugzeug(Flugzeug flugzeug) { this.flugzeug = flugzeug; }

    public Flughafen getFlughafen() { return flughafen; }
    public void setFlughafen(Flughafen flughafen) { this.flughafen = flughafen; }

    public Date getDatum() { return datum; }
    public void setDatum(Date datum) { this.datum = datum; }

    public String getTyp() { return typ; }
    public void setTyp(String typ) { this.typ = typ; }

    public String getBeschreibung() { return beschreibung; }
    public void setBeschreibung(String beschreibung) { this.beschreibung = beschreibung; }

    public String getTechniker() { return techniker; }
    public void setTechniker(String techniker) { this.techniker = techniker; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getKosten() { return kosten; }
    public void setKosten(double kosten) { this.kosten = kosten; }

    @Override
    public String toString() {
        String fz = (flugzeug == null) ? "-" : flugzeug.getKennzeichen();
        String fh = (flughafen == null) ? "-" : flughafen.getIataCode();
        return id + " | " + datum + " | " + typ + " | Flugzeug: " + fz + " @ " + fh
                + " | Techniker: " + techniker + " | " + status + " | " + kosten + " EUR";
    }
}
