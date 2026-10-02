package cbudgetbatch.evaluation;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MonatTest {

    @Test
    public void schluesselWirdAusDatumGebildet() {
        assertEquals("2024-03", Monat.from(LocalDate.of(2024, 3, 17)).key());
        assertEquals(2024, Monat.from(LocalDate.of(2024, 3, 17)).getJahr());
        assertEquals(3, Monat.from(LocalDate.of(2024, 3, 17)).getMonat());
    }

    @Test
    public void schluesselWirdZurueckGelesen() {
        Monat monat = Monat.fromKey("2024-03");
        assertEquals(2024, monat.getJahr());
        assertEquals(3, monat.getMonat());
        assertEquals("2024-03", monat.key());
        assertEquals(monat, Monat.from(LocalDate.of(2024, 3, 31)));
    }

    @Test
    public void monatsUndJahresAnzahlIstVierStellig() {
        assertEquals("2024-01", Monat.from(LocalDate.of(2024, 1, 5)).key());
        assertEquals("2024-12", Monat.from(LocalDate.of(2024, 12, 5)).key());
        assertEquals("0999-01", Monat.fromKey("0999-01").key());
    }

    @Test
    public void startIstErsterTagDesMonats() {
        assertEquals(LocalDate.of(2024, 2, 1), Monat.fromKey("2024-02").start());
    }

    @Test
    public void endeExklusivIstErsterTagDesFolgeMonats() {
        assertEquals(LocalDate.of(2024, 3, 1), Monat.fromKey("2024-02").endeExklusiv());
        assertEquals(LocalDate.of(2024, 1, 1), Monat.fromKey("2023-12").endeExklusiv());
        assertEquals(LocalDate.of(2025, 1, 1), Monat.fromKey("2024-12").endeExklusiv());
    }

    @Test
    public void sprungUeberDenFebruar() {
        assertEquals("2024-03", Monat.fromKey("2024-02").plus(1).key());
        assertEquals("2025-02", Monat.fromKey("2024-03").plus(11).key());
        assertEquals("2023-12", Monat.fromKey("2024-01").plus(-1).key());
    }

    @Test
    public void monatsAnfangLiegtNieNachDemStichtag() {
        assertTrue(Monat.fromKey("2024-02").istVor(LocalDate.of(2024, 3, 1)));
        assertTrue(Monat.fromKey("2024-02").istVor(LocalDate.of(2024, 2, 15)));
        assertFalse(Monat.fromKey("2024-03").istVor(LocalDate.of(2024, 3, 1)),
                "Der laufende Monat ist nicht vor seinem eigenen Anfang");
    }

    @Test
    public void ungleicheMonateSindUngleich() {
        assertNotEquals(Monat.fromKey("2024-03"), Monat.fromKey("2023-03"));
        assertNotEquals(Monat.fromKey("2024-03"), Monat.fromKey("2024-04"));
        assertNotEquals(Monat.fromKey("2024-03"), "2024-03");
    }

    @Test
    public void ungueltigerSchluesselWirdAbgewiesen() {
        assertThrows(IllegalArgumentException.class, () -> Monat.fromKey(null));
        assertThrows(IllegalArgumentException.class, () -> Monat.fromKey(""));
        assertThrows(IllegalArgumentException.class, () -> Monat.fromKey("2024"));
        assertThrows(IllegalArgumentException.class, () -> Monat.fromKey("2024-3"));
        assertThrows(IllegalArgumentException.class, () -> Monat.fromKey("2024/03"));
        assertThrows(IllegalArgumentException.class, () -> Monat.fromKey("20xx-03"));
        assertThrows(IllegalArgumentException.class, () -> Monat.fromKey("2024-0a"));
    }

    @Test
    public void toStringIstDerSchluessel() {
        assertEquals("2024-03", Monat.fromKey("2024-03").toString());
    }
}