package cbudgetbatch.forecastnew;

import cbudgetbatch.DBBatch;
import cbudgetbatch.gewichtung.CalculateWeights;
import cbudgetbatch.gewichtung.Weights;
import cbudgetbatch.gewichtung.YearTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Gewichtssuche. Die Testdaten sind sieben identische Jahre, in denen nur der
 * zweite Tag einen Wert traegt. Damit ist das Optimum exakt bekannt: die
 * Gewichte muessen in der Summe 1 ergeben, und weil alle Jahre gleich sind,
 * ist jede Verteilung gleich gut. Die Suche nimmt die erste gefundene
 * Loesung, also 0/0/50.
 */
public class CalculateWeightsTest {

    private static final double DELTA = 1e-9;
    private static final int AKTJAHR = 2024;

    private DBBatch db;
    private CalculateWeights calculateWeights;

    @BeforeEach
    public void setUp() {
        db = mock(DBBatch.class);
        when(db.insertForecastWeights(any(Hashtable.class))).thenReturn(true);
        calculateWeights = new CalculateWeights();
        calculateWeights.setKonto(7);
        calculateWeights.setCategory(3);
        calculateWeights.setDb(db);
    }

    @Test
    public void gewichteErgebenInDerSummeEins() {
        Weights weights = calculateWeights.findWeights(tagesWerte());

        assertEquals(0, weights.getY1());
        assertEquals(0, weights.getY2());
        assertEquals(50, weights.getY3());
        assertEquals(50, weights.getY1() + weights.getY2() + weights.getY3(),
                "Die Rohgewichte ergeben zusammen 50, also 100 Prozent");
        assertEquals(0.0, weights.getPrecision(), DELTA,
                "Identische Jahre lassen sich fehlerfrei nachbilden");
    }

    @Test
    public void speichertGefundeneGewichte() {
        ArgumentCaptor<Hashtable> captor = ArgumentCaptor.forClass(Hashtable.class);

        calculateWeights.calculate(jahrestabelle(), AKTJAHR);

        verify(db).insertForecastWeights(captor.capture());
        Hashtable gespeichert = captor.getValue();
        assertEquals(3, gespeichert.get("category"));
        assertEquals(7, gespeichert.get("konto"));
        assertEquals(0, gespeichert.get("y1"));
        assertEquals(0, gespeichert.get("y2"));
        assertEquals(50, gespeichert.get("y3"));
        assertEquals(0.0, (Double) gespeichert.get("precision"), DELTA);
    }

    @Test
    public void leereJahreSpeichernNichts() {
        //  Drei leere Jahre hintereinander heissen: es wird auch weiterhin
        //  nichts gebucht. Ein Datensatz waere hier irrefuehrend.
        Map<Integer, YearTable> maps = new HashMap<Integer, YearTable>();
        for (int jahr = AKTJAHR; jahr > AKTJAHR - 8; jahr--) {
            maps.put(jahr, leeresJahr());
        }

        assertTrue(calculateWeights.calculate(maps, AKTJAHR));

        verify(db, never()).insertForecastWeights(any(Hashtable.class));
    }

    @Test
    public void einNichtLeeresJahrGenuegt() {
        //  Nur das erste Jahr traegt Werte, die beiden folgenden sind leer.
        Map<Integer, YearTable> maps = new HashMap<Integer, YearTable>();
        maps.put(AKTJAHR, leeresJahr());
        maps.put(AKTJAHR - 1, leeresJahr());
        maps.put(AKTJAHR - 2, leeresJahr());
        maps.put(AKTJAHR - 3, jahrMitTag(2, 2.0));

        calculateWeights.calculate(maps, AKTJAHR);

        verify(db).insertForecastWeights(any(Hashtable.class));
    }

    @Test
    public void fehlendeJahreFuenhlenDenSuchraum() {
        //  Nur drei Jahre vorhanden. Die Suche darf keine NullPointerException
        //  werfen, weil sie auf Jahre zugreift, die es gar nicht gibt.
        Map<Integer, YearTable> maps = new HashMap<Integer, YearTable>();
        maps.put(AKTJAHR, leeresJahr());
        maps.put(AKTJAHR - 1, jahrMitTag(2, 2.0));
        maps.put(AKTJAHR - 2, jahrMitTag(2, 2.0));

        assertTrue(calculateWeights.calculate(maps, AKTJAHR));
    }

    @Test
    public void jedesJahrTraegtEigenenWert() {
        Map<Integer, YearTable> maps = new HashMap<Integer, YearTable>();
        maps.put(AKTJAHR, leeresJahr());
        maps.put(AKTJAHR - 1, jahrMitTag(2, 10.0));
        maps.put(AKTJAHR - 2, jahrMitTag(2, 20.0));
        maps.put(AKTJAHR - 3, jahrMitTag(2, 40.0));

        calculateWeights.calculate(maps, AKTJAHR);

        ArgumentCaptor<Hashtable> captor = ArgumentCaptor.forClass(Hashtable.class);
        verify(db).insertForecastWeights(captor.capture());
        //  20 liegt genau in der Mitte zwischen 10 und 40, das Optimum ist
        //  also eine exakte Ueberschreitung und keine Naeherung.
        assertEquals(0.0, (Double) captor.getValue().get("precision"), 1e-6);
    }

    // ------------------------------------------------------------------
    //  Testdaten
    // ------------------------------------------------------------------

    /** Sieben identische Jahre mit 2.0 am zweiten Tag. */
    private Map<Integer, YearTable> jahrestabelle() {
        Map<Integer, YearTable> maps = new HashMap<Integer, YearTable>();
        for (int i = 1; i < 8; i++) {
            maps.put(AKTJAHR - i, jahrMitTag(2, 2.0));
        }
        return maps;
    }

    private static double[][] tagesWerte() {
        double[][] tagesWerte = new double[8][];
        for (int offset = 1; offset <= 7; offset++) {
            double[] reihe = new double[367];
            reihe[2] = 2.0;
            tagesWerte[offset] = reihe;
        }
        return tagesWerte;
    }

    private static YearTable jahrMitTag(int tag, double wert) {
        YearTable yearTable = new YearTable();
        Map<Integer, Double> map = new HashMap<Integer, Double>();
        map.put(tag, wert);
        yearTable.setMapYear(map);
        yearTable.computeSum();
        return yearTable;
    }

    private static YearTable leeresJahr() {
        YearTable yearTable = new YearTable();
        Map<Integer, Double> map = new HashMap<Integer, Double>();
        for (int tag = 0; tag <= 366; tag++) {
            map.put(tag, 0.0);
        }
        yearTable.setMapYear(map);
        yearTable.computeSum();
        return yearTable;
    }
}