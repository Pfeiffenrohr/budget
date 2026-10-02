package cbudgetbatch.evaluation;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Die Auswahlregel ist die fachlich heikelste Stelle der Auswertung:
 * verglichen wird nur gegen den Stand, der vor Beginn des prognostizierten
 * Zeitraums erstellt wurde.
 */
public class SnapshotSelectorTest {

    private static final LocalDate MONAT = LocalDate.of(2024, 3, 1);

    @Test
    public void nimmtNeuestenStandVorMonatsbeginn() {
        List<Snapshot> kandidaten = Arrays.asList(
                new Snapshot(MONAT, LocalDate.of(2024, 2, 1), 100.0),
                new Snapshot(MONAT, LocalDate.of(2024, 2, 20), 200.0),
                new Snapshot(MONAT, LocalDate.of(2024, 2, 28), 300.0));

        Snapshot gewaehlt = SnapshotSelector.select(kandidaten, MONAT);

        assertNotNull(gewaehlt);
        assertEquals(300.0, gewaehlt.getWert(), 1e-9);
        assertEquals(LocalDate.of(2024, 2, 28), gewaehlt.getErstelltTag());
    }

    @Test
    public void standExaktAmMonatsbeginnZaehltNicht() {
        // Ein Forecast vom 1.3. kann die Daten aus 3.3 kennen. Er waere kein
        // gueltiger Vergleich fuer den Maerz.
        List<Snapshot> kandidaten = Arrays.asList(
                new Snapshot(MONAT, LocalDate.of(2024, 2, 28), 100.0),
                new Snapshot(MONAT, MONAT, 999.0));

        Snapshot gewaehlt = SnapshotSelector.select(kandidaten, MONAT);

        assertNotNull(gewaehlt);
        assertEquals(100.0, gewaehlt.getWert(), 1e-9,
                "Stand vom Monatsbeginn darf nicht gewaehlt werden");
    }

    @Test
    public void standNachMonatsbeginnZaehltNicht() {
        List<Snapshot> kandidaten = Arrays.asList(
                new Snapshot(MONAT, LocalDate.of(2024, 2, 28), 100.0),
                new Snapshot(MONAT, LocalDate.of(2024, 3, 5), 500.0));

        Snapshot gewaehlt = SnapshotSelector.select(kandidaten, MONAT);

        assertNotNull(gewaehlt);
        assertEquals(100.0, gewaehlt.getWert(), 1e-9);
    }

    @Test
    public void ohnePassendenStandIstErgebnisNull() {
        List<Snapshot> nurNachher = Arrays.asList(
                new Snapshot(MONAT, LocalDate.of(2024, 3, 1), 100.0),
                new Snapshot(MONAT, LocalDate.of(2024, 3, 2), 100.0));

        assertNull(SnapshotSelector.select(nurNachher, MONAT));
        assertNull(SnapshotSelector.select(new ArrayList<Snapshot>(), MONAT));
        assertNull(SnapshotSelector.select(null, MONAT));
    }

    @Test
    public void spaetereBerechnungKorrigiertFrueheresErgebnis() {
        // Nachbuchungen im Februar: der Stand vom 10.2. weiss davon noch nichts,
        // der vom 25.2. schon. Genau deshalb gewinnt der spaetere Stand.
        List<Snapshot> kandidaten = Arrays.asList(
                new Snapshot(MONAT, LocalDate.of(2024, 2, 10), 100.0),
                new Snapshot(MONAT, LocalDate.of(2024, 2, 25), 175.0));

        assertEquals(175.0, SnapshotSelector.select(kandidaten, MONAT).getWert(), 1e-9);
    }

    @Test
    public void reihenfolgeDerEingabeIstOhneBedeutung() {
        Snapshot a = new Snapshot(MONAT, LocalDate.of(2024, 1, 10), 100.0);
        Snapshot b = new Snapshot(MONAT, LocalDate.of(2024, 2, 10), 200.0);
        Snapshot c = new Snapshot(MONAT, LocalDate.of(2024, 2, 25), 300.0);

        Snapshot vorwaerts = SnapshotSelector.select(Arrays.asList(a, b, c), MONAT);
        Snapshot rueckwaerts = SnapshotSelector.select(Arrays.asList(c, b, a), MONAT);

        assertEquals(300.0, vorwaerts.getWert(), 1e-9);
        assertEquals(300.0, rueckwaerts.getWert(), 1e-9,
                "Die Datenbank liefert die Staende in beliebiger Reihenfolge");
    }

    @Test
    public void waehltJeZeitraumGetrennt() {
        LocalDate maerz2023 = LocalDate.of(2023, 3, 1);
        LocalDate mai2023 = LocalDate.of(2023, 5, 1);

        List<Snapshot> kandidaten = Arrays.asList(
                new Snapshot(maerz2023, LocalDate.of(2023, 2, 20), 100.0),
                new Snapshot(maerz2023, LocalDate.of(2023, 3, 2), 999.0),
                new Snapshot(mai2023, LocalDate.of(2023, 4, 15), 200.0));

        List<Snapshot> ergebnis = SnapshotSelector.selectJeZeitraum(kandidaten);

        assertEquals(2, ergebnis.size());
        for (Snapshot snapshot : ergebnis) {
            if (snapshot.getMonatsBeginn().equals(maerz2023)) {
                assertEquals(100.0, snapshot.getWert(), 1e-9,
                        "Stand nach Monatsbeginn darf den gueltigen nicht verdraengen");
            } else {
                assertEquals(mai2023, snapshot.getMonatsBeginn());
                assertEquals(200.0, snapshot.getWert(), 1e-9);
            }
        }
    }

    @Test
    public void auswahlIstBeiWiederholungStabil() {
        // Zweimal dieselbe Eingabe muss dasselbe Ergebnis liefern, sonst
        // waere die Auswertung nicht wiederholbar.
        List<Snapshot> kandidaten = Arrays.asList(
                new Snapshot(MONAT, LocalDate.of(2024, 1, 5), 10.0),
                new Snapshot(MONAT, LocalDate.of(2024, 2, 5), 20.0),
                new Snapshot(MONAT, LocalDate.of(2024, 3, 5), 30.0));

        for (int i = 0; i < 100; i++) {
            assertEquals(20.0, SnapshotSelector.select(kandidaten, MONAT).getWert(), 1e-9);
        }
    }
}