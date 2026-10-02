package cbudgetbatch.evaluation;

import cbudgetbatch.DBBatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Ablauf des Auswertungslaufs mit gemockter Datenbank.
 *
 * Geprueft wird vor allem, welcher Stand gegen welche Ist-Daten gerechnet wird
 * und dass Zeilen mit fehlenden Ist-Werten als solche markiert und nicht
 * verworfen werden.
 */
public class ForecastEvaluationJobTest {

    private static final double DELTA = 1e-9;
    private static final LocalDate HEUTE = LocalDate.of(2024, 4, 15);
    private static final LocalDate MAERZ = LocalDate.of(2024, 3, 1);
    private static final LocalDate FEBRUAR = LocalDate.of(2024, 2, 1);

    private ForecastEvaluationDb db;

    @BeforeEach
    public void setUp() {
        db = mock(ForecastEvaluationDb.class);
        when(db.getDoubleSetting(ForecastEvaluation.SETTING_TOLERANZ, 10.0)).thenReturn(10.0);
        when(db.getIntSetting(ForecastEvaluation.SETTING_MONATE, 6)).thenReturn(6);
        when(db.getAuswertbareZeitraeume(any(LocalDate.class), anyInt()))
                .thenReturn(Arrays.asList(MAERZ, FEBRUAR));
        when(db.getSnapshots(FEBRUAR)).thenReturn(Arrays.asList());
        when(db.getSnapshots(MAERZ)).thenReturn(Arrays.<Snapshot>asList());
    }

    // ------------------------------------------------------------------
    //  Zeitpunktlogik
    // ------------------------------------------------------------------

    @Test
    public void laufendenMonatNichtAuswerten() {
        ArgumentCaptor<LocalDate> grenze = ArgumentCaptor.forClass(LocalDate.class);
        when(db.getAuswertbareZeitraeume(any(LocalDate.class), anyInt())).thenReturn(new java.util.ArrayList<LocalDate>());
        new ForecastEvaluation(db, HEUTE).auswerten();

        verify(db).getAuswertbareZeitraeume(grenze.capture(), anyInt());
        assertEquals(LocalDate.of(2024, 4, 1), grenze.getValue(),
                "Der April ist noch nicht beendet und gehoert nicht ausgewertet");
    }

    @Test
    public void wertetNurVomBestandGelieferteZeitraeumeAus() {
        when(db.getSnapshots(MAERZ)).thenReturn(Arrays.asList(
                new Snapshot(MAERZ, LocalDate.of(2024, 2, 20), 1, 1, 100.0)));
        when(db.getIstWerte(MAERZ)).thenReturn(new HashMap<ForecastEvaluationDb.KategorieKonto, Double>());

        ForecastEvaluation.Ergebnis ergebnis = new ForecastEvaluation(db, HEUTE).auswerten();

        assertEquals(Arrays.asList(MAERZ, FEBRUAR), ergebnis.getBearbeiteteZeitraeume());
        assertEquals(1, ergebnis.getAnzahlZeilen());
        verify(db, times(1)).speichereAuswertung(any(Auswertungszeile.class), eq(HEUTE));
    }

    @Test
    public void waehltStandVonVorMonatsbeginn() {
        when(db.getSnapshots(MAERZ)).thenReturn(Arrays.asList(
                new Snapshot(MAERZ, LocalDate.of(2024, 2, 20), 1, 1, 100.0),
                new Snapshot(MAERZ, LocalDate.of(2024, 3, 5), 1, 1, 900.0),
                new Snapshot(MAERZ, LocalDate.of(2024, 1, 15), 1, 1, 50.0)));
        Map<ForecastEvaluationDb.KategorieKonto, Double> ist = new HashMap<ForecastEvaluationDb.KategorieKonto, Double>();
        ist.put(new ForecastEvaluationDb.KategorieKonto(1, 1), 120.0);
        when(db.getIstWerte(MAERZ)).thenReturn(ist);

        new ForecastEvaluation(db, HEUTE).auswerten();

        Auswertungszeile gespeichert = gefangeneZeile();
        assertEquals(100.0, gespeichert.getForecastWert(), DELTA,
                "Der Stand vom 5.3. kennt die Maerz-Daten und darf nicht gewaehlt werden");
        assertEquals(LocalDate.of(2024, 2, 20), gespeichert.getSnapshotTag());
        assertEquals(120.0, gespeichert.getIstWert(), DELTA);
        assertEquals(20.0, gespeichert.getAbweichung(), DELTA);
        assertEquals(20.0, gespeichert.getAbweichungProzent(), DELTA);
        assertEquals(ForecastMetricsCalculator.STATUS_OK, gespeichert.getStatus());
        assertEquals("2024-03", gespeichert.getMonatKey());
        assertEquals(2024, gespeichert.getJahr());
        assertEquals(3, gespeichert.getMonat());
    }

    @Test
    public void ohneStandVorMonatsbeginnKeineZeile() {
        when(db.getSnapshots(MAERZ)).thenReturn(Arrays.asList(
                new Snapshot(MAERZ, LocalDate.of(2024, 3, 5), 1, 1, 900.0)));
        when(db.getIstWerte(MAERZ)).thenReturn(new HashMap<ForecastEvaluationDb.KategorieKonto, Double>());

        ForecastEvaluation.Ergebnis ergebnis = new ForecastEvaluation(db, HEUTE).auswerten();

        assertEquals(1, ergebnis.getAnzahlOhneSnapshot());
        verify(db, times(0)).speichereAuswertung(any(Auswertungszeile.class), any(LocalDate.class));
    }

    // ------------------------------------------------------------------
    //  Sonderfaelle
    // ------------------------------------------------------------------

    @Test
    public void fehlendeIstDatenWerdenMarkiertUndGespeichert() {
        // Die Zeile geht nicht verloren, wird aber aus den Kennzahlen gehalten.
        when(db.getSnapshots(MAERZ)).thenReturn(Arrays.asList(
                new Snapshot(MAERZ, LocalDate.of(2024, 2, 20), 3, 4, 100.0)));
        when(db.getIstWerte(MAERZ)).thenReturn(new HashMap<ForecastEvaluationDb.KategorieKonto, Double>());

        ForecastEvaluation.Ergebnis ergebnis = new ForecastEvaluation(db, HEUTE).auswerten();

        assertEquals(1, ergebnis.getAnzahlZeilen());
        assertEquals(Integer.valueOf(1), ergebnis.getStatusZaehler().get(ForecastMetricsCalculator.STATUS_KEIN_IST));
        Auswertungszeile gespeichert = gefangeneZeile();
        assertEquals(ForecastMetricsCalculator.STATUS_KEIN_IST, gespeichert.getStatus());
        assertEquals(null, gespeichert.getIstWert());
        assertEquals(null, gespeichert.getAbweichung());
        assertEquals(3, gespeichert.getKategorie());
        assertEquals(4, gespeichert.getKonto());
    }

    @Test
    public void nahezuNullForecastWirdMarkiert() {
        when(db.getSnapshots(MAERZ)).thenReturn(Arrays.asList(
                new Snapshot(MAERZ, LocalDate.of(2024, 2, 20), 1, 1, 0.0)));
        Map<ForecastEvaluationDb.KategorieKonto, Double> ist = new HashMap<ForecastEvaluationDb.KategorieKonto, Double>();
        ist.put(new ForecastEvaluationDb.KategorieKonto(1, 1), 120.0);
        when(db.getIstWerte(MAERZ)).thenReturn(ist);

        new ForecastEvaluation(db, HEUTE).auswerten();

        Auswertungszeile gespeichert = gefangeneZeile();
        assertEquals(ForecastMetricsCalculator.STATUS_KEIN_FORECAST, gespeichert.getStatus());
        assertEquals(120.0, gespeichert.getIstWert(), DELTA, "Ist-Wert bleibt trotzdem erhalten");
        assertEquals(null, gespeichert.getAbweichungProzent(),
                "Ein Prozentwert gegen 0 waere undefiniert");
        assertEquals(120.0, gespeichert.getAbweichung(), DELTA,
                "Die absolute Abweichung ist auch bei Forecast 0 sinnvoll");
    }

    @Test
    public void mehrfachPaareJeZeitraum() {
        Map<ForecastEvaluationDb.KategorieKonto, Double> ist =
                new LinkedHashMap<ForecastEvaluationDb.KategorieKonto, Double>();
        ist.put(new ForecastEvaluationDb.KategorieKonto(1, 1), 110.0);
        ist.put(new ForecastEvaluationDb.KategorieKonto(2, 2), 250.0);
        when(db.getSnapshots(MAERZ)).thenReturn(Arrays.asList(
                new Snapshot(MAERZ, LocalDate.of(2024, 2, 20), 1, 1, 100.0),
                new Snapshot(MAERZ, LocalDate.of(2024, 2, 21), 2, 2, 200.0),
                new Snapshot(MAERZ, LocalDate.of(2024, 1, 5), 1, 2, 999.0)));
        when(db.getIstWerte(MAERZ)).thenReturn(ist);

        ForecastEvaluation.Ergebnis ergebnis = new ForecastEvaluation(db, HEUTE).auswerten();

        //  (1,1) und (2,2) haben Ist-Daten, (1,2) nicht. Alle drei bekommen
        //  eine Zeile, aber nur zwei zaehlen als ausgewertet.
        assertEquals(3, ergebnis.getAnzahlZeilen());
        assertEquals(Integer.valueOf(2), ergebnis.getStatusZaehler().get(ForecastMetricsCalculator.STATUS_OK));
        assertEquals(Integer.valueOf(1),
                ergebnis.getStatusZaehler().get(ForecastMetricsCalculator.STATUS_KEIN_IST));
        verify(db, times(3)).speichereAuswertung(any(Auswertungszeile.class), eq(HEUTE));
    }

    @Test
    public void ohneSnapshotsWirdNichtsGeschrieben() {
        ForecastEvaluation.Ergebnis ergebnis = new ForecastEvaluation(db, HEUTE).auswerten();

        assertEquals(0, ergebnis.getAnzahlZeilen());
        verify(db, times(0)).speichereAuswertung(any(Auswertungszeile.class), any(LocalDate.class));
    }

    @Test
    public void ergebnisLiefertKennzahlenFuerDieAusgabe() {
        when(db.getSnapshots(MAERZ)).thenReturn(Arrays.asList(
                new Snapshot(MAERZ, LocalDate.of(2024, 2, 20), 1, 1, 100.0)));
        when(db.getSnapshots(FEBRUAR)).thenReturn(Arrays.asList(
                new Snapshot(FEBRUAR, LocalDate.of(2024, 1, 20), 1, 1, 100.0)));
        Map<ForecastEvaluationDb.KategorieKonto, Double> istMaerz =
                new HashMap<ForecastEvaluationDb.KategorieKonto, Double>();
        istMaerz.put(new ForecastEvaluationDb.KategorieKonto(1, 1), 120.0);
        Map<ForecastEvaluationDb.KategorieKonto, Double> istFebruar =
                new HashMap<ForecastEvaluationDb.KategorieKonto, Double>();
        istFebruar.put(new ForecastEvaluationDb.KategorieKonto(1, 1), 80.0);
        when(db.getIstWerte(MAERZ)).thenReturn(istMaerz);
        when(db.getIstWerte(FEBRUAR)).thenReturn(istFebruar);

        ForecastEvaluation.Ergebnis ergebnis = new ForecastEvaluation(db, HEUTE).auswerten();
        Aggregat aggregat = ForecastMetricsCalculator.aggregiere(ergebnis.getZeilen(), ergebnis.getToleranz());

        assertEquals(2, aggregat.getAnzahlAusgewertet());
        assertEquals(10.0, aggregat.getToleranz(), DELTA);
        assertEquals(0.0, aggregat.getBiasProzent(), DELTA, "Plus 20 und minus 20 heben sich auf");
    }

    @Test
    public void wiederholterLaufLiefertGleichesErgebnis() {
        when(db.getSnapshots(MAERZ)).thenReturn(Arrays.asList(
                new Snapshot(MAERZ, LocalDate.of(2024, 2, 20), 1, 1, 100.0),
                new Snapshot(MAERZ, LocalDate.of(2024, 2, 25), 1, 1, 175.0)));
        Map<ForecastEvaluationDb.KategorieKonto, Double> ist = new HashMap<ForecastEvaluationDb.KategorieKonto, Double>();
        ist.put(new ForecastEvaluationDb.KategorieKonto(1, 1), 150.0);
        when(db.getIstWerte(MAERZ)).thenReturn(ist);

        List<Auswertungszeile> ersterLauf = new ForecastEvaluation(db, HEUTE).auswerten().getZeilen();
        List<Auswertungszeile> zweiterLauf = new ForecastEvaluation(db, HEUTE).auswerten().getZeilen();

        assertEquals(ersterLauf.toString(), zweiterLauf.toString());
        assertTrue(ersterLauf.get(0).getSnapshotTag().equals(LocalDate.of(2024, 2, 25)),
                "Nachbuchungen muessen in bereits ausgewertete Zeitraeume nachwachsen");
    }

    // ------------------------------------------------------------------
    //  Hilfen
    // ------------------------------------------------------------------

    private Auswertungszeile gefangeneZeile() {
        ArgumentCaptor<Auswertungszeile> captor = ArgumentCaptor.forClass(Auswertungszeile.class);
        verify(db, org.mockito.Mockito.atLeastOnce())
                .speichereAuswertung(captor.capture(), any(LocalDate.class));
        return captor.getValue();
    }
}