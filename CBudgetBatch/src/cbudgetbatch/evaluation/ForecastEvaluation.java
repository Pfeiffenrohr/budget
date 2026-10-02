package cbudgetbatch.evaluation;

import cbudgetbatch.DBBatch;
import sonstiges.MyLogger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Wertet fruehere Forecasts gegen die tatsaechlichen Werte aus.
 *
 * Kernregel der Auswertung: verglichen wird nur gegen den Stand, der vor
 * Beginn des prognostizierten Zeitraums erstellt wurde. Ein Forecast, der
 * im laufenden Monat neu gerechnet wurde, kennt die Daten dieses Monats
 * und wuerde sich selbst immer bestaetigen.
 *
 * Aufruf: ForecastEvaluation &lt;user&gt; &lt;password&gt; &lt;connectstring&gt;
 */
public class ForecastEvaluation {

    private static final MyLogger logger = new MyLogger();

    public static final String SETTING_TOLERANZ = "forecast_eval_toleranz";
    public static final String SETTING_MONATE = "forecast_eval_monate";
    public static final int STANDARD_MONATE = 6;

    /**
     * Zaehler fuer einen Lauf. Dient Logging und Test gleichermassen.
     */
    public static class Ergebnis {
        private final List<LocalDate> bearbeiteteZeitraeume = new ArrayList<LocalDate>();
        private final Map<String, Integer> statusZaehler = new TreeMap<String, Integer>();
        private final List<Auswertungszeile> zeilen = new ArrayList<Auswertungszeile>();
        private int anzahlOhneSnapshot;
        private double toleranz = ForecastMetricsCalculator.STANDARD_TOLERANZ;

        public List<LocalDate> getBearbeiteteZeitraeume() {
            return bearbeiteteZeitraeume;
        }

        /** Anzahl Zeilen je Status, etwa {ok=12, kein_ist=1}. */
        public Map<String, Integer> getStatusZaehler() {
            return statusZaehler;
        }

        public int getAnzahlZeilen() {
            return zeilen.size();
        }

        /** Paare, fuer die es keinen Stand vor Zeitraumbeginn gab. */
        public int getAnzahlOhneSnapshot() {
            return anzahlOhneSnapshot;
        }

        public double getToleranz() {
            return toleranz;
        }

        /** Alle geschriebenen Zeilen, fuer Kennzahlen und Tests. */
        public List<Auswertungszeile> getZeilen() {
            return zeilen;
        }

        void zaehleStatus(String status) {
            Integer bisher = statusZaehler.get(status);
            statusZaehler.put(status, bisher == null ? 1 : bisher + 1);
        }
    }

    private final ForecastEvaluationDb db;
    private final LocalDate heute;

    public ForecastEvaluation(ForecastEvaluationDb db, LocalDate heute) {
        this.db = db;
        this.heute = heute;
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("usage: ForecastEvaluation <user> <password> <connectstring>");
            System.exit(1);
        }
        LocalDate heute = LocalDate.now();
        DBBatch dbbatch = new DBBatch();
        if (!dbbatch.dataBaseConnect(args[0], args[1], args[2])) {
            System.err.println("Konnte mich nicht mit der Datenbank verbinden");
            System.exit(1);
        }
        try {
            ForecastEvaluationDb db = new ForecastEvaluationDb(dbbatch);
            Ergebnis ergebnis = new ForecastEvaluation(db, heute).auswerten();
            logger.log("Forecast-Auswertung beendet. Zeitraeume: "
                    + ergebnis.getBearbeiteteZeitraeume() + " Zeilen: " + ergebnis.getAnzahlZeilen()
                    + " Status: " + ergebnis.getStatusZaehler());
        } catch (RuntimeException e) {
            //  Nicht schlucken: ein stiller Fehler wuerde die Auswertung
            //  wochenlang unauffaellig leer aussehen lassen.
            logger.log("Forecast-Auswertung fehlgeschlagen: " + e);
            e.printStackTrace();
            System.exit(1);
        } finally {
            dbbatch.closeConnection();
        }
    }

    /**
     * Wertet alle beendeten Zeitraeume im rollierenden Fenster aus.
     *
     * Wiederholt sich jeder Tag, wodurch spaet gebuchte Umsaetze noch in
     * bereits ausgewertete Zeitraeume nachwachsen. Deshalb ist das Schreiben
     * als Aktualisierung angelegt und nicht als Einfuegen.
     */
    public Ergebnis auswerten() {
        long start = System.currentTimeMillis();
        Ergebnis ergebnis = new Ergebnis();
        ergebnis.toleranz = db.getDoubleSetting(SETTING_TOLERANZ, ForecastMetricsCalculator.STANDARD_TOLERANZ);
        int monate = db.getIntSetting(SETTING_MONATE, STANDARD_MONATE);

        //  Nur Zeitraeume, die vollstaendig abgeschlossen sind. Der laufende
        //  Monat wird nie bewertet, seine Ist-Daten waeren unvollstaendig.
        LocalDate grenze = heute.withDayOfMonth(1);
        List<LocalDate> zeitraeume = db.getAuswertbareZeitraeume(grenze, monate);

        for (LocalDate monatsBeginn : zeitraeume) {
            ergebnis.bearbeiteteZeitraeume.add(monatsBeginn);
            auswertenZeitraum(monatsBeginn, ergebnis);
        }

        logger.log("Auswertung " + zeitraeume.size() + " Zeitraeume in "
                + (System.currentTimeMillis() - start) + " ms, ohne Snapshot: "
                + ergebnis.getAnzahlOhneSnapshot());
        return ergebnis;
    }

    private void auswertenZeitraum(LocalDate monatsBeginn, Ergebnis ergebnis) {
        List<Snapshot> alleSnapshots = db.getSnapshots(monatsBeginn);
        if (alleSnapshots.isEmpty()) {
            return;
        }
        Map<ForecastEvaluationDb.KategorieKonto, Double> istWerte = db.getIstWerte(monatsBeginn);
        Monat monat = Monat.from(monatsBeginn);

        for (Map.Entry<ForecastEvaluationDb.KategorieKonto, List<Snapshot>> paar
                : gruppiereNachKategorieUndKonto(alleSnapshots).entrySet()) {

            ForecastEvaluationDb.KategorieKonto schluessel = paar.getKey();
            Snapshot gueltig = SnapshotSelector.select(paar.getValue(), monatsBeginn);
            if (gueltig == null) {
                //  Kein Stand von vor Zeitraumbeginn. Ohne Forecast-Wert gibt es
                //  nichts zu vergleichen und nichts zu schreiben; die Luecke ist
                //  ueber v_forecast_abdeckung sichtbar.
                ergebnis.anzahlOhneSnapshot++;
                continue;
            }
            Double istWert = istWerte.get(schluessel);
            String status = ForecastMetricsCalculator.status(gueltig.getWert(), istWert);

            Auswertungszeile zeile = new Auswertungszeile(
                    monat.getJahr(), monat.getMonat(), monat.key(),
                    schluessel.getKategorie(), schluessel.getKonto(),
                    gueltig.getErstelltTag(), gueltig.getWert(), istWert, status);

            db.speichereAuswertung(zeile, heute);
            ergebnis.zeilen.add(zeile);
            ergebnis.zaehleStatus(status);
        }
    }

    private Map<ForecastEvaluationDb.KategorieKonto, List<Snapshot>> gruppiereNachKategorieUndKonto(
            List<Snapshot> snapshots) {
        Map<ForecastEvaluationDb.KategorieKonto, List<Snapshot>> gruppen =
                new LinkedHashMap<ForecastEvaluationDb.KategorieKonto, List<Snapshot>>();
        for (Snapshot snapshot : snapshots) {
            ForecastEvaluationDb.KategorieKonto schluessel =
                    new ForecastEvaluationDb.KategorieKonto(snapshot.getKategorie(), snapshot.getKonto());
            List<Snapshot> gruppe = gruppen.get(schluessel);
            if (gruppe == null) {
                gruppe = new ArrayList<Snapshot>();
                gruppen.put(schluessel, gruppe);
            }
            gruppe.add(snapshot);
        }
        return gruppen;
    }
}