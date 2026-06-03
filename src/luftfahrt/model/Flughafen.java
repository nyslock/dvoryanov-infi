package luftfahrt.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// Ein Flughafen (z. B. Wien-Schwechat). ORMLite legt daraus die Tabelle "flughafen" an.
@DatabaseTable(tableName = "flughafen")
public class Flughafen {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(columnName = "iata_code", unique = true)
    private String iataCode;

    @DatabaseField
    private String stadt;

    @DatabaseField
    private String land;

    @DatabaseField
    private int terminals;

    @DatabaseField
    private String zeitzone;

    // Leerer Konstruktor wird von ORMLite benötigt
    public Flughafen() {
    }

    public Flughafen(String name, String iataCode, String stadt, String land, int terminals, String zeitzone) {
        this.name = name;
        this.iataCode = iataCode;
        this.stadt = stadt;
        this.land = land;
        this.terminals = terminals;
        this.zeitzone = zeitzone;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIataCode() { return iataCode; }
    public void setIataCode(String iataCode) { this.iataCode = iataCode; }

    public String getStadt() { return stadt; }
    public void setStadt(String stadt) { this.stadt = stadt; }

    public String getLand() { return land; }
    public void setLand(String land) { this.land = land; }

    public int getTerminals() { return terminals; }
    public void setTerminals(int terminals) { this.terminals = terminals; }

    public String getZeitzone() { return zeitzone; }
    public void setZeitzone(String zeitzone) { this.zeitzone = zeitzone; }

    @Override
    public String toString() {
        return id + " | " + name + " (" + iataCode + ") | " + stadt + ", " + land
                + " | Terminals=" + terminals + " | " + zeitzone;
    }
}
