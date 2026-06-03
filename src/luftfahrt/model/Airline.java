package luftfahrt.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// Eine Airline (z. B. Wizz Air). Hat genau einen Basis-Flughafen.
@DatabaseTable(tableName = "airline")
public class Airline {

    @DatabaseField(generatedId = true)
    private int id;

    // Fremdschlüssel: zeigt auf einen Flughafen. foreignAutoRefresh lädt das ganze Objekt mit.
    @DatabaseField(columnName = "basis_flughafen_id", foreign = true, foreignAutoRefresh = true)
    private Flughafen basisFlughafen;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(columnName = "icao_code")
    private String icaoCode;

    @DatabaseField(columnName = "iata_code")
    private String iataCode;

    @DatabaseField
    private String hauptsitz;

    @DatabaseField
    private int gruendungsjahr;

    public Airline() {
    }

    public Airline(Flughafen basisFlughafen, String name, String icaoCode, String iataCode,
                   String hauptsitz, int gruendungsjahr) {
        this.basisFlughafen = basisFlughafen;
        this.name = name;
        this.icaoCode = icaoCode;
        this.iataCode = iataCode;
        this.hauptsitz = hauptsitz;
        this.gruendungsjahr = gruendungsjahr;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Flughafen getBasisFlughafen() { return basisFlughafen; }
    public void setBasisFlughafen(Flughafen basisFlughafen) { this.basisFlughafen = basisFlughafen; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIcaoCode() { return icaoCode; }
    public void setIcaoCode(String icaoCode) { this.icaoCode = icaoCode; }

    public String getIataCode() { return iataCode; }
    public void setIataCode(String iataCode) { this.iataCode = iataCode; }

    public String getHauptsitz() { return hauptsitz; }
    public void setHauptsitz(String hauptsitz) { this.hauptsitz = hauptsitz; }

    public int getGruendungsjahr() { return gruendungsjahr; }
    public void setGruendungsjahr(int gruendungsjahr) { this.gruendungsjahr = gruendungsjahr; }

    @Override
    public String toString() {
        String basis = (basisFlughafen == null) ? "-" : basisFlughafen.getName();
        return id + " | " + name + " (" + iataCode + "/" + icaoCode + ") | Basis: " + basis
                + " | " + hauptsitz + " | gegr. " + gruendungsjahr;
    }
}
