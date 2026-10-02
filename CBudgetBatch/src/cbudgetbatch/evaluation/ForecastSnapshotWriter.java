package cbudgetbatch.evaluation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Sammelt die Monatsprognosen eines Forecast-Laufs und schreibt sie als
 * historischen Stand in forecast_snapshot.
 *
 * Forecast.java loescht vor jedem Lauf die alten Forecast-Zeilen und rechnet
 * sie neu. Ohne diesen Writer waere der Stand vor dem Lauf nicht mehr
 * rekonstruierbar. Der Writer haengt deshalb in die Tagesschleife des
 * Laufs und summiert die Tageswerte, waehrend sie entstehen - dadurch
 * entfaellt eine zusaetzliche Datenbankabfrage.
 *
 * Der Horizont ist bewusst rollierend und begrenzt (Vorgabe: 24 Monate).
 * Ohne Begrenzung wuerden 30 Jahre Vorlauf je Kategorie und Konto
 * gespeichert, also rund fuenfundvierzigtausend Zeilen pro Lauf.
 */
public class ForecastSnapshotWriter {

    public static final int STANDARD_HORIZONT_MONATE = 24;

    public static final String SETTING_HORIZONT = "forecast_eval_horizont";
    public static final String SETTING_ALGORITHMUS = "forecast_eval_algorithmus";
    public static final String STANDARD_ALGORITHMUS = "v1";

    private final int horizontMonate;
    private final TreeMap<LocalDate, Double> monatsSummen = new TreeMap<LocalDate, Double>();

    /**
     * @param horizontMonate so viele Monate ab dem laufenden Monat gehalten werden
     */
    public ForecastSnapshotWriter(int horizontMonate) {
        this.horizontMonate = Math.max(1, horizontMonate);
    }

    public static ForecastSnapshotWriter ausSettings(ForecastEvaluationDb db) {
        return new ForecastSnapshotWriter(
                db.getIntSetting(SETTING_HORIZONT, STANDARD_HORIZONT_MONATE));
    }

    public int getHorizontMonate() {
        return horizontMonate;
    }

    /**
     * Uebernimmt den Tageswert in den laufenden Prognosemonat.
     * Mehrere Tage desselben Monats werden addiert.
     */
    public void addTag(LocalDate datum, double wert) {
        LocalDate monatsBeginn = datum.withDayOfMonth(1);
        Double summe = monatsSummen.get(monatsBeginn);
        monatsSummen.put(monatsBeginn, summe == null ? wert : summe + wert);
    }

    public Map<LocalDate, Double> getMonatsSummen() {
        return monatsSummen;
    }

    /**
     * Liefert die Monate innerhalb des Horizonts.
     *
     * Ein Rollierendfenster ohne Luecken ist wichtig: Kaum noch gelaufene
     * Prognosen sind keine brauchbare historische Referenz.
     */
    public Map<LocalDate, Double> getMonatsSummenImHorizont(LocalDate heute) {
        LocalDate ersterMonat = heute.withDayOfMonth(1);
        LocalDate letzterMonat = ersterMonat.plusMonths(horizontMonate);
        Map<LocalDate, Double> ergebnis = new LinkedHashMap<LocalDate, Double>();
        for (Map.Entry<LocalDate, Double> eintrag : monatsSummen.entrySet()) {
            LocalDate monat = eintrag.getKey();
            if (!monat.isBefore(ersterMonat) && monat.isBefore(letzterMonat)) {
                ergebnis.put(monat, eintrag.getValue());
            }
        }
        return ergebnis;
    }

    /**
     * Wandelt einen Kategorie/Konto-Durchlauf in Snapshot-Zeilen um.
     * Aufrufer sammelt erst den kompletten Lauf, damit die Zeilen eines
     * Laufs denselben erstellt_tag tragen.
     */
    public List<ForecastEvaluationDb.SnapshotZeile> zeilenFuer(int kategorie, int konto, LocalDate heute) {
        List<ForecastEvaluationDb.SnapshotZeile> zeilen =
                new ArrayList<ForecastEvaluationDb.SnapshotZeile>();
        for (Map.Entry<LocalDate, Double> eintrag : getMonatsSummenImHorizont(heute).entrySet()) {
            zeilen.add(new ForecastEvaluationDb.SnapshotZeile(kategorie, konto, eintrag.getKey(), eintrag.getValue()));
        }
        return zeilen;
    }
}