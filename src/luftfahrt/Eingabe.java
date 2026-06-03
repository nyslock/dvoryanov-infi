package luftfahrt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Kleine Helfer-Klasse für Konsoleneingaben.
 * Liest sicher Text, Zahlen und Datum von der Tastatur ein.
 * Es wird ein einziger gemeinsamer Scanner für System.in verwendet.
 */
public class Eingabe {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final SimpleDateFormat DATUM = new SimpleDateFormat("yyyy-MM-dd");

    private Eingabe() {
        // keine Objekte nötig - nur statische Methoden
    }

    // Liest eine ganze Textzeile
    public static String text(String frage) {
        System.out.print(frage);
        return SCANNER.nextLine().trim();
    }

    // Liest eine ganze Zahl, fragt bei Fehler erneut
    public static int zahl(String frage) {
        while (true) {
            String s = text(frage);
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Bitte eine ganze Zahl eingeben.");
            }
        }
    }

    // Liest eine Kommazahl, fragt bei Fehler erneut
    public static double kommazahl(String frage) {
        while (true) {
            String s = text(frage).replace(',', '.');
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                System.out.println("Bitte eine Zahl eingeben (z. B. 12.5).");
            }
        }
    }

    // Liest ja/nein
    public static boolean jaNein(String frage) {
        String s = text(frage + " (j/n): ").toLowerCase();
        return s.startsWith("j");
    }

    // Liest ein Datum im Format yyyy-MM-dd (leer = null)
    public static Date datum(String frage) {
        while (true) {
            String s = text(frage + " (yyyy-MM-dd, leer = keine Angabe): ");
            if (s.isEmpty()) {
                return null;
            }
            try {
                return DATUM.parse(s);
            } catch (ParseException e) {
                System.out.println("Bitte Datum als yyyy-MM-dd eingeben.");
            }
        }
    }
}
