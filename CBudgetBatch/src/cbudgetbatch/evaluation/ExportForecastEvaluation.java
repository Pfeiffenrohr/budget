package cbudgetbatch.evaluation;

import cbudgetbatch.DBBatch;
import sonstiges.FileHandling;
import sonstiges.MyLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Exportiert die Forecast-Auswertung als CSV, damit sie auch ohne Datenbank-
 * ansicht ausgewertet werden kann.
 *
 * Aufruf: ExportForecastEvaluation &lt;user&gt; &lt;password&gt; &lt;connectstring&gt; &lt;datei&gt; [jahr] [kategorie] [konto]
 *
 * Getrennt von der Auswertung, weil der Export selten ist und nicht jeden
 * Tag laufen soll.
 */
public class ExportForecastEvaluation {

    private static final MyLogger logger = new MyLogger();

    /** Spaltenreihenfolge der CSV-Datei. */
    public static final List<String> SPALTEN = Arrays.asList(
            "jahr", "monat", "monat_key", "kategorie_id", "kategorie",
            "konto_id", "konto", "snapshot_tag", "forecast_wert", "ist_wert",
            "abweichung", "abweichung_prozent", "status");

    private static final String TRENNER = ";";

    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("usage: ExportForecastEvaluation <user> <password> <connectstring>"
                    + " <datei> [jahr] [kategorie] [konto]");
            System.exit(1);
        }
        DBBatch dbbatch = new DBBatch();
        if (!dbbatch.dataBaseConnect(args[0], args[1], args[2])) {
            System.err.println("Konnte mich nicht mit der Datenbank verbinden");
            System.exit(1);
        }
        try {
            Integer jahr = parseOptionalInt(args, 4);
            Integer kategorie = parseOptionalInt(args, 5);
            Integer konto = parseOptionalInt(args, 6);
            ForecastEvaluationDb db = new ForecastEvaluationDb(dbbatch);
            List<Map<String, String>> zeilen = db.getExportZeilen(jahr, kategorie, konto);
            int anzahl = schreibe(args[3], zeilen);
            logger.log("Export geschrieben: " + args[3] + " mit " + anzahl + " Zeilen");
        } finally {
            dbbatch.closeConnection();
        }
    }

    private static Integer parseOptionalInt(String[] args, int index) {
        if (args.length <= index || args[index] == null || args[index].trim().length() == 0) {
            return null;
        }
        return Integer.valueOf(args[index].trim());
    }

    /**
     * Schreibt die CSV-Datei.
     *
     * FileHandling.writeFile nimmt genau einen String und haengt einen Zeilenumbruch
     * an, deshalb wird hier zeilenweise geschrieben: die erste Zeile ersetzt die
     * Datei, alle weiteren haengen sich an. Das ist langsamer als ein einziger
     * Aufruf, entspricht aber dem Muster, das CalculateForecast.writeData schon nutzt.
     *
     * @return Anzahl Datenzeilen ohne Kopfzeile
     */
    public static int schreibe(String datei, List<Map<String, String>> zeilen) {
        List<String> inhalt = alsCsv(zeilen);
        FileHandling fileHandling = new FileHandling();
        boolean erfolg = true;
        for (int i = 0; i < inhalt.size(); i++) {
            boolean anhaengen = i > 0;
            if (!fileHandling.writeFile(datei, inhalt.get(i), anhaengen)) {
                erfolg = false;
                break;
            }
        }
        if (!erfolg) {
            throw new IllegalStateException("Export konnte nicht nach " + datei + " geschrieben werden");
        }
        return Math.max(0, inhalt.size() - 1);
    }

    /**
     * Baut den CSV-Inhalt, Kopfzeile inklusive.
     *
     * Dezimaltrennzeichen bleiben unveraendert. Die Werte kommen aus
     * numeric-Spalten und damit mit Punkt als Trenner; ein Umstellen auf das
     * lokale Dezimalkomma wuerde die Zahlen beim Wiedereinlesen beschaedigen.
     * Fuer die Darstellung ist die Oberflaeche zustaendig.
     */
    public static List<String> alsCsv(List<Map<String, String>> zeilen) {
        List<String> inhalt = new ArrayList<String>();
        inhalt.add(kopfzeile());
        if (zeilen != null) {
            for (Map<String, String> zeile : zeilen) {
                inhalt.add(zeile(zeile));
            }
        }
        return inhalt;
    }

    static String kopfzeile() {
        return verbinde(SPALTEN);
    }

    static String zeile(Map<String, String> werte) {
        List<String> felder = new ArrayList<String>(SPALTEN.size());
        for (String spalte : SPALTEN) {
            String wert = werte.get(spalte);
            felder.add(wert == null ? "" : wert);
        }
        return verbinde(felder);
    }

    private static String verbinde(List<String> felder) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < felder.size(); i++) {
            if (i > 0) {
                sb.append(TRENNER);
            }
            sb.append(felder.get(i));
        }
        return sb.toString();
    }
}