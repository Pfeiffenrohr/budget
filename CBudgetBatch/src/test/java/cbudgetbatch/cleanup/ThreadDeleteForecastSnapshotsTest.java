package cbudgetbatch.cleanup;

import cbudgetbatch.evaluation.ForecastEvaluationDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Aufraeumlogik der Snapshot-Historie.
 *
 * Die Reihenfolge ist bewusst geprueft: verwaiste Zeilen muessen vor der
 * Altersgrenze entfernt werden, sonst blockiert ein Verweis auf eine
 * geloeschte Kategorie das spaetere Loeschen gar nicht, sondern nur den
 * Rest muell.
 */
public class ThreadDeleteForecastSnapshotsTest {

    private static final LocalDate HEUTE = LocalDate.of(2024, 6, 15);

    private ForecastEvaluationDb db;
    private ThreadDeleteForecastSnapshots thread;

    @BeforeEach
    public void setUp() {
        db = mock(ForecastEvaluationDb.class);
        thread = new ThreadDeleteForecastSnapshots(db);
    }

    @Test
    public void loeschtNichtsOhneEinstellungUeberDefaultDreiJahre() {
        when(db.getIntSetting(ThreadDeleteForecastSnapshots.SETTING_RETENTION_JAHRE, 3)).thenReturn(3);
        when(db.deleteSnapshotsAelterAls(any(LocalDate.class))).thenReturn(0);

        assertEquals(0, thread.aufraeumen(HEUTE));

        verify(db).deleteSnapshotsAelterAls(LocalDate.of(2021, 6, 15));
    }

    @Test
    public void respektiertEingestellteAufbewahrung() {
        when(db.getIntSetting(ThreadDeleteForecastSnapshots.SETTING_RETENTION_JAHRE, 3)).thenReturn(1);
        when(db.deleteSnapshotsAelterAls(any(LocalDate.class))).thenReturn(0);

        thread.aufraeumen(HEUTE);

        verify(db).deleteSnapshotsAelterAls(LocalDate.of(2023, 6, 15));
    }

    @Test
    public void mindestensEinJahrAufbewahrung() {
        // 0 oder negativ wuerde bedeuten, den gesamten Bestand zu loeschen.
        when(db.getIntSetting(ThreadDeleteForecastSnapshots.SETTING_RETENTION_JAHRE, 3)).thenReturn(0);
        when(db.deleteSnapshotsAelterAls(any(LocalDate.class))).thenReturn(0);

        thread.aufraeumen(HEUTE);

        verify(db).deleteSnapshotsAelterAls(LocalDate.of(2023, 6, 15));
    }

    @Test
    public void loeschtVerwaisteZeilenVorDerAltersgrenze() {
        when(db.getIntSetting(ThreadDeleteForecastSnapshots.SETTING_RETENTION_JAHRE, 3)).thenReturn(3);
        when(db.deleteSnapshotsAelterAls(any(LocalDate.class))).thenReturn(42);

        assertEquals(42, thread.aufraeumen(HEUTE));

        InOrder reihenfolge = inOrder(db);
        reihenfolge.verify(db).deleteVerwaisteSnapshots();
        reihenfolge.verify(db).deleteSnapshotsAelterAls(any(LocalDate.class));
        reihenfolge.verify(db).deleteAuswertungOhneDaten(HEUTE);
    }

    @Test
    public void raeumtAuchAuswertungszeilenOhneIstDatenAuf() {
        when(db.getIntSetting(ThreadDeleteForecastSnapshots.SETTING_RETENTION_JAHRE, 3)).thenReturn(3);
        when(db.deleteSnapshotsAelterAls(any(LocalDate.class))).thenReturn(0);

        thread.aufraeumen(HEUTE);

        verify(db).deleteAuswertungOhneDaten(eq(HEUTE));
    }

    @Test
    public void haeufigkeitEntsprichtEinemTag() {
        assertEquals(1440L, ThreadDeleteForecastSnapshots.INTERVALL_MINUTEN);
    }

    @Test
    public void threadHatLesbarenNamen() {
        assertEquals("deleteForecastSnapshots", thread.getName());
    }

    @Test
    public void fehlerBeendenDenThreadNicht() {
        // Wenn die Datenbank einmal nicht erreichbar ist, darf der Thread
        // nicht sterben: sonst waechst die Tabelle unbegrenzt weiter.
        when(db.getIntSetting(ThreadDeleteForecastSnapshots.SETTING_RETENTION_JAHRE, 3))
                .thenThrow(new IllegalStateException("keine Verbindung"));

        //  Ein Durchlauf wirft; run() faengt das ab. Hier wird nur geprueft,
        //  dass der Fehler durchgereicht wird und nicht verschluckt.
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class,
                () -> thread.aufraeumen(HEUTE));
    }
}