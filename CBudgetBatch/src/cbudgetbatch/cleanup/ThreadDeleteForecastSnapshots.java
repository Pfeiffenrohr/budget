package cbudgetbatch.cleanup;

import cbudgetbatch.DBBatch;
import cbudgetbatch.evaluation.ForecastEvaluationDb;
import sonstiges.MyLogger;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

/**
 * Haelt forecast_snapshot in Groessenordnung.
 *
 * Der Snapshot waechst ohne Aufrueumen um rund dreihunderttausend Zeilen pro
 * Jahr, weil jeder Forecast-Lauf fuer 24 rollierende Monate je Kategorie und
 * Konto wieder einen Stand schreibt. Das sind bei einer Aufbewahrung von
 * drei Jahren rund eine Million Zeilen.
 *
 * Aufruf: ThreadDeleteForecastSnapshots &lt;user&gt; &lt;password&gt; &lt;connectstring&gt;
 */
public class ThreadDeleteForecastSnapshots extends Thread {

    private static final MyLogger logger = new MyLogger();

    public static final String SETTING_RETENTION_JAHRE = "forecast_eval_retention_jahre";
    public static final int STANDARD_RETENTION_JAHRE = 3;

    /** Ein Tag. Einmal pro Tag reicht, die Datenmenge ist klein. */
    public static final long INTERVALL_MINUTEN = 60 * 24;

    private final ForecastEvaluationDb db;

    public ThreadDeleteForecastSnapshots(ForecastEvaluationDb db) {
        this.db = db;
        setName("deleteForecastSnapshots");
        setDaemon(false);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("usage: ThreadDeleteForecastSnapshots <user> <password> <connectstring>");
            System.exit(1);
        }
        DBBatch dbbatch = new DBBatch();
        if (!dbbatch.dataBaseConnect(args[0], args[1], args[2])) {
            System.err.println("Konnte mich nicht mit der Datenbank verbinden");
            System.exit(1);
        }
        new ThreadDeleteForecastSnapshots(new ForecastEvaluationDb(dbbatch)).start();
    }

    public void run() {
        while (!isInterrupted()) {
            try {
                aufraeumen(LocalDate.now());
            } catch (RuntimeException e) {
                //  Der Thread darf nicht sterben: sonst waechst die Tabelle
                //  unbegrenzt weiter, ohne dass irgendwo ein Fehler auffaellt.
                logger.log("FEHLER beim Aufraeumen der Snapshots: " + e);
            }
            try {
                TimeUnit.MINUTES.sleep(INTERVALL_MINUTEN);
            } catch (InterruptedException e) {
                logger.log("ThreadDeleteForecastSnapshots wird beendet");
                interrupt();
            }
        }
    }

    /**
     * Ein Durchlauf. Getrennt von run(), damit der Ablauf testbar ist.
     *
     * @return Anzahl geloeschter Snapshot-Zeilen
     */
    public int aufraeumen(LocalDate heute) {
        int jahre = db.getIntSetting(SETTING_RETENTION_JAHRE, STANDARD_RETENTION_JAHRE);
        if (jahre < 1) {
            jahre = 1;
        }
        LocalDate grenze = heute.minusYears(jahre);
        logger.log("Start deleteSnapshots aelter als " + grenze + " (" + jahre + " Jahre) ..");

        db.deleteVerwaisteSnapshots();
        int geloescht = db.deleteSnapshotsAelterAls(grenze);
        db.deleteAuswertungOhneDaten(heute);

        logger.log("deleteSnapshots done, " + geloescht + " Zeilen geloescht");
        return geloescht;
    }
}