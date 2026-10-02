package cbudgetbatch.forecastnew;

import cbudgetbatch.DBBatch;
import cbudgetbatch.gewichtung.CalculateWeights;
import cbudgetbatch.gewichtung.ForecastWriteDataToFile;
import cbudgetbatch.gewichtung.YearTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Uebergabe der Jahrestabellen an die Gewichtssuche.
 *
 * Die frueher hier fest verdrahtete Jahreszahl 2022 hat den Test im Januar
 * 2027 unbemerkt fehlschlagen lassen. Geprueft wird deshalb die Eigenschaft
 * selbst und nicht ein Kalenderjahr.
 */
public class ForecastWriteDataToFileTest {

    private DBBatch db;
    private CalculateWeights calculateWeights;
    private Calendar[] calendars;

    @BeforeEach
    public void setUp() {
        db = mock(DBBatch.class);
        calculateWeights = mock(CalculateWeights.class);

        calendars = new Calendar[8];
        for (int i = 0; i < calendars.length; i++) {
            calendars[i] = Calendar.getInstance();
            calendars[i].add(Calendar.YEAR, -i);
        }

        Map<Integer, Double> tageswerte = new HashMap<Integer, Double>();
        tageswerte.put(2, 2.0);

        when(db.getAllKategorien()).thenReturn(vektor(kategorie(1, "Elektronik", 1)));
        when(db.getAllKonto()).thenReturn(vektor(konto(1, "Sparkasse Giro")));
        when(db.getKategorienAlleSummeWhereAsMapPerDay(anyString(), anyString(), anyString()))
                .thenReturn(tageswerte);
        when(calculateWeights.calculate(any(Map.class))).thenReturn(true);
    }

    @Test
    public void reichtSiebenJahreAnDieGewichtssuche() {
        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);

        new ForecastWriteDataToFile().calculateYears(db, calendars, calculateWeights);
        verify(calculateWeights).calculate(captor.capture());

        Map<Integer, YearTable> maps = captor.getValue();
        assertEquals(7, maps.size(), "Sieben vollstaendige Jahre, das laufende gehoert nicht dazu");
        for (Map.Entry<Integer, YearTable> jahr : maps.entrySet()) {
            assertEquals(2.0, jahr.getValue().getSumOfYear(), 1e-9,
                    "Jahr " + jahr.getKey() + " traegt den Testwert");
        }
    }

    @Test
    public void reichtDieJahreInAbsteigenderReihenfolge() {
        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);

        new ForecastWriteDataToFile().calculateYears(db, calendars, calculateWeights);
        verify(calculateWeights).calculate(captor.capture());

        Map<Integer, YearTable> maps = captor.getValue();
        TreeSet<Integer> jahre = new TreeSet<Integer>(maps.keySet());
        int laufendesJahr = calendars[0].get(Calendar.YEAR);

        assertEquals(laufendesJahr - 7, jahre.first().intValue(),
                "Der Vergleich beginnt sieben Jahre zurueck");
        assertEquals(laufendesJahr - 1, jahre.last().intValue(),
                "und endet beim vorletzten Jahr");
    }

    @Test
    public void berechnetJedesPaarAusKategorieUndKonto() {
        when(db.getAllKategorien()).thenReturn(vektor(kategorie(3, "Elektronik", 1)));
        when(db.getAllKonto()).thenReturn(vektor(konto(7, "Sparkasse Giro")));

        new ForecastWriteDataToFile().calculateYears(db, calendars, calculateWeights);

        verify(calculateWeights).setCategory(3);
        verify(calculateWeights).setKonto(7);
        verify(calculateWeights).setDb(db);
    }

    @Test
    public void ueberspringtKategorienOhneForecast() {
        when(db.getAllKategorien()).thenReturn(vektor(
                kategorie(1, "Elektronik", 0),
                kategorie(2, "Miete", 1)));

        new ForecastWriteDataToFile().calculateYears(db, calendars, calculateWeights);

        verify(calculateWeights, never()).setCategory(1);
        verify(calculateWeights).setCategory(2);
        verify(calculateWeights, times(1)).calculate(any(Map.class));
    }

    @Test
    public void verarbeitetJedesKontoEinerKategorie() {
        when(db.getAllKonto()).thenReturn(vektor(
                konto(1, "Giro"),
                konto(2, "Sparkasse"),
                konto(3, "Bar")));

        new ForecastWriteDataToFile().calculateYears(db, calendars, calculateWeights);

        verify(calculateWeights, org.mockito.Mockito.times(3)).calculate(any(Map.class));
    }

    @Test
    public void ohneKategorienWirdNichtsBerechnet() {
        when(db.getAllKategorien()).thenReturn(new Vector<Object>());

        new ForecastWriteDataToFile().calculateYears(db, calendars, calculateWeights);

        verify(calculateWeights, never()).calculate(any(Map.class));
    }

    // ------------------------------------------------------------------
    //  Hilfen
    // ------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private static Vector vektor(Hashtable... eintraege) {
        Vector<Object> vector = new Vector<Object>(eintraege.length);
        for (Hashtable eintrag : eintraege) {
            vector.addElement(eintrag);
        }
        return (Vector) vector;
    }

    private static Hashtable kategorie(int id, String name, int forecast) {
        Hashtable hash = new Hashtable();
        hash.put("id", id);
        hash.put("name", name);
        hash.put("forecast", forecast);
        return hash;
    }

    private static Hashtable konto(int id, String name) {
        Hashtable hash = new Hashtable();
        hash.put("id", id);
        hash.put("name", name);
        return hash;
    }
}