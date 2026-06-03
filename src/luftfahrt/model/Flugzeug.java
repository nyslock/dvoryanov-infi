package luftfahrt.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// Ein Flugzeug. Gehört einer Airline und hat einen Heimat-Flughafen.
@DatabaseTable(tableName = "flugzeug")
public class Flugzeug {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "airline_id", foreign = true, foreignAutoRefresh = true)
    private Airline airline;

    @DatabaseField(columnName = "heimat_flughafen_id", foreign = true, foreignAutoRefresh = true)
    private Flughafen heimatFlughafen;

    @DatabaseField(unique = true)
    private String kennzeichen;

    @DatabaseField
    private String modell;

    @DatabaseField
    private String hersteller;

    @DatabaseField
    private int baujahr;

    @DatabaseField
    private int sitzanzahl;

    @DatabaseField(columnName = "flugstunden_gesamt")
    private double flugstundenGesamt;

    public Flugzeug() {
    }

    public Flugzeug(Airline airline, Flughafen heimatFlughafen, String kennzeichen, String modell,
                    String hersteller, int baujahr, int sitzanzahl, double flugstundenGesamt) {
        this.airline = airline;
        this.heimatFlughafen = heimatFlughafen;
        this.kennzeichen = kennzeichen;
        this.modell = modell;
        this.hersteller = hersteller;
        this.baujahr = baujahr;
        this.sitzanzahl = sitzanzahl;
        this.flugstundenGesamt = flugstundenGesamt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Airline getAirline() { return airline; }
    public void setAirline(Airline airline) { this.airline = airline; }

    public Flughafen getHeimatFlughafen() { return heimatFlughafen; }
    public void setHeimatFlughafen(Flughafen heimatFlughafen) { this.heimatFlughafen = heimatFlughafen; }

    public String getKennzeichen() { return kennzeichen; }
    public void setKennzeichen(String kennzeichen) { this.kennzeichen = kennzeichen; }

    public String getModell() { return modell; }
    public void setModell(String modell) { this.modell = modell; }

    public String getHersteller() { return hersteller; }
    public void setHersteller(String hersteller) { this.hersteller = hersteller; }

    public int getBaujahr() { return baujahr; }
    public void setBaujahr(int baujahr) { this.baujahr = baujahr; }

    public int getSitzanzahl() { return sitzanzahl; }
    public void setSitzanzahl(int sitzanzahl) { this.sitzanzahl = sitzanzahl; }

    public double getFlugstundenGesamt() { return flugstundenGesamt; }
    public void setFlugstundenGesamt(double flugstundenGesamt) { this.flugstundenGesamt = flugstundenGesamt; }

    @Override
    public String toString() {
        String al = (airline == null) ? "-" : airline.getName();
        String heimat = (heimatFlughafen == null) ? "-" : heimatFlughafen.getIataCode();
        return id + " | " + kennzeichen + " | " + hersteller + " " + modell + " (" + baujahr + ")"
                + " | Airline: " + al + " | Heimat: " + heimat
                + " | Sitze=" + sitzanzahl + " | Flugstunden=" + flugstundenGesamt;
    }
}
