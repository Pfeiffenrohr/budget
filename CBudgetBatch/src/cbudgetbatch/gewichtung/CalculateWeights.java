package cbudgetbatch.gewichtung;

import cbudgetbatch.DBBatch;
import sonstiges.MyLogger;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class CalculateWeights {
    private Map<Integer, YearTable> maps;

    public Map<Integer, YearTable> getMaps() {
        return maps;
    }

    public void setMaps(Map<Integer, YearTable> maps) {
        this.maps = maps;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getKonto() {
        return konto;
    }

    public void setKonto(int konto) {
        this.konto = konto;
    }

    private int category;
    private int konto;
    private DBBatch db;

    public DBBatch getDb() {
        return db;
    }

    public void setDb(DBBatch db) {
        this.db = db;
    }

private static final MyLogger logger = new MyLogger();

    /** Anzahl Tageswerte je Jahr (Index 0..366). */
    public static final int TAGE_PRO_JAHR = 366;

    /** Anzahl Jahre, die in die Gewichtssuche einfliessen (offset 0..5). */
    private static final int ANZAHL_JAHRE = 8;

    /** Anzahl Zieljahre, gegen die optimiert wird (offset 1..2). */
    private static final int ANZAHL_ZIELJAHRE = 2;

    /** Rohgewichte werden in 100 Schritten gesucht (y1/50 ergibt den Anteil). */
    private static final int GEWICHT_SCHRITTE = 100;

    /** Teiler, mit dem ein Rohgewicht in den Prozentanteil umgerechnet wird. */
    private static final double GEWICHTS_TEILER = 50.0;

    /** Summen unterhalb dieser Grenze gelten als "praktisch leer". */
    private static final double NULL_GRENZE = 0.01;

    public CalculateWeights(Map<Integer, YearTable> maps, int category, int konto, DBBatch db) {
        this.maps = maps;
        this.category = category;
        this.konto = konto;
        this.db = db;
    }
    public CalculateWeights() {
    }

    public boolean calculate(Map<Integer, YearTable> maps) {
        return calculate(maps, Calendar.getInstance().get(Calendar.YEAR));
    }

    public boolean calculate(Map<Integer, YearTable> maps, int aktYear) {
        this.maps = maps;
        double[][] tagesWerte = toArrays(maps, aktYear);
        /*
        Wenn drei Jahre die Summe null war, dann ist die Wahrscheinlichkeit groß,
        dass sie auch null wird. Dann wird nichts gespeichert.
        */
        if (Math.abs(summeJahr(tagesWerte, 1)) < NULL_GRENZE &&
                Math.abs(summeJahr(tagesWerte, 2)) < NULL_GRENZE &&
                Math.abs(summeJahr(tagesWerte, 3)) < NULL_GRENZE) {

            return true;
        }
        Weights weights = findWeights(tagesWerte);
        logger.log(" " + weights.toString());

        Hashtable hash = new Hashtable();
        hash.put("category", category);
        hash.put("konto", konto);
        hash.put("y1", (int) weights.getY1());
        hash.put("y2", (int) weights.getY2());
        hash.put("y3", (int) weights.getY3());
        hash.put("precision", weights.getPrecision());
        db.insertForecastWeights(hash);
        return true;
    }

    /**
     * Brute-Force-Suche ueber das Gitter 0..99 x 0..99 x 0..99.
     *
     * Reihenfolge der Schachtelung und der Summenbildung sind bewusst
     * identisch zur urspruenglichen Map-basierten Variante, damit die
     * Ergebnisse bitweise gleich bleiben. Nur die Datenhaltung ist von
     * HashMap mit Boxing auf primitive Arrays umgestellt, was die Laufzeit
     * von rund 49 Sekunden auf unter einer Sekunde bringt.
     *
     * @param tagesWerte Index ist der Jahresoffset (0 = aktuelles Jahr),
     *                   Wert ist die Reihe der Tageswerte 0..366.
     */
    public Weights findWeights(double[][] tagesWerte) {
        double[] zielSummen = new double[ANZAHL_ZIELJAHRE];
        double[][][] zielTage = new double[ANZAHL_ZIELJAHRE][3][];
        for (int i = 0; i < ANZAHL_ZIELJAHRE; i++) {
            int offset = i + 1;
            zielSummen[i] = summeJahr(tagesWerte, offset);
            zielTage[i][0] = reihe(tagesWerte, offset + 1);
            zielTage[i][1] = reihe(tagesWerte, offset + 2);
            zielTage[i][2] = reihe(tagesWerte, offset + 3);
        }

        double differenzMax = 999999999;
        int y1max = 0;
        int y2max = 0;
        int y3max = 0;
        for (int y1 = 0; y1 < GEWICHT_SCHRITTE; y1++) {
            double w1 = y1 / GEWICHTS_TEILER;
            for (int y2 = 0; y2 < GEWICHT_SCHRITTE; y2++) {
                double w2 = y2 / GEWICHTS_TEILER;
                for (int y3 = 0; y3 < GEWICHT_SCHRITTE; y3++) {
                    double w3 = y3 / GEWICHTS_TEILER;
                    double differenzAll = 0.0;
                    for (int i = 0; i < ANZAHL_ZIELJAHRE; i++) {
                        double avgSum = 0.0;
                        double[] a = zielTage[i][0];
                        double[] b = zielTage[i][1];
                        double[] c = zielTage[i][2];
                        for (int k = 0; k <= TAGE_PRO_JAHR; k++) {
                            avgSum += a[k] * w1 + b[k] * w2 + c[k] * w3;
                        }
                        double differenz = avgSum - zielSummen[i];
                        differenzAll += differenz * differenz;
                    }
                    if (differenzAll < differenzMax) {
                        y1max = y1;
                        y2max = y2;
                        y3max = y3;
                        differenzMax = differenzAll;
                    }
                }
            }
        }
        if (y1max == 0 && y2max == 0 && y3max == 0) {
            y1max = 50;
        }
        return new Weights(y1max, y2max, y3max, differenzMax);
    }

    /**
     * Wandelt die YearTable-Map in Arrays um. Index ist der Jahresoffset
     * relativ zum aktuellen Jahr, 0 also das laufende Jahr.
     */
    private double[][] toArrays(Map<Integer, YearTable> maps, int aktYear) {
        double[][] tagesWerte = new double[ANZAHL_JAHRE][];
        for (int offset = 0; offset < ANZAHL_JAHRE; offset++) {
            YearTable yearTable = maps.get(aktYear - offset);
            if (yearTable == null || yearTable.getMapYear() == null) {
                continue;
            }
            double[] reihe = new double[TAGE_PRO_JAHR + 1];
            for (int k = 0; k <= TAGE_PRO_JAHR; k++) {
                Double wert = yearTable.getMapYear().get(k);
                if (wert != null) {
                    reihe[k] = wert;
                }
            }
            tagesWerte[offset] = reihe;
        }
        return tagesWerte;
    }

    private static double[] reihe(double[][] tagesWerte, int offset) {
        double[] reihe = tagesWerte[offset];
        return reihe == null ? new double[TAGE_PRO_JAHR + 1] : reihe;
    }

    private static double summeJahr(double[][] tagesWerte, int offset) {
        double[] reihe = reihe(tagesWerte, offset);
        double sum = 0.0;
        for (int k = 0; k <= TAGE_PRO_JAHR; k++) {
            sum += reihe[k];
        }
        return sum;
    }
}
