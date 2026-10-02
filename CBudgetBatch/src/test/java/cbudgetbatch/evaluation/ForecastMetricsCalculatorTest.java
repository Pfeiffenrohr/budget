package cbudgetbatch.evaluation;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Kennzahlenformeln und die Sonderfaelle, die die fachliche Anforderung
 * ausdruecklich verlangt.
 */
public class ForecastMetricsCalculatorTest {

    private static final double DELTA = 1e-9;

    // ------------------------------------------------------------------
    //  Abweichung
    // ------------------------------------------------------------------

    @Test
    public void abweichungIstIstMinusForecast() {
        assertEquals(20.0, ForecastMetricsCalculator.abweichung(100.0, 120.0), DELTA);
        assertEquals(-20.0, ForecastMetricsCalculator.abweichung(100.0, 80.0), DELTA);
        assertEquals(0.0, ForecastMetricsCalculator.abweichung(100.0, 100.0), DELTA);
    }

    @Test
    public void prozentAbweichungIstRelativZumForecast() {
        assertEquals(20.0, ForecastMetricsCalculator.abweichungProzent(100.0, 120.0), DELTA);
        assertEquals(-20.0, ForecastMetricsCalculator.abweichungProzent(100.0, 80.0), DELTA);
        // Bezugsmenge ist der Forecast, nicht der Ist-Wert.
        assertEquals(50.0, ForecastMetricsCalculator.abweichungProzent(200.0, 300.0), DELTA);
    }

    @Test
    public void vorzeichenKehrtSichBeiNegativemForecast() {
        //  -100 prognostiziert, -80 gebucht: es wurden 20 Einheiten weniger
        //  vorhergesagt. Der Quotient liefert trotzdem -20 Prozent, weil sich
        //  das Vorzeichen des Forecasts mitdreht. Der Betrag stimmt.
        assertEquals(-20.0, ForecastMetricsCalculator.abweichungProzent(-100.0, -80.0), DELTA);
        assertEquals(20.0, ForecastMetricsCalculator.abweichung( -100.0, -80.0), DELTA);
    }

    // ------------------------------------------------------------------
    //  Sonderfall: nahezu null Forecast
    // ------------------------------------------------------------------

    @Test
    public void prozentAbweichungIstBeiNahezuNullForecastNull() {
        assertNull(ForecastMetricsCalculator.abweichungProzent(0.0, 120.0));
        assertNull(ForecastMetricsCalculator.abweichungProzent(0.005, 120.0));
        assertNull(ForecastMetricsCalculator.abweichungProzent(-0.005, 120.0));
    }

    @Test
    public void grenzeWirdNichtMehrAlsNahezuNullBehandelt() {
        assertTrue(ForecastMetricsCalculator.istNahezuNull(0.0099));
        assertEquals(0.01, ForecastMetricsCalculator.NULL_GRENZE, DELTA);
        assertTrue(ForecastMetricsCalculator.abweichungProzent(0.01, 1.0) != null,
                "Genau an der Grenze ist der Quotient noch definiert");
    }

    @Test
    public void nahezuNullForecastIstKeinTreffer() {
        assertTrue(!ForecastMetricsCalculator.istTreffer(0.0, 0.0, 10.0),
                "0 gegen 0 ist keine Prognosequalitaet, sondern fehlende Daten");
    }

    // ------------------------------------------------------------------
    //  Status
    // ------------------------------------------------------------------

    @Test
    public void statusIstOkBeiWertenUndSinnvollemForecast() {
        assertEquals(ForecastMetricsCalculator.STATUS_OK,
                ForecastMetricsCalculator.status(100.0, 120.0));
    }

    @Test
    public void statusIstKeinIstOhneIstDaten() {
        assertEquals(ForecastMetricsCalculator.STATUS_KEIN_IST,
                ForecastMetricsCalculator.status(100.0, null));
    }

    @Test
    public void statusIstKeinForecastBeiNahezuNull() {
        assertEquals(ForecastMetricsCalculator.STATUS_KEIN_FORECAST,
                ForecastMetricsCalculator.status(0.0, 120.0));
    }

    @Test
    public void fehlendeIstDatenSchlagenNahezuNullForecast() {
        // Ist fehlt und der Forecast ist null: beides trifft zu. Ist-Daten
        // sind die traeftigere Information und werden zuerst genannt.
        assertEquals(ForecastMetricsCalculator.STATUS_KEIN_IST,
                ForecastMetricsCalculator.status(0.0, null));
    }

    // ------------------------------------------------------------------
    //  Trefferquote
    // ------------------------------------------------------------------

    @Test
    public void trefferWirdUeberProzentabweichungBestimmt() {
        assertTrue(ForecastMetricsCalculator.istTreffer(100.0, 105.0, 10.0));
        assertTrue(ForecastMetricsCalculator.istTreffer(100.0, 95.0, 10.0),
                "Unterschaetzung innerhalb der Toleranz ist auch ein Treffer");
        assertTrue(!ForecastMetricsCalculator.istTreffer(100.0, 111.0, 10.0));
        assertTrue(!ForecastMetricsCalculator.istTreffer(100.0, 89.0, 10.0));
    }

    @Test
    public void trefferAnDerToleranzgrenzeZaehltAlsTreffer() {
        //  Genau 10 Prozent gehoeren noch zum Treffer, sonst waere die
        //  Trefferquote von der Rundung des Betrags abhaengig: 100 gegen 110
        //  waere ein Treffer, 1 gegen 1.1 nicht.
        assertTrue(ForecastMetricsCalculator.istTreffer(100.0, 110.0, 10.0));
        assertTrue(ForecastMetricsCalculator.istTreffer(1.0, 1.1, 10.0));
        assertTrue(ForecastMetricsCalculator.istTreffer(1.0, 0.9, 10.0));
        assertTrue(!ForecastMetricsCalculator.istTreffer(100.0, 110.001, 10.0));
    }

    @Test
    public void trefferquoteIstUnabhaengigVonDerBetragshoehe() {
        //  1000 mit 10 Prozent Abweichung trifft genauso wie 1 mit 10 Prozent.
        assertTrue(ForecastMetricsCalculator.istTreffer(1000.0, 1100.0, 10.0));
        assertTrue(ForecastMetricsCalculator.istTreffer(1.0, 1.1, 10.0));
        assertTrue(ForecastMetricsCalculator.istTreffer(0.01, 0.011, 10.0));
    }

    // ------------------------------------------------------------------
    //  Aggregation
    // ------------------------------------------------------------------

    @Test
    public void aggregatZaehltNurAuswertbareZeilen() {
        List<Auswertungszeile> zeilen = new ArrayList<Auswertungszeile>(Arrays.asList(
                zeile("2024-01", 100.0, 120.0),
                zeile("2024-02", 100.0, 80.0),
                ohneIst("2024-03", 100.0),
                ohneForecastWert("2024-04", 0.0, 120.0)));

        Aggregat aggregat = ForecastMetricsCalculator.aggregiere(zeilen, 10.0);

        assertEquals(4, aggregat.getAnzahlGesamt());
        assertEquals(2, aggregat.getAnzahlAusgewertet());
        assertEquals(1, aggregat.getAnzahlOhneIst());
        assertEquals(1, aggregat.getAnzahlOhneForecastwert());
    }

    @Test
    public void aggregatBildetBiasUndStaerksteAbweichungen() {
        List<Auswertungszeile> zeilen = Arrays.asList(
                zeile("2024-01", 100.0, 120.0),
                zeile("2024-02", 100.0, 80.0));

        Aggregat aggregat = ForecastMetricsCalculator.aggregiere(zeilen, 10.0);

        assertEquals(0.0, aggregat.getMittelAbweichung(), DELTA, "20 und -20 heben sich auf");
        assertEquals(20.0, aggregat.getMittelAbsAbweichung(), DELTA);
        assertEquals(0.0, aggregat.getBiasProzent(), DELTA);
        assertEquals(20.0, aggregat.getMittelAbsProzent(), DELTA);
        assertEquals(20.0, aggregat.getStaerksteUeberschatzung(), DELTA);
        assertEquals(-20.0, aggregat.getStaerksteUnterschaetzung(), DELTA);
    }

    @Test
    public void aggregatZeigtSystematischeUeberschaetzung() {
        // Jeder Monat 20 Prozent zu niedrig: Der Forecast ist zu hoch.
        List<Auswertungszeile> zeilen = Arrays.asList(
                zeile("2024-01", 100.0, 80.0),
                zeile("2024-02", 100.0, 80.0),
                zeile("2024-03", 100.0, 80.0));

        Aggregat aggregat = ForecastMetricsCalculator.aggregiere(zeilen, 10.0);

        assertEquals(-20.0, aggregat.getBiasProzent(), DELTA,
                "Negatives Vorzeichen bedeutet: Forecast lag zu hoch");
        assertEquals(0.0, aggregat.getTrefferquoteProzent(), DELTA);
    }

    @Test
    public void trefferquoteZaehtNurToleranzTreffer() {
        List<Auswertungszeile> zeilen = Arrays.asList(
                zeile("2024-01", 100.0, 105.0),
                zeile("2024-02", 100.0, 130.0),
                zeile("2024-03", 100.0, 95.0),
                zeile("2024-04", 100.0, 89.0));

        Aggregat aggregat = ForecastMetricsCalculator.aggregiere(zeilen, 10.0);

        assertEquals(50.0, aggregat.getTrefferquoteProzent(), DELTA,
                "Plus 5 und minus 5 liegen innerhalb, plus 30 und minus 11 nicht");
    }

    @Test
    public void leeresAggregatHatNullKennzahlen() {
        Aggregat aggregat = ForecastMetricsCalculator.aggregiere(new ArrayList<Auswertungszeile>(), 10.0);

        assertNull(aggregat.getMittelAbweichung(),
                "Keine Daten ist nicht dasselbe wie 0 Prozent Abweichung");
        assertNull(aggregat.getTrefferquoteProzent());
        assertEquals(0, aggregat.getAnzahlAusgewertet());
    }

    @Test
    public void aggregatOhneAuswertbareZeilenHatNullKennzahlen() {
        List<Auswertungszeile> zeilen = Arrays.asList(ohneIst("2024-01", 100.0));

        Aggregat aggregat = ForecastMetricsCalculator.aggregiere(zeilen, 10.0);

        assertEquals(0, aggregat.getAnzahlAusgewertet());
        assertEquals(1, aggregat.getAnzahlOhneIst());
        assertNull(aggregat.getBiasProzent(),
                "Fehlende Ist-Werte duerfen die Kennzahlen nicht auf 0 ziehen");
    }

    // ------------------------------------------------------------------
    //  Auswertungszeile
    // ------------------------------------------------------------------

    @Test
    public void zeileBerechnetAbweichungNurMitIstDaten() {
        assertEquals(20.0, zeile("2024-01", 100.0, 120.0).getAbweichung(), DELTA);
        assertEquals(20.0, zeile("2024-01", 100.0, 120.0).getAbweichungProzent(), DELTA);
        assertNull(ohneIst("2024-01", 100.0).getAbweichung());
        assertNull(ohneIst("2024-01", 100.0).getAbweichungProzent());
        assertNull(ohneForecastWert("2024-01", 0.0, 120.0).getAbweichungProzent());
    }

    // ------------------------------------------------------------------
    //  Hilfen
    // ------------------------------------------------------------------

    private static Auswertungszeile zeile(String monatKey, double forecast, double ist) {
        Monat monat = Monat.fromKey(monatKey);
        return new Auswertungszeile(monat.getJahr(), monat.getMonat(), monatKey, 1, 1,
                LocalDate.of(monat.getJahr(), monat.getMonat(), 1).minusDays(10),
                forecast, ist, ForecastMetricsCalculator.STATUS_OK);
    }

    private static Auswertungszeile ohneIst(String monatKey, double forecast) {
        Monat monat = Monat.fromKey(monatKey);
        return new Auswertungszeile(monat.getJahr(), monat.getMonat(), monatKey, 1, 1,
                LocalDate.of(monat.getJahr(), monat.getMonat(), 1).minusDays(10),
                forecast, null, ForecastMetricsCalculator.STATUS_KEIN_IST);
    }

    private static Auswertungszeile ohneForecastWert(String monatKey, double forecast, double ist) {
        Monat monat = Monat.fromKey(monatKey);
        return new Auswertungszeile(monat.getJahr(), monat.getMonat(), monatKey, 1, 1,
                LocalDate.of(monat.getJahr(), monat.getMonat(), 1).minusDays(10),
                forecast, ist, ForecastMetricsCalculator.STATUS_KEIN_FORECAST);
    }
}