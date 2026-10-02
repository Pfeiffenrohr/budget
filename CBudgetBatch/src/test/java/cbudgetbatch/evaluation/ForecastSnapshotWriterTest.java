package cbudgetbatch.evaluation;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Monatsaggregation im Snapshot-Writer und die Begrenzung des Horizonts.
 *
 * Das ist die Schnittstelle zwischen dem Forecast-Lauf und der Historie: was
 * hier falsch summiert wird, verfalscht jede spaetere Kennzahl.
 */
public class ForecastSnapshotWriterTest {

    private static final double DELTA = 1e-9;
    private static final LocalDate HEUTE = LocalDate.of(2024, 6, 15);

    @Test
    public void summiertTageZuMonaten() {
        ForecastSnapshotWriter writer = new ForecastSnapshotWriter(24);

        writer.addTag(LocalDate.of(2024, 6, 16), 1.0);
        writer.addTag(LocalDate.of(2024, 6, 17), 2.0);
        writer.addTag(LocalDate.of(2024, 7, 1), 4.0);

        Map<LocalDate, Double> monate = writer.getMonatsSummen();
        assertEquals(2, monate.size());
        assertEquals(3.0, monate.get(LocalDate.of(2024, 6, 1)), DELTA);
        assertEquals(4.0, monate.get(LocalDate.of(2024, 7, 1)), DELTA);
    }

    @Test
    public void summiertAuchKleinsteBetraegeUndNegative() {
        // Der Writer rundet nichts. Die Null-Grenze gehoert zur Auswertung
        // (ForecastMetricsCalculator), nicht zum Aufsummieren.
        ForecastSnapshotWriter writer = new ForecastSnapshotWriter(24);

        writer.addTag(LocalDate.of(2024, 6, 16), 1.0);
        writer.addTag(LocalDate.of(2024, 6, 17), 0.0005);
        writer.addTag(LocalDate.of(2024, 6, 18), -0.0005);

        assertEquals(1.0, writer.getMonatsSummen().get(LocalDate.of(2024, 6, 1)), DELTA);
    }

    @Test
    public void monatsSchluesselIstErsterTagDesMonats() {
        ForecastSnapshotWriter writer = new ForecastSnapshotWriter(24);

        writer.addTag(LocalDate.of(2024, 2, 29), 5.0);
        writer.addTag(LocalDate.of(2024, 3, 1), 7.0);

        assertEquals(5.0, writer.getMonatsSummen().get(LocalDate.of(2024, 2, 1)), DELTA);
        assertEquals(7.0, writer.getMonatsSummen().get(LocalDate.of(2024, 3, 1)), DELTA);
    }

    @Test
    public void schachteltDenSprungNachFebruar() {
        // Der Forecast rechnet 30 Jahre im Voraus. Der February muss in
        // allen Jahren auf 28 oder 29 Tage stimmen.
        ForecastSnapshotWriter writer = new ForecastSnapshotWriter(24);

        for (int jahr : new int[]{2024, 2025, 2028}) {
            writer.addTag(LocalDate.of(jahr, 2, 1), 1.0);
            writer.addTag(LocalDate.of(jahr, 2, 28), 1.0);
        }

        assertEquals(2.0, writer.getMonatsSummen().get(LocalDate.of(2024, 2, 1)), DELTA);
        assertEquals(2.0, writer.getMonatsSummen().get(LocalDate.of(2025, 2, 1)), DELTA);
        assertEquals(2.0, writer.getMonatsSummen().get(LocalDate.of(2028, 2, 1)), DELTA);
    }

    // ------------------------------------------------------------------
    //  Horizont
    // ------------------------------------------------------------------

    @Test
    public void horizontBeginntImLaufendenMonat() {
        ForecastSnapshotWriter writer = new ForecastSnapshotWriter(24);

        writer.addTag(LocalDate.of(2024, 5, 31), 1.0);
        writer.addTag(LocalDate.of(2024, 6, 1), 2.0);

        Map<LocalDate, Double> imHorizont = writer.getMonatsSummenImHorizont(HEUTE);

        assertEquals(1, imHorizont.size(),
                "Der Mai liegt vor dem Stichtag und gehoert nicht mehr zum Fenster");
        assertEquals(2.0, imHorizont.get(LocalDate.of(2024, 6, 1)), DELTA);
    }

    @Test
    public void horizontEndetNachEinerBestimmtenAnzahlMonaten() {
        ForecastSnapshotWriter writer = new ForecastSnapshotWriter(24);

        for (int monat = 0; monat <= 40; monat++) {
            writer.addTag(LocalDate.of(2024, 6, 1).plusMonths(monat), 1.0);
        }

        Map<LocalDate, Double> imHorizont = writer.getMonatsSummenImHorizont(HEUTE);

        assertEquals(24, imHorizont.size());
        assertTrue(imHorizont.containsKey(LocalDate.of(2024, 6, 1)));
        //  Juni 2024 plus 23 Monate
        assertTrue(imHorizont.containsKey(LocalDate.of(2026, 5, 1)));
        assertTrue(!imHorizont.containsKey(LocalDate.of(2026, 6, 1)),
                "Der 24. Monat nach dem laufenden liegt ausserhalb");
    }

    @Test
    public void begrenztDenSpeicherplatz() {
        // Ohne Horizont waeren es 30 Jahre Vorlauf: 360 Monate statt 24.
        ForecastSnapshotWriter writer = new ForecastSnapshotWriter(24);
        for (int monat = 0; monat < 360; monat++) {
            writer.addTag(LocalDate.of(2024, 6, 1).plusMonths(monat), 1.0);
        }

        assertEquals(360, writer.getMonatsSummen().size());
        assertEquals(24, writer.getMonatsSummenImHorizont(HEUTE).size());
    }

    @Test
    public void horizontWirdMindestensAufEinenMonatGesetzt() {
        assertEquals(1, new ForecastSnapshotWriter(0).getHorizontMonate());
        assertEquals(1, new ForecastSnapshotWriter(-5).getHorizontMonate());
    }

    // ------------------------------------------------------------------
    //  Zeilen fuer die Datenbank
    // ------------------------------------------------------------------

    @Test
    public void erzeugtJeMonatEineZeile() {
        ForecastSnapshotWriter writer = new ForecastSnapshotWriter(24);

        writer.addTag(LocalDate.of(2024, 6, 16), 1.0);
        writer.addTag(LocalDate.of(2024, 6, 17), 2.0);
        writer.addTag(LocalDate.of(2024, 7, 1), 4.0);

        List<ForecastEvaluationDb.SnapshotZeile> zeilen = writer.zeilenFuer(7, 9, HEUTE);

        assertEquals(2, zeilen.size());
    }

    @Test
    public void ohneDatenKeineZeilen() {
        ForecastSnapshotWriter writer = new ForecastSnapshotWriter(24);
        assertEquals(0, writer.zeilenFuer(7, 9, HEUTE).size());
    }

    @Test
    public void writerBleibtZwischenKategorienErhalten() {
        // Forecast legt fuer jedes Kategorie/Konto-Paar einen frischen Writer
        // an. Hier wird geprueft, dass addTag die Monatssummen nicht verliert,
        // wenn mehrmals gelesen wird.
        ForecastSnapshotWriter writer = new ForecastSnapshotWriter(24);
        writer.addTag(LocalDate.of(2024, 6, 16), 3.0);
        writer.addTag(LocalDate.of(2024, 6, 17), 4.0);

        writer.zeilenFuer(1, 1, HEUTE);
        writer.zeilenFuer(2, 2, HEUTE);

        assertEquals(7.0, writer.getMonatsSummen().get(LocalDate.of(2024, 6, 1)), DELTA);
    }

    @Test
    public void zaehltNegativeUndNullwerte() {
        ForecastSnapshotWriter writer = new ForecastSnapshotWriter(24);

        writer.addTag(LocalDate.of(2024, 6, 16), 10.0);
        writer.addTag(LocalDate.of(2024, 6, 17), -4.0);
        writer.addTag(LocalDate.of(2024, 6, 18), 0.0);

        assertEquals(6.0, writer.getMonatsSummen().get(LocalDate.of(2024, 6, 1)), DELTA);
    }

    @Test
    public void leererLaufLiefertKeineZeilen() {
        assertTrue(new ArrayList<ForecastEvaluationDb.SnapshotZeile>(
                new ForecastSnapshotWriter(24).zeilenFuer(1, 1, HEUTE)).isEmpty());
        assertNotNull(new ForecastSnapshotWriter(24).getMonatsSummen());
        assertNull(new ForecastSnapshotWriter(24).getMonatsSummen().get(LocalDate.of(2024, 6, 1)));
    }

    @Test
    public void horizontEntsprichtDerVorgabeVon24Monaten() {
        assertEquals(24, ForecastSnapshotWriter.STANDARD_HORIZONT_MONATE);
        assertEquals(24, new ForecastSnapshotWriter(ForecastSnapshotWriter.STANDARD_HORIZONT_MONATE)
                .getHorizontMonate());
        assertEquals("forecast_eval_horizont", ForecastSnapshotWriter.SETTING_HORIZONT);
    }
}